package service.permissions;

import app.data.auth.User;
import database.permissions.UserPermissionsRepository;
import database.utils.ConnectionProvider;


/*
 TODO: Consider creating a permission repository class for each user type and passing it as a constructor parameter

 */
public class GroupPermissionService {

  private UserPermissionsRepository userPermissionsRepository;

  public GroupPermissionService(){
    userPermissionsRepository = new UserPermissionsRepository();
  }

  public void setGroupAdmin(User currentUser, User userToUpdate, int groupId, ConnectionProvider connectionProvider) throws Exception{
    if(!currentUser.isSiteAdmin() && !userPermissionsRepository.canUpdateGroupAdmin(currentUser, groupId, connectionProvider.getDatabaseConnection())){
      throw new Exception("User " + currentUser.getId() + " does not have permission to edit group " + groupId);
    }
    userPermissionsRepository.setGroupAdmin(userToUpdate, groupId, connectionProvider.getDatabaseConnection());
  }

  public void addGroupModerator(User currentUser,User userToUpdate, int groupId, ConnectionProvider connectionProvider) throws Exception{
    if(!canEditGroup(currentUser, groupId, connectionProvider)){
      throw new Exception("User " + currentUser.getId() + " does not have permission to edit group " + groupId);
    }
    userPermissionsRepository.addGroupModerator(userToUpdate, groupId, connectionProvider.getDatabaseConnection());
  }

  public boolean canEditGroup(User user, int groupId, ConnectionProvider connectionProvider) throws Exception {
    if(user.isSiteAdmin()){
      return true;
    }
    return userPermissionsRepository.hasGroupEditorRole(user, groupId, connectionProvider.getDatabaseConnection());
  }
}
