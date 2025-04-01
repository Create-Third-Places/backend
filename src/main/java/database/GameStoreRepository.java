package database;

import app.data.GameStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class GameStoreRepository {
  Logger logger;

  public GameStoreRepository() {
    logger = LogUtils.getLogger();
  }

  public void insertGameStores(GameStore[] gameStores, Connection conn)
    throws Exception {
    LocationsRepository locationRepository = new LocationsRepository();

    for (GameStore gameStore : gameStores) {
      if (!gameStoreExists(gameStore, conn)) {
        int location_id = locationRepository.insertLocation(
          gameStore.getLocation(),
          conn
        );
        logger.debug("Inserting game store:" + gameStore.getName());
        String query =
          "INSERT INTO game_stores (url, name, location_id) VALUES(?, ?, ?)";
        PreparedStatement insert = conn.prepareStatement(query);
        insert.clearBatch();
        insert.setString(1, gameStore.getLink());
        insert.setString(2, gameStore.getName());
        insert.setInt(3, location_id);
        insert.executeUpdate();
      }
    }
  }

  public boolean gameStoreExists(GameStore gameStore, Connection conn)
    throws Exception {
    String query = "SELECT * from game_stores where url = ?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, gameStore.getLink());

    ResultSet rs = select.executeQuery();
    return rs.next();
  }
}
