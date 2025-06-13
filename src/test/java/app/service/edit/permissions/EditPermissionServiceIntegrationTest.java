package app.service.edit.permissions;

import app.groups.data.Group;
import app.data.auth.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.utils.CreateGroupUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.permissions.GroupPermissionService;
import service.user.UserService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class EditPermissionServiceIntegrationTest {

  private static final String ADMIN_USERNAME = "unitTest";
  private static final String USERNAME_1 = "user";
  private static final String USERNAME_2 = "user2";
  private static final String USERNAME_3 = "user3";
  private static final String USERNAME_4 = "user4";

  private static UserService createUserService;
  private static GroupPermissionService groupPermissionService;
  private static IntegrationTestConnectionProvider testConnectionProvider;


  private static User admin;
  private static User user;
  private static User user2;
  private static User user3;
  private static User user4;

  @BeforeAll
  static void setup() throws Exception{
    testConnectionProvider = new IntegrationTestConnectionProvider();
    groupPermissionService = new GroupPermissionService();
    createUserService = new UserService(UserService.DataProvider.createDataProvider(testConnectionProvider.getDatabaseConnection()));
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();
      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);

      admin = createUserService.createAdmin(ADMIN_USERNAME);
      user = createUserService.createStandardUser(USERNAME_1);
      user2 = createUserService.createStandardUser(USERNAME_2);
      user3 = createUserService.createStandardUser(USERNAME_3);
      user4 = createUserService.createStandardUser(USERNAME_4);

    } catch(Exception e){
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testSiteAdmin_canEdit_groupPermissionLevelForUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(admin, testConnectionProvider);

    groupPermissionService.setGroupAdmin(admin, user, group.getId(), testConnectionProvider);
    assertTrue(groupPermissionService.canEditGroup(user, group.getId(), testConnectionProvider));
    assertFalse(groupPermissionService.canEditGroup(user2, group.getId(), testConnectionProvider));

    groupPermissionService.setGroupAdmin(admin, user2, group.getId(), testConnectionProvider);
    assertFalse(groupPermissionService.canEditGroup(user, group.getId(), testConnectionProvider));
    assertTrue(groupPermissionService.canEditGroup(user2, group.getId(), testConnectionProvider));
  }

  @Test
  public void testGroupAdmin_canEdit_groupPermissionLevelForUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(user, testConnectionProvider);

    groupPermissionService.addGroupModerator(user, user2, group.getId(), testConnectionProvider);
    assertTrue(groupPermissionService.canEditGroup(user, group.getId(), testConnectionProvider));
    assertTrue(groupPermissionService.canEditGroup(user2, group.getId(), testConnectionProvider));
  }

  @Test
  public void testGroupAdmin_cannotEdit_groupPermissionLevelForDifferentGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(user, testConnectionProvider);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupPermissionService.addGroupModerator(user2, user2, group.getId(), testConnectionProvider);

        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupModerator_canEdit_groupPermissionLevelForUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(user, testConnectionProvider);

    groupPermissionService.addGroupModerator(user, user2, group.getId(), testConnectionProvider);
    groupPermissionService.addGroupModerator(user2, user3, group.getId(), testConnectionProvider);

    assertTrue(groupPermissionService.canEditGroup(user2, group.getId(), testConnectionProvider));
    assertTrue(groupPermissionService.canEditGroup(user3, group.getId(), testConnectionProvider));
  }

  @Test
  public void testGroupModerator_cannotEdit_groupPermissionLevelForGroupAdmin() throws Exception{
    Group group = CreateGroupUtils.createGroup(user, testConnectionProvider);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupPermissionService.addGroupModerator(user, user2, group.getId(), testConnectionProvider);
          groupPermissionService.setGroupAdmin(user2, user2, group.getId(), testConnectionProvider);

        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupModerator_cannotEdit_userPermissionLevelForDifferentGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(user, testConnectionProvider);
    Group group2 = CreateGroupUtils.createGroup(user3, testConnectionProvider);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupPermissionService.addGroupModerator(user, user4, group2.getId(), testConnectionProvider);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }
}
