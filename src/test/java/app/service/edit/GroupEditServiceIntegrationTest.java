package app.service.edit;

import app.groups.data.Group;
import app.data.auth.User;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.utils.CreateGroupUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.auth.AuthService;
import service.permissions.GroupPermissionService;
import service.provider.ReadGroupDataProvider;
import service.read.ReadGroupService;
import service.update.GroupEditService;
import service.user.UserService;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GroupEditServiceIntegrationTest {

  private static final String ADMIN_USERNAME = "unitTest";
  private static final String USERNAME_2 = "user";
  private static final String USERNAME_3 = "user2@test";
  private static final String USERNAME_4 = "user3@test";

  private static User admin;
  private static User standardUser;
  private static User standardUser2;
  private static User standardUser3;


  private static UserService createUserService;
  private static GroupEditService groupEditService;
  private static ReadGroupService readGroupService;
  private static GroupPermissionService groupPermissionService;

  private static IntegrationTestConnectionProvider testConnectionProvider;


  private static void assertGroupsAreEqual(Group group1, Group group2){
    assertEquals(group1.id, group2.id);
    assertEquals(group1.url, group2.url);
    assertEquals(group1.name, group2.name);
    assertEquals(group1.summary, group2.summary);

  }

  @BeforeAll
  static void setup() throws Exception{
    testConnectionProvider = new IntegrationTestConnectionProvider();
    groupEditService = new GroupEditService();
    createUserService = new UserService(UserService.DataProvider.createDataProvider(testConnectionProvider.getDatabaseConnection()));
    groupPermissionService = new GroupPermissionService();

    ReadGroupDataProvider dataProvider = ReadGroupDataProvider.create();
    readGroupService = new ReadGroupService(dataProvider);
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();
      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);

      admin = createUserService.createAdmin(ADMIN_USERNAME);
      standardUser = createUserService.createStandardUser(USERNAME_2);
      standardUser2 = createUserService.createStandardUser(USERNAME_3);
      standardUser3 = createUserService.createStandardUser(USERNAME_4);

    } catch(Exception e){
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void testUserCannotEditGroup_whenNotLoggedIn() throws Exception {
    Group group = CreateGroupUtils.createGroup(admin, testConnectionProvider);

    User readOnlyUser = AuthService.getReadOnlyUser();

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupEditService.editGroup(readOnlyUser, updated, testConnectionProvider);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }


  @Test
  public void testUserCannotEditGroup_whenTheyAreStandardUser() throws Exception{
    Group group = CreateGroupUtils.createGroup(admin, testConnectionProvider);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupEditService.editGroup(standardUser, updated, testConnectionProvider);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testSiteAdminCanEditGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());
    groupEditService.editGroup(admin, updated, testConnectionProvider);

    Optional<Group> updatedFromDb = readGroupService.getGroup(group.getId(), testConnectionProvider);

    assertGroupsAreEqual(updatedFromDb.orElseThrow(), updated);
  }


  @Test
  public void testGroupAdminCanEditGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());
    groupEditService.editGroup(standardUser, updated, testConnectionProvider);

    Optional<Group> updatedFromDb = readGroupService.getGroup(group.getId(), testConnectionProvider);
    assertGroupsAreEqual(updatedFromDb.orElseThrow(), updated);
  }

  @Test
  public void testUserCannotEditGroupThatDoesNotExist() throws Exception{
    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId((int)(Math.random()*999999));
    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupEditService.editGroup(admin, updated, testConnectionProvider);
        }
    );
    assertTrue(exception.getMessage().contains("not found"));

  }

  @Test
  public void testGroupAdminCannotEditGroup_whenTheyAreNotAdminOfThatGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);

    CreateGroupUtils.createGroup(standardUser2, testConnectionProvider);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());


    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupEditService.editGroup(standardUser2, updated, testConnectionProvider);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupModeratorCanEditGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);

    groupPermissionService.addGroupModerator(standardUser, standardUser2, group.getId(), testConnectionProvider);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group.getId());
    groupEditService.editGroup(standardUser2, updated, testConnectionProvider );

    Optional<Group> updatedFromDb  = readGroupService.getGroup(group.getId(), testConnectionProvider);
    assertGroupsAreEqual(updatedFromDb.orElseThrow(), updated);
  }

  @Test
  public void testGroupModeratorCannotEditGroup_whenTheyAreNotAdminOfThatGroup() throws Exception{
    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);
    Group group2 = CreateGroupUtils.createGroup(standardUser2, testConnectionProvider);

    groupPermissionService.addGroupModerator(standardUser, standardUser3, group.getId(), testConnectionProvider);

    Group updated = CreateGroupUtils.createGroupObject();
    updated.setId(group2.getId());

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupEditService.editGroup(standardUser3, updated, testConnectionProvider);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }

  @Test
  public void testGroupAdminCanDeleteGroup() throws Exception {

    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);

    groupEditService.deleteGroup(standardUser, group.getId(), testConnectionProvider);

    Optional<Group> groupInDb = readGroupService.getGroup(group.getId(),testConnectionProvider);
    assertTrue(groupInDb.isEmpty());
  }

  @Test
  public void testStandardUserCannotDeleteGroup() throws Exception {

    Group group = CreateGroupUtils.createGroup(standardUser, testConnectionProvider);

    Exception exception = assertThrows(
        Exception.class,
        ()->{
          groupEditService.deleteGroup(standardUser2, group.getId(), testConnectionProvider);
        }
    );
    assertTrue(exception.getMessage().contains("does not have permission"));
  }
}
