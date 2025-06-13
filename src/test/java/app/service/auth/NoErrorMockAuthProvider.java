package app.service.auth;

import app.users.data.RegisterUserRequest;
import app.users.data.RegisterUserResponse;
import service.auth.AuthProvider;

import java.util.Optional;

public class NoErrorMockAuthProvider implements AuthProvider {

  @Override
  public Optional<String> getUsernameFromToken(String token) throws Exception {
    return Optional.of(token);
  }

  @Override
  public RegisterUserResponse registerUser(RegisterUserRequest request) throws Exception{
    return new RegisterUserResponse(request.getEmail(), "1-1-2025");
  }
}
