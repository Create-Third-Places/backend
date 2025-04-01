package database;

import app.data.Event;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PSQLException;

public class EventTimeRepository {
  private static final Logger logger = LogManager.getLogger(
    EventTimeRepository.class
  );

  public void setEventDay(Event event, Connection conn) throws Exception {
    if (!hasEventDay(event, conn)) {
      String day = event.getDay();
      String query =
        "INSERT into event_time (day_of_week, event_id) VALUES(cast(? AS dayofweek), ?)";
      PreparedStatement insert = conn.prepareStatement(query);
      insert.setString(1, day);
      insert.setInt(2, event.getId());
      insert.executeUpdate();
    }
  }

  public boolean hasEventDay(Event event, Connection conn) throws Exception {
    try {
      String day = event.getDay();
      String query =
        "SELECT * from event_time where day_of_week = cast(? AS dayofweek) AND event_id = ?";
      PreparedStatement select = conn.prepareStatement(query);
      select.setString(1, day);
      select.setInt(2, event.getId());
      ResultSet rs = select.executeQuery();

      return rs.next();
    } catch (PSQLException e) {
      logger.error("Query error in hasEventDay");
      throw e;
    }
  }

  public void setEventDate(int eventId, LocalDate date, Connection conn)
    throws Exception {
    if (!hasEventDate(eventId, date, conn)) {
      String day = date.getDayOfWeek().toString();
      String query =
        "INSERT into event_time (day_of_week, event_id, start_time) VALUES(cast(? AS dayofweek), ?, ?)";
      PreparedStatement insert = conn.prepareStatement(query);
      insert.setString(1, day.toLowerCase());
      insert.setInt(2, eventId);
      insert.setTimestamp(3, Timestamp.valueOf(date.atStartOfDay()));
      insert.executeUpdate();
    }
  }

  public boolean hasEventDate(int eventId, LocalDate date, Connection conn)
    throws Exception {
    String query =
      "SELECT * from event_time where day_of_week = cast(? AS dayofweek) and event_id = ? and start_time = ?";
    PreparedStatement select = conn.prepareStatement(query);
    select.setString(1, date.getDayOfWeek().toString().toLowerCase());
    select.setInt(2, eventId);
    select.setTimestamp(3, Timestamp.valueOf(date.atStartOfDay()));
    ResultSet rs = select.executeQuery();

    return rs.next();
  }
}
