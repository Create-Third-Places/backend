package app.database.utils;

import database.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnectionProvider extends ConnectionProvider {

    public Connection getDatabaseConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        System.out.println("Retrieving connection to test database");
        String url =
                "jdbc:postgresql://localhost:5432/postgres?" +
                        "user=postgres&" +
                        "password=postgres";
        return DriverManager.getConnection(url);
    }
}
