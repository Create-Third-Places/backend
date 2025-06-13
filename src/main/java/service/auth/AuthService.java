package service.auth;

import app.data.auth.User;
import app.data.auth.UserType;
import app.request.BulkUpdateInputRequest;
import app.result.error.DuplicateUsernameException;
import app.users.data.RegisterUserRequest;
import app.users.data.RegisterUserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import database.utils.ConnectionProvider;
import io.javalin.http.Context;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.Logger;
import service.auth.supabase.SupabaseAuthProvider;
import service.data.AuthRequest;
import service.user.UserService;
import utils.LogUtils;
import utils.Params;

import java.sql.Connection;
import java.util.Optional;

public class AuthService {

  private AuthProvider authProvider;
  private UserService userService;
  private static final Logger logger = LogUtils.getLogger();

  public AuthService(AuthProvider authProvider, UserService userService){
    this.authProvider = authProvider;
    this.userService = userService;
  }

  public class RegisterUserException extends RuntimeException {
    public RegisterUserException(String message) {
      super(message);
    }
  }

  public class RegisterUserInvalidDataException extends RuntimeException {
    public RegisterUserInvalidDataException(String message) {
      super(message);
    }
  }

  public RegisterUserResponse registerUser(RegisterUserRequest request, UserType userType) throws Exception{

    validateRegisterUserRequest(request);
    if(userService.userExists(request.getEmail())) {
      throw new DuplicateUsernameException("Username: " + request.getEmail() + " already exists");
    }

    if(userType.equals(UserType.USER)) {
      userService.createStandardUser(request.getEmail());
    } else if(userType.equals(UserType.SITE_ADMIN)) {
      userService.createAdmin(request.getEmail());
    } else if(userType.equals(UserType.TESTER)) {
      userService.createTester(request.getEmail());
    } else {
      throw new Exception("Cannot create user with type:"+userType.toString());
    }

    try {
      var registerUserResponse = authProvider.registerUser(request);
      userService.commitChanges();
      logger.info("Created user with username:"+request.getEmail());
      return registerUserResponse;
    } catch (Exception e) {
      userService.rollbackChanges();
      e.printStackTrace();
      throw new RegisterUserException("Failed to create user due to error:"+e.getMessage());
    }
  }

  private void validateRegisterUserRequest(RegisterUserRequest request){
    if(request.getEmail() == null ||request.getEmail().isBlank()){
      throw new RegisterUserInvalidDataException("Invalid email");
    }
    if(request.getPassword() == null ||request.getPassword().isBlank()){
      throw new RegisterUserInvalidDataException("Invalid password");
    }
  }
  /**
   *
   * @return Returns currently logged in user, or read only user if the user is not logged in.
   */
  public User getUser(Context ctx) throws Exception{
    logger.info("Retrieving current user");

    Optional<String> username =  authProvider.getUsernameFromToken(ctx.header("authToken"));

    if(!username.isPresent()){
      return getReadOnlyUser();
    }

    User user = userService.getActiveUser(username.get());
    if(user == null){
      return AuthService.getReadOnlyUser();
    }
    return user;
  }


  /**
   *
   * @return Returns read only user
   */
  public static User getReadOnlyUser(){
    User user = new User("reader@dmvboardgames.com", UserType.READONLY, 123);
    return user;
  }


  /**
   * Validate bulk update request used for internet testing
   * @param request
   * @return
   * @throws Exception
   */
  public boolean validateBulkUpdateInputRequest(BulkUpdateInputRequest request) throws Exception {
    final HttpPost httpPost = new HttpPost(Params.getSupabasePasswordCheckUrl());
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("apikey", Params.getSupabaseApiKey());
    final HttpClient httpClient = HttpClients.createDefault();

    AuthRequest authRequest = new AuthRequest();

    authRequest.setPassword(request.getPassword());
    authRequest.setEmail(request.getEmail());

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(authRequest);

    HttpEntity stringEntity = new StringEntity(
      json,
      ContentType.APPLICATION_JSON
    );
    httpPost.setEntity(stringEntity);

    try {
      HttpResponse rawResponse = httpClient.execute(httpPost);

      final int statusCode = rawResponse.getCode();

      if (statusCode != 200) {
        logger.debug(rawResponse);

        throw new Exception(
          "Authorization failed with status code:" + statusCode
        );
      }
    } catch (Exception e) {
      logger.error("Authorization failed with error", e);
      throw (e);
    }
    logger.info("Authorized");
    return true;
  }

  //TODO: Consider moving this function and the one below it outside of AuthService.
  public static User getUser(Connection conn, Context ctx) throws Exception{

    UserService userService = new UserService(UserService.DataProvider.createDataProvider(conn));
    SupabaseAuthProvider supabaseAuthProvider = new SupabaseAuthProvider();

    AuthService authService = new AuthService(supabaseAuthProvider, userService);
    return authService.getUser(ctx);
  }

  public static RegisterUserResponse registerUser(Context ctx) throws Exception{
    var connectionProvider = new ConnectionProvider();

    UserService userService = new UserService(UserService.DataProvider.createDataProvider(connectionProvider.getConnectionWithManualCommit()));
    SupabaseAuthProvider supabaseAuthProvider = new SupabaseAuthProvider();

    AuthService authService = new AuthService(supabaseAuthProvider, userService);

    var data = ctx.bodyAsClass(RegisterUserRequest.class);
    return authService.registerUser(data, UserType.USER);
  }

}
