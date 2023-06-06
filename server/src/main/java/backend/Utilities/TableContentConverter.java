package backend.Utilities;

import java.util.ArrayList;

public class TableContentConverter {
    public static ArrayList<ArrayList<String>> convert(Table table) {
        ArrayList<ArrayList<Object>> records = table.getTableContent();
        ArrayList<ArrayList<String>> convertedRecords = new ArrayList<>();

        for (ArrayList<Object> record : records) {
            ArrayList<String> convertedRecord = new ArrayList<>();

            for (Object element : record) {
                convertedRecord.add(String.valueOf(element));
            }

            convertedRecords.add(convertedRecord);
        }

        return convertedRecords;
    }

}
