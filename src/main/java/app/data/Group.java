package app.data;

public class Group {
  public Event[] events;
  public int id;
  public String link;
  public String locations;
  public String summary;
  public String title;

  public Group() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getLink() {
    return link;
  }

  public void setLocations(String locations) {
    this.locations = locations;
  }

  public String getLocations() {
    return locations;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setEvents(Event[] events) {
    this.events = events;
  }

  public Event[] getEvents() {
    return events;
  }
}
