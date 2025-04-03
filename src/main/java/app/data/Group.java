package app.data;

public class Group {
  public Event[] events;
  public int id;
  public String url;
  public String locations;
  public String summary;
  public String name;

  public Group() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEvents(Event[] events) {
    this.events = events;
  }

  public Event[] getEvents() {
    return events;
  }

  public String toString() {
    return (
      "Events:" +
      events +
      "\n" +
      "id:" +
      id +
      "\n" +
      "url:" +
      url +
      "\n" +
      "locations:" +
      locations +
      "\n" +
      "summary:" +
      summary +
      "\n" +
      "name:" +
      name +
      "\n"
    );
  }

  public int countEvents() {
    if (events == null) {
      return 0;
    }
    return events.length;
  }
  public void addEvent(Event event) {
    if(events == null) {
      events = new Event[]{event};
      return;
    }
    Event[] updated = new Event[events.length + 1];
    System.arraycopy(events, 0, updated, 0, events.length);
    updated[events.length] = event;
    events = updated;
  }
}
