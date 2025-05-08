package app.service;

import app.database.utils.DbUtils;
import app.database.utils.TestConnectionProvider;
import app.result.GroupSearchResult;
import app.result.groupPage.GroupPageData;
import app.result.groupPage.GroupPageEventData;
import database.search.GroupSearchParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.GameLocationsService;
import service.GroupService;
import service.SearchService;

import java.sql.Connection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GroupServiceIntegrationTest {

  private static GroupService groupService;
  private static TestConnectionProvider testConnectionProvider;

  @BeforeAll
  static void setup() {
    testConnectionProvider = new TestConnectionProvider();
    try {
      Connection conn = testConnectionProvider.getDatabaseConnection();

      System.out.println("Creating tables");
      DbUtils.createTables(conn);
      System.out.println("Initializing data");
      DbUtils.initializeData(testConnectionProvider);
      groupService = new GroupService(new SearchService());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error initializing database:" + e.getMessage());
    }
  }

  @Test
  public void invalidNameAndTag() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "test");
    params.put(GroupSearchParams.NAME, "test2");

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          GroupPageData result = groupService.getGroupPageData(
              params,
              testConnectionProvider
          );
        }
    );
    assertTrue(exception.getMessage().contains("No group found"));
  }

  @Test
  public void invalidNameAndValidTag() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "test2");

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          GroupPageData result = groupService.getGroupPageData(
              params,
              testConnectionProvider
          );
        }
    );
    assertTrue(exception.getMessage().contains("No group found"));
  }

  @Test
  public void validNameAndInvalidTag() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "test");
    params.put(GroupSearchParams.NAME, "Game_Nights_at_Crossroads");

    Exception exception = assertThrows(
        Exception.class,
        () -> {
          GroupPageData result = groupService.getGroupPageData(
              params,
              testConnectionProvider
          );
        }
    );
    assertTrue(exception.getMessage().contains("No group found"));
  }


  @Test
  public void validNameAndValidTag_correctGroupInformation() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Alexandria_Board_Game_Group");


    GroupPageData result = groupService.getGroupPageData(
        params,
        testConnectionProvider
    );

    Assertions.assertAll(
        () -> assertEquals("Alexandria Board Game Group", result.getName()),
        () -> assertEquals("https://www.meetup.com/board-games-at/", result.getUrl()),
        () -> assertEquals(result.getSummary().contains("Like playing board games after meeting new people?"), true)
    );
  }

  @Test
  public void validNameAndValidTag_Group_ValidEventsWithOneEventEachWeek() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Alexandria_Board_Game_Group");

    GroupPageData result = groupService.getGroupPageData(
        params,
        testConnectionProvider
    );

    Set<GroupPageEventData> eventData = result.getEventData();
    int eventCount = 0;

    LocalDate prevDate = null;
    for(GroupPageEventData data: eventData) {

      if(prevDate != null){
        assertTrue(data.getEventDate().isAfter(prevDate));
      }
      eventCount++;
      Assertions.assertAll(
         () -> assertEquals(data.getEventDate().getDayOfWeek(), DayOfWeek.MONDAY),
         () -> assertEquals(data.getName(), "Game Night at Glory Days"),
      () -> assertEquals(data.getDescription().contains("We will be playing board games at Glory Days Grill in Alexandria "),
          true),
      () -> assertEquals(data.getLocation(), "3141 Duke Street, Alexandria, VA 22314")
      );
      prevDate = data.getEventDate();
    }

    assertTrue(eventData.size()==4 || eventData.size()==5);

  }

  @Test
  public void validNameAndValidTag_Group_CorrectNumberOfEventsWithFourEventsEachWeek() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Beer_&_Board_Games");

    GroupPageData result = groupService.getGroupPageData(
        params,
        testConnectionProvider
    );

    Set<GroupPageEventData> eventData = result.getEventData();

    LocalDate prevDate = null;
    for(GroupPageEventData data: eventData) {

      if(prevDate != null){
        assertTrue(data.getEventDate().isAfter(prevDate));
      }
      prevDate = data.getEventDate();
    }
    assertTrue(eventData.size()>=16 && eventData.size()<=20);
  }



  @Test
  public void validNameAndValidTag_Group_NoEvents() throws Exception{
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(GroupSearchParams.AREA, "dmv");
    params.put(GroupSearchParams.NAME, "Alexandria_Board_Gaming");

    GroupPageData result = groupService.getGroupPageData(
        params,
        testConnectionProvider
    );

    Set<GroupPageEventData> eventData = result.getEventData();

    LocalDate prevDate = null;
    for(GroupPageEventData data: eventData) {

      System.out.println(data.getName());
      if(prevDate != null){
        assertTrue(data.getEventDate().isAfter(prevDate));
      }
      prevDate = data.getEventDate();
    }

    assertEquals(eventData.size(), 0);
  }
}
