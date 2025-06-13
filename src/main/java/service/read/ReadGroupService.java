package service.read;

import app.groups.data.Group;
import app.data.auth.PermissionName;
import app.data.auth.User;
import app.result.groupPage.GroupPageData;
import database.content.GroupsRepository;
import database.utils.ConnectionProvider;
import service.data.SearchParameterException;
import service.permissions.GroupPermissionService;
import service.provider.ReadGroupDataProvider;

import java.util.LinkedHashMap;
import java.util.Optional;

public class ReadGroupService{


  SearchService searchService;
  GroupPermissionService groupPermissionService;
  GroupsRepository groupsRepository;

  public ReadGroupService(ReadGroupDataProvider dataProvider) {
    this.searchService = dataProvider.getSearchService();
    this.groupPermissionService = dataProvider.getGroupPermissionService();
    this.groupsRepository = dataProvider.getGroupsRepository();
  }

  public Optional<Group> getGroup(int groupId, ConnectionProvider connectionProvider) throws Exception{
    return this.groupsRepository.getGroup(groupId, connectionProvider.getDatabaseConnection());
  }
  public GroupPageData getGroupPageData(
      User currentUser,
      LinkedHashMap<String, String> params,
      ConnectionProvider connectionProvider) throws Exception{

    Group group = searchService.getSingleGroup(params, connectionProvider);
    if(group == null){
      throw new SearchParameterException("No group found with parameters:"+params);
    }
    GroupPageData groupPageData = GroupPageData.createFromSearchResult(group);

    boolean canEdit = groupPermissionService.canEditGroup(currentUser, groupPageData.getId(),connectionProvider);
    groupPageData.enablePermission(PermissionName.USER_CAN_EDIT.toString(), canEdit);
    return groupPageData;
  }
}
