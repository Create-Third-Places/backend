package app.service.auth;

import app.data.auth.User;
import app.data.auth.UserType;
import app.database.utils.DbUtils;
import app.database.utils.IntegrationTestConnectionProvider;
import app.result.error.DuplicateUsernameException;
import app.users.data.RegisterUserRequest;
import app.utils.CreateUserUtils;
import io.javalin.http.Context;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.auth.AuthService;
import service.user.UserService;

import java.sql.Connection;


import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

public class RegisterUserIntegrationTest {


  private static IntegrationTestConnectionProvider testConnectionProvider;

  private static AuthService authService;
  private static AuthService authServiceWithError;

  private static UserService userService;

  private static Connection conn;
  private static Connection transactionConn;
  @BeforeAll
  static void setup() {
    testConnectionProvider = new IntegrationTestConnectionProvider();
    try {
      conn = testConnectionProvider.getDatabaseConnection();

      transactionConn = testConnectionProvider.getConnectionWithManualCommit();

      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);

      userService = new UserService(UserService.DataProvider.createDataProvider(transactionConn));

      authServiceWithError = new AuthService(new MockAuthProviderInvalidToken(), userService);
      authService = new AuthService(new NoErrorMockAuthProvider(), userService);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @AfterAll
  static void cleanup() throws Exception{
    conn.close();
    transactionConn.close();
  }


  @Test
  public void registerWithDuplicateUsername_throwsError_withNoDatabaseUpdate() throws Exception{

    User user = CreateUserUtils.createUserObject(UserType.USER);
    userService.createStandardUser(user.getEmail());

    int userCount1 = userService.countUsers();

    RegisterUserRequest request = RegisterUserRequest.createRequest(user.getEmail(), "1234");
    Exception exception = assertThrows(
        Exception.class,
        ()->{
          authService.registerUser(request, UserType.USER);
        }
    );

    assertEquals(exception.getMessage(), "Username: " +user.getEmail()+ " already exists");
    assertTrue(exception instanceof DuplicateUsernameException);

    int userCount2 = userService.countUsers();
    assertEquals(userCount1, userCount2);
  }

  @Test
  public void registerWithValidUsernameAndPassword_createsInactiveUser() throws Exception{

    User user = CreateUserUtils.createUserObject(UserType.USER);

    RegisterUserRequest request =  RegisterUserRequest.createRequest(user.getEmail(), "1234");

    var registerUserInfo = authService.registerUser(request, UserType.USER);

    User createdUser = userService.getUser(user.getEmail());
    assertEquals(registerUserInfo.getEmail(), user.getEmail());
    assertEquals(user.getEmail(), createdUser.getEmail());
  }

  @Test
  public void registerUser_authenticationError_userNotCreated() throws Exception {
    User user = CreateUserUtils.createUserObject(UserType.USER);

    RegisterUserRequest request =  RegisterUserRequest.createRequest(user.getEmail(), "1234");
    Exception exception = assertThrows(
        Exception.class,
        ()->{
          authServiceWithError.registerUser(request, UserType.USER);
        }
    );
    assertTrue(exception.getMessage().contains("Failed to create user"), exception.getMessage());
    assertTrue(exception instanceof AuthService.RegisterUserException,exception.getClass().getName());
    User savedUser = userService.getUser(user.getEmail());
    assertNull(savedUser);
  }


  @Test
  public void testInactiveUserCannotAuthenticate() throws Exception{

    User user = CreateUserUtils.createUserObject(UserType.USER);
    RegisterUserRequest request =  RegisterUserRequest.createRequest(user.getEmail(), "1234");

    authService.registerUser(request, UserType.USER);

    Context context = mock(Context.class);
    when(context.header("authToken")).thenReturn(user.getEmail());
    User readOnly = authService.getUser(context);

    assertEquals(readOnly.getEmail(),AuthService.getReadOnlyUser().getEmail());
    assertEquals(readOnly.getAdminLevel(),UserType.READONLY.name());
  }

  @Test
  public void testActiveUsersHasCorrectUserType() throws Exception{

    User user = CreateUserUtils.createUserObject(UserType.USER);
    User user2 = CreateUserUtils.createUserObject(UserType.TESTER);

    RegisterUserRequest request =  RegisterUserRequest.createRequest(user.getEmail(), "1234");
    RegisterUserRequest request2 =  RegisterUserRequest.createRequest(user2.getEmail(), "1234");

    authService.registerUser(request, UserType.USER);
    authService.registerUser(request2, UserType.TESTER);

    User savedUser = userService.getUser(user.getEmail());
    User savedUser2 = userService.getUser(user2.getEmail());

    assertEquals(user.getEmail(), savedUser.getEmail());
    assertEquals(user.getAdminLevel(), savedUser.getAdminLevel());

    assertEquals(user2.getEmail(), savedUser2.getEmail());
    assertEquals(user2.getAdminLevel(), savedUser2.getAdminLevel());
  }

  @Test
  public void testActiveUserCanAuthenticate() throws Exception{
    User user = CreateUserUtils.createUserObject(UserType.USER);
    RegisterUserRequest request =  RegisterUserRequest.createRequest(user.getEmail(), "1234");

    authService.registerUser(request, UserType.USER);
    userService.activateUser(user.getEmail());

    Context context = mock(Context.class);
    when(context.header("authToken")).thenReturn(user.getEmail());

    User authenticatedUser = authService.getUser(context);
    assertEquals(user.getEmail(), authenticatedUser.getEmail());
    assertEquals(user.getAdminLevel(), authenticatedUser.getAdminLevel());

  }

}
