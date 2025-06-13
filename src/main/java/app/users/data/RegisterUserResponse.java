package app.users.data;

public class RegisterUserResponse {
  String email;
  String createdAt;

  public RegisterUserResponse(String email, String createdAt){
    this.email = email;
    this.createdAt = createdAt;
  }

  public void setEmail(String email){
    this.email = email;
  }

  public String getEmail(){
    return email;
  }

  public String getCreatedAt(){
    return createdAt;
  }

  public void setCreatedAt(String createdAt){
    this.createdAt = createdAt;
  }
}
