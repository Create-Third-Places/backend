package service.read;

import app.data.auth.User;
import app.result.GroupSearchResult;
import database.search.GroupSearchParams;
import database.search.SearchRepository;
import database.utils.ConnectionProvider;

import java.sql.Connection;
import java.util.LinkedHashMap;

public class SearchService {


  public GroupSearchResult getGroups(
    User currentUser,
    LinkedHashMap<String, String> searchParams,
    ConnectionProvider connectionProvider
  ) throws Exception
  {
    GroupSearchParams params = new GroupSearchParams(searchParams);
    Connection conn = connectionProvider.getDatabaseConnection();

    SearchRepository searchRepository = new SearchRepository();


    GroupSearchResult groups = searchRepository.getGroups(params, conn);

    //TODO: Check for group edit permissions
    groups
    return groups;
  }


}
