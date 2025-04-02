package database.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class SearchParams {
    private HashMap<String, String> params;
    public SearchParams() {
        params = new HashMap<String, String>();
    }

    public void setParams(String key, String value){
        params.put(key, value);
    }

    public PreparedStatement getSearchQuery(Connection connection){
        /**
         * Determine query that needs to be returned based on parameters.
         */
        return null;
    }
}
