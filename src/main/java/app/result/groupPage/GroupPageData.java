package app.result.groupPage;

import app.data.Event;
import app.groups.data.Group;
import app.data.auth.PermissionName;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;



public class GroupPageData {

  private static final int TIME_RANGE_DAYS = 30;
  private int id;
  private String name;
  private String url;
  private String summary;
  private HashMap<String, Boolean> permissions;
  private TreeSet<GroupPageEventData> groupPageEventData;

  private GroupPageData(int id, String name, String url, String summary){
    this.id = id;
    this.name = name;
    this.url = url;
    this.summary = summary;
    this.permissions = new HashMap<>();
    this.groupPageEventData = new TreeSet<GroupPageEventData>(new GroupPageEventDataComparator());
  }

  public int getId(){
    return id;
  }

  public void setId(int id){
    this.id = id;
  }

  public String getName(){
    return name;
  }

  public void setName(String name){
    this.name = name;
  }

  public String getUrl(){
    return url;
  }

  public void setUrl(String url){
    this.url = url;
  }

  public String getSummary(){
    return summary;
  }

  public void setSummary(String summary){
    this.summary = summary;
  }


  public Set<GroupPageEventData> getEventData(){
    return this.groupPageEventData;
  }

  public void addEventData(LocalDate date, String name, String description, String link, int id){
    GroupPageEventData eventData = new GroupPageEventData(date, name, description, link, id);
    groupPageEventData.add(eventData);
  }

  public static GroupPageData createFromSearchResult(Group group) {
    GroupPageData data = new GroupPageData(group.getId(), group.getName(), group.getUrl(), group.getSummary());

    LocalDate currentDate = LocalDate.now();

    if(group.getEvents() != null){
      for(Event event: group.getEvents()) {

        //TODO: Handle case where events are not recurring
        String day = event.getDay();
        LocalDate nextEventDate = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(day.toUpperCase())));
        while (nextEventDate.minusDays(TIME_RANGE_DAYS + 1).isBefore(currentDate)) {
          data.addEventData(nextEventDate, event.getName(), event.getSummary(), event.getLocation(), event.getId());
          nextEventDate = nextEventDate.plusDays(7);
        }
      }
    }

    return data;
  }


  public HashMap<String,Boolean> getPermissions(){
    return permissions;
  }

  public void setPermissions(HashMap<String, Boolean> permissions){
    this.permissions = permissions;
  }

  public void enablePermission(String permissionName, boolean isEnabled){
    this.permissions.put(permissionName, isEnabled);
  }

  public boolean userCanEdit(){
    if(permissions == null) {
      return false;
    }
    return permissions.getOrDefault(PermissionName.USER_CAN_EDIT.toString(), false);
  }
}
