package service;

import app.data.Data;
import database.*;
import database.utils.ConnectionProvider;
import java.sql.Connection;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class BulkUpdateService {
  Logger logger;

  public BulkUpdateService() {
    logger = LogUtils.getLogger();
  }

  public void bulkUpdate(Data data, ConnectionProvider connectionProvider)
    throws Exception {
    Connection conn = connectionProvider.getDatabaseConnection();
    conn.setAutoCommit(false);

    try {
      logger.info("Number of groups to insert:"+data.getGroups().length);
      GroupsRepository groupsRepository = new GroupsRepository();
      groupsRepository.insertGroups(data.getGroups(), conn);
    } catch (Exception e) {
      logger.error("Error inserting groups");
      throw e;
    }

    try {
      ConventionsRepository conventionsRepository = new ConventionsRepository();
      conventionsRepository.insertConventions(data.getConventions(), conn);
    } catch (Exception e) {
      logger.error("Error inserting conventions");
      throw e;
    }

    try {
      GameStoreRepository gameStoreRepository = new GameStoreRepository();
      gameStoreRepository.insertGameStores(data.getGameStores(), conn);
    } catch (Exception e) {
      logger.error("Error inserting game stores");
      throw e;
    }

    try {
      GameRestaurantRepository gameRestaurantRepository = new GameRestaurantRepository();
      gameRestaurantRepository.insertGameRestaurants(
        data.getGameRestaurants(),
        conn
      );
    } catch (Exception e) {
      logger.error("Error inserting game restaurants");
      throw e;
    }

    try {
      EventRepository eventRepository = new EventRepository();
      eventRepository.addEvents(data.getGroups(), conn);
    } catch (Exception e) {
      logger.error("Error inserting events");
      throw e;
    }

    conn.commit();
    conn.close();

    logger.info("Done with bulk update");
  }
}
