package database.permissions;

import app.data.auth.GroupAdminType;
import app.data.auth.User;
import app.result.error.GroupNotFoundError;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserPermissionsRepository
{
  Logger logger;
  public UserPermissionsRepository(){
    logger = LogUtils.getLogger();
  }

  public void setGroupAdmin(User userToUpdate, int groupId, Connection conn) throws Exception{
    String query = """
            UPDATE group_admin_data
              SET user_id = ?
              WHERE group_id = ?
              AND group_admin_level = cast(? as group_admin_level)
        """;

    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, userToUpdate.getId());
    insert.setInt(2, groupId);
    insert.setString(3, GroupAdminType.GROUP_ADMIN.toString());

    insert.executeUpdate();
  }

  public void addGroupModerator(User currentUser, int groupId, Connection conn) throws Exception{

    String query = """
            INSERT INTO group_admin_data (user_id, group_id, group_admin_level)
          VALUES(?, ?, cast(? as group_admin_level))
        """;

    PreparedStatement insert = conn.prepareStatement(query);
    insert.setInt(1, currentUser.getId());
    insert.setInt(2, groupId);
    insert.setString(3, GroupAdminType.GROUP_MODERATOR.toString());

    insert.executeUpdate();
  }

  public boolean canUpdateGroupAdmin(User user, int groupId, Connection conn) throws Exception {
    String query =  """
                      SELECT (group_admin_level)
                      FROM group_admin_data 
                      WHERE user_id = ?
                      AND group_id = ?
                      AND group_admin_level = cast(? as group_admin_level)
                    """;

    PreparedStatement select = conn.prepareStatement(query);
    select.setInt(1, user.getId());
    select.setInt(2, groupId);
    select.setString(3, GroupAdminType.GROUP_ADMIN.toString());

    ResultSet rs = select.executeQuery();
    return rs.next();
  }

  private ResultSet getGroupEditorRoles(int groupId, Connection conn) throws Exception {
    String query =  """
                      SELECT * from groups
                      FULL JOIN group_admin_data on group_admin_data.group_id = groups.id
                      WHERE groups.id = ?
                    """;

    PreparedStatement select = conn.prepareStatement(query);
    select.setInt(1, groupId);

    ResultSet rs = select.executeQuery();
    if(!rs.next()){
      var message = "Group "+groupId + " not found";
      logger.error(message);
      throw new GroupNotFoundError(message);
    }
    return rs;
  }

  public boolean hasGroupEditorRole(User user, int groupId, Connection conn) throws Exception {

    ResultSet rs = getGroupEditorRoles(groupId, conn);
    while(true){
      int user_id = rs.getInt("user_id");
      String groupAdminLevel = rs.getString("group_admin_level");
      if(user_id == user.getId()) {
        if(groupAdminLevel.equals(GroupAdminType.GROUP_ADMIN.toString()) ||
            groupAdminLevel.equals(GroupAdminType.GROUP_MODERATOR.toString())){
          return true;
        }
      }
      if(!rs.next()){
        return false;
      }
    }
  }

  public boolean isGroupAdmin(User user, int groupId, Connection conn) throws Exception {
    ResultSet rs = getGroupEditorRoles(groupId, conn);
    while (true) {
      int user_id = rs.getInt("user_id");
      String groupAdminLevel = rs.getString("group_admin_level");
      if (user_id == user.getId()) {
        if (groupAdminLevel.equals(GroupAdminType.GROUP_ADMIN.toString())) {
          return true;
        }
      }
      if (!rs.next()) {
        return false;
      }
    }
  }
}
