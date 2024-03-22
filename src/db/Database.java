package db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Database implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Table> database;
    public Database(){
        database = new ArrayList();
    }
    public List<Table> getDatabase(){
        return database;
    }

    public void setDatabase(List<Table> database) {
        this.database = database;
    }
}
