package utils;

import database.GameStoreRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtils {

  public static Logger getLogger() {
    return LogManager.getLogger(GameStoreRepository.class);
  }
}
