package app.database.utils;

import app.data.Data;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import service.BulkUpdateService;

public class DbUtils {

  public static void createTables(Connection conn) throws Exception {
    Statement stat = conn.createStatement();
    try {
      Scanner scanner = new Scanner(
        new File("src/test/fixtures/createTables.sql")
      );
      StringBuilder stringBuilder = new StringBuilder();
      while (scanner.hasNextLine()) {
        stringBuilder.append(scanner.nextLine() + " ");
      }
      String query = stringBuilder.toString();

      System.out.println("Query:" + query);
      stat.execute(query);
      System.out.println("Created tables");
    } catch (Exception e) {
      System.out.println("Error creating tables:" + e.getMessage());
      throw e;
    }
  }

  public static void initializeData(
    TestConnectionProvider testConnectionProvider
  )
    throws Exception {
    try {
      File file = new File("src/test/fixtures/listingData.json");
      ObjectMapper mapper = new ObjectMapper();
      Data data = mapper.readValue(file, Data.class);

      BulkUpdateService bulkUpdateService = new BulkUpdateService();
      bulkUpdateService.bulkUpdate(data, testConnectionProvider);
    } catch (Exception e) {
      System.out.println("Error initializing data:" + e.getMessage());
      throw e;
    }
  }
}
