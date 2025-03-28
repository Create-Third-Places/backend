package database;

import app.data.Event;
import app.data.Group;
import service.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EventRepository {

    public void addEvents(Group[] groups, Connection conn) throws Exception {

        GroupsRepository groupsRepository = new GroupsRepository();
        LocationsRepository locationsRepository = new LocationsRepository();
        EventTimeRepository eventTimeRepository = new EventTimeRepository();

        for(Group group: groups) {
            int groupId = groupsRepository.getGroupId(group, conn);
            for(Event event: group.events){

                int eventId = getEvent(event.getTitle(),  group.link,conn);
                if(eventId == -1) {
                    if (!Validator.isValidAddress(event.getLocation())){
                        String query = "INSERT INTO events(description, name, url) values(?,?,?) returning id";
                        PreparedStatement insert = conn.prepareStatement(query);
                        insert.setString(1, event.getSummary());
                        insert.setString(2, event.getTitle());
                        insert.setString(3, group.link);

                        ResultSet rs = insert.executeQuery();
                        rs.next();

                        eventId = rs.getInt(1);

                    } else {
                        int location_id = locationsRepository.insertLocation(event.getLocation(), conn);
                        String query = "INSERT INTO events(location_id, description, name, url) values(?,?,?,?) returning id";
                        PreparedStatement insert = conn.prepareStatement(query);
                        insert.setInt(1, location_id);
                        insert.setString(2, event.getSummary());
                        insert.setString(3, event.getTitle());
                        insert.setString(4, group.link);

                        ResultSet rs = insert.executeQuery();
                        rs.next();
                        eventId = rs.getInt(1);

                    }
                    updateEventGroupMap(groupId, eventId, conn);
                    event.setId(eventId);
                    System.out.println(eventId);
                    eventTimeRepository.setEventDay(event, conn);

                }

            }
        }
    }

    public int getEvent(String eventTitle, String url, Connection conn) throws Exception {
        String query = "SELECT * from events where name = ? and url=?";
        PreparedStatement select = conn.prepareStatement(query);
        select.setString(1, eventTitle);
        select.setString(2, url);
        ResultSet rs = select.executeQuery();
        if(rs.next()) {
            return rs.getInt(1);
        }
        return -1;

    }

    private void updateEventGroupMap(int groupId, int locationId, Connection conn) throws Exception{
        String query = "INSERT INTO event_group_map(group_id, event_id) values(?,?)";
        PreparedStatement insert = conn.prepareStatement(query);
        insert.setInt(1, groupId);
        insert.setInt(2, locationId);
        insert.executeUpdate();
    }
}
