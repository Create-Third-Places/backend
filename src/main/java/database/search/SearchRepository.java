package database.search;

import app.result.GroupSearchResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;

public class SearchRepository {
    public GroupSearchResult getGroups(GroupSearchParams searchParams, Connection conn) throws Exception{

        PreparedStatement statement = searchParams.getSearchQuery(conn);
        ResultSet rs = statement.executeQuery();

        GroupSearchResult searchResult = new GroupSearchResult();
        while(rs.next()){
            Integer groupId = rs.getInt("id");

            String groupName = rs.getString("name");
            String url = rs.getString("url");
            String groupSummary = rs.getString("summary");
            searchResult.addGroup(groupId, groupName, url, groupSummary);

            Integer eventId = rs.getInt("eventId");
            String eventName = rs.getString("eventname");
            String description = rs.getString("description");
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(rs.getString("day_of_week"));

            String streetAddress = rs.getString("street_address");
            String city = rs.getString("city");
            String state = rs.getString("state");
            String zipCode = rs.getString("zip_code");

            String address = streetAddress + ", " + city + ", " + state + " " + zipCode;
            searchResult.addEvent(
                    groupId,
                    eventId,
                    eventName,
                    description,
                    dayOfWeek,
                    address
            );
        }
        return searchResult;

        //TODO
        /*
            Run unit tests to make sure default test case works.
            Make sure search works with filters.
            Create API endpoint.

         */
    }
}
