package service.read;

import app.data.Group;
import app.data.auth.User;
import app.result.GroupSearchResult;
import app.result.groupPage.GroupPageData;
import database.utils.ConnectionProvider;
import service.data.SearchParameterException;

import java.util.LinkedHashMap;

public class GroupListService {

  SearchService searchService;

  public GroupListService(SearchService searchService, User currentUser){
    this.searchService = searchService;
  }

  public GroupPageData getGroupPageData(
      User currentUser,
      LinkedHashMap<String, String> params,
      ConnectionProvider connectionProvider) throws Exception{

    GroupSearchResult groupSearchResult = searchService.getGroups(currentUser, params, connectionProvider);

    if(groupSearchResult.countGroups() > 1 ){
      throw new Exception("Multiple groups were found");
    }

    Group group = groupSearchResult.getFirstGroup();
    if(group == null){
      throw new SearchParameterException("No group found");
    }
    return GroupPageData.createFromSearchResult(group);

  }
}
