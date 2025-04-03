package database.search;

import app.result.GroupSearchResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SearchRepository {

  public GroupSearchResult getGroups(
    GroupSearchParams searchParams,
    Connection conn
  )
    throws Exception {
    PreparedStatement statement = searchParams.generateSearchQuery(conn);
    ResultSet rs = statement.executeQuery();

    GroupSearchResult searchResult = new GroupSearchResult();
    while (rs.next()) {
      Integer groupId = rs.getInt("groupId");

      String groupName = rs.getString("name");
      String url = rs.getString("url");
      String groupSummary = rs.getString("summary");
      searchResult.addGroup(groupId, groupName, url, groupSummary);

      Integer eventId = rs.getInt("eventId");
      String eventName = rs.getString("eventname");
      String description = rs.getString("description");
      String dayOfWeek = rs.getString("day_of_week");

      String streetAddress = rs.getString("street_address");
      String city = rs.getString("city");
      String state = rs.getString("state");
      String zipCode = rs.getString("zip_code");

      if(eventId != 0) {
        String address =
            streetAddress + ", " + city + ", " + state + " " + zipCode;
        searchResult.addEvent(
            groupId,
            eventId,
            eventName,
            description,
            dayOfWeek,
            address

        );
      }
    }
    return searchResult;
  }
}
