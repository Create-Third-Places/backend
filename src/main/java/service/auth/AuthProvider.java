package service.auth;



import app.users.data.RegisterUserRequest;
import app.users.data.RegisterUserResponse;

import java.util.Optional;

public interface AuthProvider {
  public Optional<String> getUsernameFromToken(String token) throws Exception;

  public RegisterUserResponse registerUser(RegisterUserRequest request) throws Exception;

}
