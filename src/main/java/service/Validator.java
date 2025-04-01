package service;

public class Validator {

  public static boolean isValidAddress(String address) {
    String[] data = address.split(",");
    return data.length == 3;
  }
}
