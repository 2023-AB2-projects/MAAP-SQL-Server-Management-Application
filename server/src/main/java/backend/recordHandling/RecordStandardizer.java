package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidTypeException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordStandardizer {
    public static String formatString(String str, String type) throws InvalidTypeException {
        String formattedStr;
        Pattern pattern = Pattern.compile("char\\((\\d+)\\)");
        Matcher matcher = pattern.matcher(type);
        if (matcher.find()) {
            long size = Long.parseLong(matcher.group(1));
            if (size < str.length()) {
                formattedStr = str.substring(0, (int) size - 1);
            } else {
                formattedStr = String.format("%-" + size + "s", str);
            }
        }else{
            throw new InvalidTypeException();
        }
        return formattedStr;
    }

    public static ArrayList<String> standardizeValues(ArrayList<String> values, ArrayList<String> types) {
        ArrayList<String> standardValues = new ArrayList<>();
        for(int i = 0; i < types.size(); i++){
            try{
                standardValues.add(formatString(values.get(i), types.get(i)));
            }catch (InvalidTypeException e){
                standardValues.add(values.get(i));
            }
        }
        return standardValues;
    }

}
