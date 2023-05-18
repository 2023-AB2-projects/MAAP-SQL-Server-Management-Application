package backend.Utilities;

import backend.service.CatalogManager;

import java.util.ArrayList;

public class Table {
    private ArrayList<String> columnTypes, columnNames;

    private ArrayList<ArrayList<Object>> tableContent;

    public Table(ArrayList<String> columnTypes, ArrayList<String> columnNames, ArrayList<ArrayList<Object>> tableContent) {
        this.columnTypes = columnTypes;
        this.columnNames = columnNames;
        this.tableContent = tableContent;
    }

    public Table(String databaseName, String tableName, ArrayList<ArrayList<Object>> tableContent) {
        this.columnTypes = (ArrayList<String>) CatalogManager.getFieldTypes(databaseName, tableName);
        this.columnNames = (ArrayList<String>) CatalogManager.getFieldNames(databaseName, tableName);
        this.tableContent = tableContent;
    }

}
