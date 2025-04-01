package app;
import io.javalin.Javalin;
import service.AuthService;
import app.data.InputRequest;
import service.BulkUpdateService;
import service.TestService;

public class Main {
    public static void main(String[] args) {
        var app = Javalin.create(config -> {
                                     config.bundledPlugins.enableCors(cors -> {
                                         cors.addRule(it -> {
                                             it.anyHost();
                                         });
                                     });
                                 })
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);

        app.get("/countLocations", ctx->{
            var testService = new TestService();
            ctx.result("" + testService.countLocations());

        });

        app.post("/admin/saveData", ctx->{
            var authService = new AuthService();

            var data = ctx.bodyAsClass(InputRequest.class);
            authService.validate(data);
            ctx.result("Test");

            var bulkUpdateService = new BulkUpdateService();
            bulkUpdateService.bulkUpdate(data.getData());
        });
    }
}