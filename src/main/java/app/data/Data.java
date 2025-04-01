package app.data;

public class Data {
  private Group[] groups;
  private Convention[] conventions;
  private GameStore[] gameStores;
  private GameRestaurant[] gameRestaurants;

  public Data() {}

  public Group[] getGroups() {
    return groups;
  }

  public void setGroups(Group[] groups) {
    this.groups = groups;
  }

  public Convention[] getConventions() {
    return conventions;
  }

  public void setConventions(Convention[] conventions) {
    this.conventions = conventions;
  }

  public GameStore[] getGameStores() {
    return gameStores;
  }

  public void setGameStores(GameStore[] gameStores) {
    this.gameStores = gameStores;
  }

  public GameRestaurant[] getGameRestaurants() {
    return gameRestaurants;
  }

  public void setGameRestaurants(GameRestaurant[] gameRestaurants) {
    this.gameRestaurants = gameRestaurants;
  }
}
