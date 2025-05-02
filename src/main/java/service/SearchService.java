package service;

import app.result.GroupSearchResult;
import database.search.GroupSearchParams;
import database.search.SearchRepository;
import database.utils.ConnectionProvider;
import java.sql.Connection;
import java.util.LinkedHashMap;

public class SearchService {

  public GroupSearchResult getGroups(
    LinkedHashMap<String, String> searchParams,
    ConnectionProvider connectionProvider
  ) throws Exception
  {
    GroupSearchParams params = new GroupSearchParams(searchParams);
    Connection conn = connectionProvider.getDatabaseConnection();

    SearchRepository searchRepository = new SearchRepository();
    return searchRepository.getGroups(params, conn);
  }

  //TODO: Add logic
  public GroupSearchResult getGroup(
      ConnectionProvider connectionProvider
  ) throws Exception
  {
    /**
     * TODO
     * -Create search parameters object and call getGroups method.
     */
    return null;
  }
}
