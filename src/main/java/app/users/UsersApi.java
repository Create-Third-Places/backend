package app.users;

import app.data.auth.UserType;
import app.users.data.RegisterUserRequest;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import org.apache.logging.log4j.Logger;
import service.auth.AuthService;
import utils.LogUtils;

public class UsersApi {
  public static Logger logger = LogUtils.getLogger();

  public static void createEndpoints(Javalin app){
    app.post(
        "/users/register",
        ctx -> {

          try {

            var response = AuthService.registerUser(ctx);

            logger.info("User created successfully");
            ctx.status(200);
            ctx.json(response);
          } catch(AuthService.RegisterUserInvalidDataException e){
            ctx.status(400);
            ctx.result(e.getMessage());
          }
          catch(MismatchedInputException e){
            if(e.getMessage().contains("app.user.data.RegisterUserRequest")){
              ctx.result(ctx.body() + " is not valid input for the POST /users/register endpoint");
              ctx.status(400);
            } else {
              ctx.result(e.getMessage());
              ctx.status(500);
            }
          }catch (Exception e){
            e.printStackTrace();
            ctx.result(e.getMessage());
            ctx.status(500);
          }
        }
    );
  }
}
