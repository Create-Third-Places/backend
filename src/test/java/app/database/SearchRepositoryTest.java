package app.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import app.database.utils.DbUtils;
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

    @Test
    void test() throws Exception{

        Class.forName("org.h2.Driver");
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test");
            DbUtils.createTables(conn);
            assertEquals(1,1);

        } catch(Exception e){
            fail("Error initializing database");
        }
    }
}
