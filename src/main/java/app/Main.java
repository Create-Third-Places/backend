package app;

import app.admin.request.BulkUpdateInputRequest;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import database.search.GroupSearchParams;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;
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

          config.jsonMapper(new JavalinJackson().updateMapper(mapper->{
            mapper.registerModule(new JavaTimeModule());
          }));
        }
      )
      .get("/", ctx -> ctx.result("Hello World"))
      .start(7070);

    UsersApi.userEndpoints(app);
    GroupsApi.groupEndpoints(app);
    EventsApi.eventEndpoints(app);

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

          var sessionContext = SessionContext.createContextWithoutUser(new ConnectionProvider());
          var searchParams = GroupSearchParams.generateParameterMapFromQueryString(
            ctx
          );

          var searchService = sessionContext.createSearchService();
          var groupSearchResult = searchService.getGroups(
            searchParams
          );

          long end = System.currentTimeMillis();

          logger.info("Search time:"+((end-start)/100));
          ctx.json(groupSearchResult);
          ctx.status(HttpStatus.OK);

          logger.info("Finished search");

        } catch (Exception e) {
          e.printStackTrace();
          ctx.result("Invalid search parameter");
          ctx.status(HttpStatus.BAD_REQUEST);
        }
      }
    );

    app.get(
        "/searchLocations",
        ctx->{

          var connectionProvider = new ConnectionProvider();
          var conn = connectionProvider.getDatabaseConnection();
          GameLocationsService gameLocationsService = new GameLocationsService(conn);

          var gameLocationData = gameLocationsService.getGameLocations(LocalDate.now());
          logger.info("Retrieved game location data");

          ctx.json(gameLocationData);
          ctx.status(HttpStatus.OK);

        });

    app.get(
        "/listCities",
        ctx->{
          var connectionProvider = new ConnectionProvider();
          var conn = connectionProvider.getDatabaseConnection();

          GameLocationsService gameLocationsService = new GameLocationsService(conn);

          String areaFilter = ctx.queryParam("area");

          var cities = gameLocationsService.getAllEventLocations(areaFilter);
          logger.info("Retrieved event cities");
          ctx.json(cities);
          ctx.status(HttpStatus.OK);
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
