package backend.model;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;

@Data
public class Database {
    private String dataBaseName;        // stores the name of the database
    private List<Field> fields;         // stores the name of the tables

    public Database(String name) {
        dataBaseName = name;
    }
}
