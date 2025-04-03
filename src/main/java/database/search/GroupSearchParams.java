package database.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class GroupSearchParams {
    private HashMap<String, String> params;
    public GroupSearchParams() {
        params = new HashMap<String, String>();
    }

    public void setParams(String key, String value){
        params.put(key, value);
    }

    public PreparedStatement getSearchQuery(Connection conn) throws Exception{

        String query = getQueryForAllResults();

        PreparedStatement select = conn.prepareStatement(query);

        /**
         * Determine query that needs to be returned based on parameters.
         */
        return select;
    }

    private static String getQueryForAllResults() {
        String query = """
                SELECT
                  groups.id,
                  groups.name,
                  groups.url,
                  groups.summary,
                  events.name as eventName,
                  events.description,
                  event_time.day_of_week,
                  locations.city,
                  locations.state,
                  locations.street_address,
                  locations.zip_code
                FROM groups
                JOIN event_group_map on event_group_map.group_id = groups.id
                JOIN events on event_group_map.event_id = events.id
                JOIN event_time on event_time.event_id = events.id
                JOIN locations on events.location_id = locations.id
                """;
        return query;
    }

}
