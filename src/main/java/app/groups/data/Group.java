package app.groups.data;

import app.data.ContentItem;
import app.data.ContentType;
import app.data.Event;

public class Group extends ContentItem {
  public Event[] events;
  public int id;
  public String url;
  public String[] cities;
  public String summary;
  public String name;

  public Group() {
    super(ContentType.GROUP);
  }

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

  public void setCities(String[] cities) {
    this.cities = cities;
  }

  public String[] getCities() {
    return cities;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getSummary(){
    return summary;
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

    if(events == null || events.length == 0){
      return new Event[0];
    }
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
      "cities:" +
      cities +
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
    if (events == null) {
      events = new Event[] { event };
      return;
    }
    Event[] updated = new Event[events.length + 1];
    System.arraycopy(events, 0, updated, 0, events.length);
    updated[events.length] = event;
    events = updated;
  }

  public void addCity(String city) {


    if(city == null){
      return;
    }

    if (cities == null) {
      cities = new String[] { city };
      return;
    }


    //TODO: Consider using set to improve efficiency.
    for(String existing: cities){
      if(existing.equals(city)){
        return;
      }
    }

    String[] updated = new String[cities.length + 1];
    System.arraycopy(cities, 0, updated, 0, cities.length);
    updated[cities.length] = city;
    cities = updated;
  }

}
