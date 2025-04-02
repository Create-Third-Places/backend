package app.database.utils;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class DbUtils {

    public static void createTables(Connection conn) throws Exception {

        Statement stat = conn.createStatement();
        try {
            Scanner scanner  = new Scanner(new File("test/fixtures/createTables.sql"));
            StringBuilder stringBuilder = new StringBuilder();
            while(scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine()+ " ");
            }
            String query = stringBuilder.toString();

            System.out.println("Query:"+query);
            stat.execute(query);
            System.out.println("Created tables");
        } catch (Exception e){
            System.out.println("Error creating tables:"+e.getMessage());
            throw e;
        }
    }
}
