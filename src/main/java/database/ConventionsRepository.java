package database;

import app.data.Convention;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class ConventionsRepository {
  Logger logger;

  public ConventionsRepository() {
    logger = LogUtils.getLogger();
  }

  public void insertConventions(Convention[] conventions, Connection conn)
    throws Exception {
    EventRepository eventRepository = new EventRepository();
    EventTimeRepository eventTimeRepository = new EventTimeRepository();
    for (Convention convention : conventions) {
      logger.debug(convention.getTitle());

      int eventId = eventRepository.getEvent(
        convention.getTitle(),
        convention.getLink(),
        conn
      );
      if (eventId == -1) {
        String query =
          "INSERT INTO events (url, name, is_convention) VALUES(?, ?, true) returning id";
        PreparedStatement insert = conn.prepareStatement(query);
        insert.clearBatch();
        insert.setString(1, convention.getLink());
        insert.setString(2, convention.getTitle());
        ResultSet rs = insert.executeQuery();
        rs.next();
        convention.setId(rs.getInt(1));
      } else {
        convention.setId(eventId);
      }

      // Date values should be formated similar to the following date: 3/22/2025
      for (String date : convention.getDays()) {
        String[] data = date.split("/");

        LocalDate localDate = LocalDate.of(
          Integer.parseInt(data[2]),
          Integer.parseInt(data[0]),
          Integer.parseInt(data[1])
        );
        eventTimeRepository.setEventDate(convention.getId(), localDate, conn);
      }
    }
  }
}
