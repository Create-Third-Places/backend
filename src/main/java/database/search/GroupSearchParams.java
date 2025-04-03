package database.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GroupSearchParams {
  public static final String DAY_OF_WEEK = "day_of_week";
  public static final String LOCATION = "location";

  private final LinkedHashMap<String, String> params;

  private static final HashMap<String, String> paramQueryMap;
  static {
    paramQueryMap = new HashMap<String,String>();
    paramQueryMap.put(DAY_OF_WEEK,"WHERE day_of_week = cast(? AS dayofweek)");
    paramQueryMap.put(LOCATION, "WHERE COALESCE(locs.city, locations.city) = ?");
  }




  public GroupSearchParams(LinkedHashMap<String, String> params) {
    this.params = params;
  }

  public PreparedStatement generateSearchQuery(Connection conn) throws Exception {
    String query = getQueryForAllResults();

    ArrayList<String> whereClauses = new ArrayList<>();

    for(String param: params.keySet()){
      whereClauses.add(paramQueryMap.get(param));
    }

    query = query + String.join( " AND ", whereClauses.toArray(new String[0]));

    PreparedStatement select = conn.prepareStatement(query);

    int i = 1;
    for(String param: params.keySet()){
      select.setString(i, params.get(param));
      i++;
    }
    return select;
  }

  private static String getQueryForAllResults() {
      String query = """
           SELECT
                   DISTINCT ON (events.id, groups.id)
                    events.id as eventId,
                    groups.id as groupId,
                    groups.name,
                    groups.url,
                    groups.summary,
                    events.name as eventName,
                    events.description,
                    event_time.day_of_week,
                    locations.state,
                    locations.street_address,
                    locations.zip_code,
                    COALESCE(locs.city, locations.city) as city
                  FROM groups
                  LEFT JOIN event_group_map on groups.id = event_group_map.group_id
                  LEFT JOIN  events on event_group_map.event_id = events.id
                  LEFT JOIN  event_time on event_time.event_id = events.id
                  LEFT JOIN  locations on events.location_id = locations.id
                  LEFT JOIN location_group_map on groups.id = location_group_map.group_id
                  LEFT JOIN locations as locs on location_group_map.location_id = locs.id
          
        """;
    return query;
  }
}
