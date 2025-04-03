package app.result;

import app.data.Event;
import app.data.Group;
import java.util.HashMap;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

public class GroupSearchResult {
  private HashMap<Integer, Group> groupData;

  Logger logger;

  public GroupSearchResult() {
    groupData = new HashMap<Integer, Group>();
    logger = LogUtils.getLogger();
  }

  public void addGroup(Integer id, String name, String url, String summary) {
    if (!groupData.containsKey(id)) {

      Group group = new Group();
      group.setId(id);
      group.setName(name);
      group.setUrl(url);
      group.setSummary(summary);
      groupData.put(id, group);
    }
  }

  public void addEvent(
    Integer groupId,
    Integer eventId,
    String name,
    String description,
    String dayOfWeek,
    String address
  ) {
    if (!groupData.containsKey(groupId)) {
      logger.warn(
        "Group with id {} does not exist. Event will not be added to group search result",
        groupId
      );
      return;
    }

    Group group = groupData.get(groupId);

    Event event = new Event();
    event.setName(name);
    event.setSummary(description);
    event.setDay(dayOfWeek);
    event.setLocation(address);
    event.setId(eventId);
    group.addEvent(event);
  }

  public int countGroups(){
    return groupData.size();
  }

  public int countEvents() {
    int events = 0;
    for(Group group: groupData.values()){
      events+=group.countEvents();
    }
    return events;
  }
}
