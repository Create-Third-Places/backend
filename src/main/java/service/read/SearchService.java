package service.read;

import app.groups.data.Group;
import app.result.GroupSearchResult;
import database.search.GroupSearchParams;
import database.search.SearchRepository;
import database.utils.ConnectionProvider;

import java.sql.Connection;
import java.util.LinkedHashMap;

public class SearchService {

  private final SearchRepository searchRepository;
  public SearchService(Connection conn){
    this.searchRepository = new SearchRepository(conn);
  }

  public GroupSearchResult getGroups(
    LinkedHashMap<String, String> searchParams,
    ConnectionProvider connectionProvider
  ) throws Exception
  {
    GroupSearchParams params = new GroupSearchParams(searchParams);
    GroupSearchResult groups = searchRepository.getGroups(params);
    return groups;
  }


  public Group getSingleGroup(
      LinkedHashMap<String, String> searchParams,
      ConnectionProvider connectionProvider
  ) throws Exception
  {
    GroupSearchParams params = new GroupSearchParams(searchParams);
    Connection conn = connectionProvider.getDatabaseConnection();

    GroupSearchResult groups = searchRepository.getGroups(params);
    if(groups.countGroups() > 1 ){
      throw new Exception("Multiple groups were found");
    }
    return groups.getFirstGroup();
  }
}
