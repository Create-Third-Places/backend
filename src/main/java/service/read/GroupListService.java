package service.read;

import app.data.Group;
import app.result.GroupSearchResult;
import app.result.groupPage.GroupPageData;
import database.utils.ConnectionProvider;
import service.ContentItemService;
import service.data.SearchParameterException;

import java.util.LinkedHashMap;

public class GroupListService extends ContentItemService {

  SearchService searchService;

  public GroupListService(SearchService searchService){
    this.searchService = searchService;
  }



  public GroupPageData getGroupPageData(LinkedHashMap<String, String> params,
                                        ConnectionProvider connectionProvider) throws Exception{

    GroupSearchResult groupSearchResult = searchService.getGroups(params, connectionProvider);

    if(groupSearchResult.countGroups() > 1 ){
      throw new Exception("Multiple groups were found");
    }

    System.out.println(params);
    Group group = groupSearchResult.getFirstGroup();
    if(group == null){
      throw new SearchParameterException("No group found");
    }
    return GroupPageData.createFromSearchResult(group);

  }
}
