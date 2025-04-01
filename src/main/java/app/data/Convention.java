package app.data;

public class Convention {
  String[] days;
  Integer id;
  String link;
  String title;

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

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
