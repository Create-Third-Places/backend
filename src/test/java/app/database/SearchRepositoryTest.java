package app.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import app.database.utils.DbUtils;
import app.database.utils.TestConnectionProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * TODO
 * -Figure out how to run tests
 * -Figure out how to initialize local database
 * -Add test data.
 *
 */
public class SearchRepositoryTest {

    Connection conn;

    @BeforeAll
    static void setup()  {

        TestConnectionProvider testConnectionProvider = new TestConnectionProvider();
        try {
            Connection conn = testConnectionProvider.getDatabaseConnection();
            DbUtils.createTables(conn);
            DbUtils.initializeData(testConnectionProvider);

        } catch(Exception e){
            fail("Error initializing database:"+e.getMessage());
        }
    }

    @Test
    public void simpleTest(){
        assertEquals(1,1);
    }
}
