package database;

import database.utils.RepositoryUtils;
import java.sql.*;

public class TestRepository {

  public int countLocations() throws Exception {
    Connection connection = RepositoryUtils.getDatabaseConnection();
    Statement st;
    int count = 0;
    try {
      st = connection.createStatement();
      ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM locations");

      while (rs.next()) {
        count = rs.getInt(1);
      }
      connection.close();
      st.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return count;
  }
}
