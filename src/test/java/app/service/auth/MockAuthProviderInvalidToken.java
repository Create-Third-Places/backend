package app.service.auth;

import app.users.data.RegisterUserRequest;
import app.users.data.RegisterUserResponse;
import service.auth.AuthProvider;

import java.util.Optional;

public class MockAuthProviderInvalidToken implements AuthProvider {

  @Override
  public Optional<String> getUsernameFromToken(String token) throws Exception {
    throw new Exception("Error");
  }

  @Override
  public RegisterUserResponse registerUser(RegisterUserRequest request) throws Exception{
    throw new Exception("Error");
  }
}
