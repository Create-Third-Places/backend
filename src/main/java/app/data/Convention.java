package app.data;

public class Convention {
  String[] days;
  Integer id;
  String url;
  String name;

  public Convention() {}

  public String[] getDays() {
    return days;
  }

  public void setDays(String[] days) {
    this.days = days;
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

  public void setUrl(String link) {
    this.url = link;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
