package service;

import app.data.Group;
import app.result.GroupSearchResult;
import app.result.groupPage.GroupPageData;
import database.utils.ConnectionProvider;
import service.data.SearchParameterException;

import java.util.LinkedHashMap;

public class GroupService {

  SearchService searchService;

  public GroupService(SearchService searchService){
    this.searchService = searchService;
  }

  public GroupPageData getGroupPageData(LinkedHashMap<String, String> params,
                                        ConnectionProvider connectionProvider) throws Exception{

    GroupSearchResult groupSearchResult = searchService.getGroups(params, connectionProvider);

    System.out.println(groupSearchResult.countGroups());
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
