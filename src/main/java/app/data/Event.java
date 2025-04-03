package app.data;

public class Event {
  private Integer id;
  private String day;
  private String location;
  private String summary;
  private String name;

  public Event() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getDay() {
    return day.toLowerCase();
  }

  public void setDay(String day) {
    this.day = day.toLowerCase();
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getSummary() {
    return this.summary;
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
}
