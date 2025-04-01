package service;

import app.data.InputRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.Logger;
import service.data.AuthRequest;
import utils.LogUtils;

public class AuthService {
  private static final Logger logger = LogUtils.getLogger();
  final String API_KEY =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImthcnF5c2t1dWRudmZ4b2h3a29rIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE5ODQ5NjgsImV4cCI6MjA1NzU2MDk2OH0.TR-Pn6dknOTtqS9y-gxK_S1-nw6TX-sL3gRH2kXJY_I";
  final String URL =
    "https://karqyskuudnvfxohwkok.supabase.co/auth/v1/token?grant_type=password";

  public boolean validate(InputRequest request) throws Exception {
    final HttpPost httpPost = new HttpPost(URL);
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("apikey", API_KEY);
    final HttpClient httpClient = HttpClients.createDefault();

    AuthRequest authRequest = new AuthRequest();
    authRequest.setPassword(request.getPassword());
    authRequest.setEmail(request.getEmail());

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(authRequest);

    HttpEntity stringEntity = new StringEntity(
      json,
      ContentType.APPLICATION_JSON
    );
    httpPost.setEntity(stringEntity);

    try {
      HttpResponse rawResponse = httpClient.execute(httpPost);

      final int statusCode = rawResponse.getCode();

      if (statusCode != 200) {
        logger.debug(rawResponse);

        throw new Exception(
          "Authorization failed with status code:" + statusCode
        );
      }
    } catch (Exception e) {
      logger.error("Authorization failed with error", e);
      throw (e);
    }
    logger.info("Authorized");
    return true;
  }
}
