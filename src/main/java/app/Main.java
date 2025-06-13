package app;

import app.groups.GroupsApi;
import app.request.BulkUpdateInputRequest;
import app.users.UsersApi;
import database.search.GroupSearchParams;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import org.apache.logging.log4j.Logger;
import service.*;
import service.auth.AuthService;
import service.auth.supabase.SupabaseAuthProvider;
import service.read.GameLocationsService;
import service.read.SearchService;
import service.user.UserService;
import utils.LogUtils;

import java.time.LocalDate;

public class Main {

  public static Logger logger = LogUtils.getLogger();
  public static void main(String[] args) {
    var app = Javalin
      .create(
        config -> {
          config.bundledPlugins.enableCors(
            cors -> {
              cors.addRule(
                it -> {
                  it.anyHost();
                }
              );
            }
          );
        }
      )
      .get("/", ctx -> ctx.result("Hello World"))
      .start(7070);

    UsersApi.createEndpoints(app);
    GroupsApi.createEndpoints(app);
    app.get(
      "/countLocations",
      ctx -> {
        var testService = new TestService();
        ctx.result("Number of locations:" + testService.countLocations());
      }
    );

    app.get(
      "/searchEvents",
      ctx -> {

        long start = System.currentTimeMillis();
        try {
          var connectionProvider = new ConnectionProvider();
          var searchParams = GroupSearchParams.generateParameterMapFromQueryString(
            ctx
          );

          var searchService = new SearchService();

          var groupSearchResult = searchService.getGroups(
            searchParams,
            connectionProvider
          );

          long end = System.currentTimeMillis();

          logger.info("Search time:"+((end-start)/100));
          ctx.json(groupSearchResult);
          ctx.status(200);

          logger.info("Finished search");

        } catch (Exception e) {
          e.printStackTrace();
          ctx.result("Invalid search parameter");
          ctx.status(400);
        }
      }
    );

    app.get(
        "/searchLocations",
        ctx->{

          var connectionProvider = new ConnectionProvider();
          GameLocationsService gameLocationsService = new GameLocationsService();

          var gameLocationData = gameLocationsService.getGameLocations(connectionProvider, LocalDate.now());
          logger.info("Retrieved game location data");

          ctx.json(gameLocationData);
          ctx.status(200);

        });

    app.get(
        "/listCities",
        ctx->{
          var connectionProvider = new ConnectionProvider();
          GameLocationsService gameLocationsService = new GameLocationsService();

          String areaFilter = ctx.queryParam("area");

          var cities = gameLocationsService.getAllEventLocations(connectionProvider, areaFilter);
          logger.info("Retrieved event cities");
          ctx.json(cities);
          ctx.status(200);
        });

    //TODO: Consider deleting this endpoint.
    app.post(
      "/admin/saveData",
      ctx -> {

        var connectionProvider = new ConnectionProvider();
        UserService userService = new UserService(UserService.DataProvider.createDataProvider(connectionProvider.getDatabaseConnection()));
        SupabaseAuthProvider supabaseAuthProvider = new SupabaseAuthProvider();

        AuthService authService = new AuthService(supabaseAuthProvider, userService);
        var data = ctx.bodyAsClass(BulkUpdateInputRequest.class);
        authService.validateBulkUpdateInputRequest(data);
        ctx.result("Saved data");

        var bulkUpdateService = new BulkUpdateService();
        bulkUpdateService.bulkUpdate(data.getData(), connectionProvider);
      }
    );
  }
}
