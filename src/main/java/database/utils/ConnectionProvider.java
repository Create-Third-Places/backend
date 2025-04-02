package database.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class ConnectionProvider {
  private static Logger logger = LogUtils.getLogger();

  public Connection getDatabaseConnection() throws Exception {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    logger.info("Loaded database Driver");
    String url =
      "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres?" +
      "user=postgres.karqyskuudnvfxohwkok&" +
      "password=" +
      System.getenv("DB_PASSWORD");
    Connection connection = DriverManager.getConnection(url);
    return connection;
  }
}
