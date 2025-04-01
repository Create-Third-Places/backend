package database;

import app.data.GameRestaurant;
import app.data.GameStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class GameRestaurantRepository {
  Logger logger;

  public GameRestaurantRepository() {
    logger = LogUtils.getLogger();
  }

  public void insertGameRestaurants(
    GameRestaurant[] gameRestaurants,
    Connection conn
  )
    throws Exception {
    LocationsRepository locationRepository = new LocationsRepository();

    for (GameRestaurant gameRestaurant : gameRestaurants) {
      int location_id = locationRepository.insertLocation(
        gameRestaurant.getLocation(),
        conn
      );

      if (!hasGameRestaurant(gameRestaurant, location_id, conn)) {
        logger.debug(gameRestaurant.getName());
        String query =
          "INSERT INTO game_restaurants (url, name, location_id) VALUES(?, ?, ?)";
        PreparedStatement insert = conn.prepareStatement(query);
        insert.clearBatch();
        insert.setString(1, gameRestaurant.getLink());
        insert.setString(2, gameRestaurant.getName());
        insert.setInt(3, location_id);
        insert.executeUpdate();
      }
    }
  }

  public boolean hasGameRestaurant(
    GameRestaurant gameRestaurant,
    int locationId,
    Connection conn
  )
    throws Exception {
    String query =
      "SELECT * from game_restaurants where name = ? and url=? and location_id=?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, gameRestaurant.getName());
    select.setString(2, gameRestaurant.getLink());
    select.setInt(3, locationId);

    ResultSet rs = select.executeQuery();
    return rs.next();
  }
}
