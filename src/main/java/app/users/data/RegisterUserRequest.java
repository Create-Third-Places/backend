package app.users.data;

public class RegisterUserRequest {
  private String email;
  private String password;

  public RegisterUserRequest(){}

  public String getEmail(){
    return email;
  }

  public void setEmail(String email){
    this.email = email;
  }

  public String getPassword(){
    return password;
  }

  public void setPassword(String password){
    this.password = password;
  }

  public static RegisterUserRequest createRequest(String username, String password){
    RegisterUserRequest request = new RegisterUserRequest();
    request.setEmail(username);
    request.setPassword(password);
    return request;
  }
}