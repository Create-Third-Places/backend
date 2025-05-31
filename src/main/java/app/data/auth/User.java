package app.data.auth;

public class User {
  private String email;
  private UserType userType;
  private int id;
  public User(String email, UserType userType, int id){
    this.email = email;
    this.userType = userType;
    this.id = id;
  }

  public boolean isLoggedInUser(){
    return email != null && !email.isBlank();
  }

  public int getId(){
    return id;
  }

  public String getEmail(){
    return email;
  }

  public String getAdminLevel(){
    return userType.name();
  }

  public boolean isSiteAdmin(){
    return userType.equals(UserType.SITE_ADMIN);
  }


}
