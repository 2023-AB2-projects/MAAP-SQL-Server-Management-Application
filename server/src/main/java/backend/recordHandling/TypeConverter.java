package backend.recordHandling;

import backend.Indexing.Key;
import backend.exceptions.recordHandlingExceptions.InvalidTypeException;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TypeConverter {
    public static long sizeof(String type){
        switch (type) {
            case "int" -> { return Integer.BYTES; }
            case "float" -> { return Float.BYTES; }
            case "char" -> { return Character.BYTES; }
            case "bit" -> { return 1;}
            default -> {
                Pattern pattern = Pattern.compile("char\\((\\d+)\\)");
                Matcher matcher = pattern.matcher(type);

                if(matcher.find()){
                    return Long.parseLong(matcher.group(1));
                }
                else{
                    return 0;
                }
            }
        }
    }
    public static long sizeofStructure(ArrayList<String> types){
        long sum = 0;
        for(String type : types){
            sum += sizeof(type);
        }
        return sum;
    }
    public static String toString(String type, byte[] bytes){
        switch (type) {
            case "int" -> {
                return Integer.toString(ByteBuffer.wrap(bytes).getInt());
            }
            case "float" -> {
                return Float.toString(ByteBuffer.wrap(bytes).getFloat());
            }
            case "char" -> {
                return Character.toString(ByteBuffer.wrap(bytes).getChar());
            }
            case "bit" -> {
                if(bytes[0] == 0){
                    return "0";
                }
                return "1";
            }
            default -> {
                Pattern pattern = Pattern.compile("char\\((\\d+)\\)");
                Matcher matcher = pattern.matcher(type);
                if (matcher.find()) {
                    return new String(bytes, StandardCharsets.US_ASCII);
                } else {
                    return "";
                }
            }
        }
    }
    public static Object toObject(String type, byte[] bytes){
        switch (type) {
            case "int" -> {
                return ByteBuffer.wrap(bytes).getInt();
            }
            case "long" -> {
                return ByteBuffer.wrap(bytes).getLong();
            }
            case "float" -> {
                return ByteBuffer.wrap(bytes).getFloat();
            }
            case "double" -> {
                return ByteBuffer.wrap(bytes).getDouble();
            }
            case "char" -> {
                return ByteBuffer.wrap(bytes).getChar();
            }
            case "bit" -> {
                if(bytes[0] == 0){
                    return (byte) 0;
                }
                return (byte) 1;
            }
            default -> {
                Pattern pattern = Pattern.compile("char\\((\\d+)\\)");
                Matcher matcher = pattern.matcher(type);
                if (matcher.find()) {
                    return new String(bytes, StandardCharsets.US_ASCII);
                } else {
                    return "";
                }
            }
        }
    }
    public static Object toObject(String type, String value){
        switch (type) {
            case "int" -> {
                return Integer.parseInt(value);
            }
            case "float" -> {
                return Float.parseFloat(value);
            }
            case "char" -> {
                return value.charAt(0);
            }
            case "bit" -> {
                if (value.equals("1")) {
                    return (byte) 1;
                }
                return (byte) 0;
            }
            default -> {
                try {
                    return RecordStandardizer.formatString(value, type);
                }catch (InvalidTypeException e){
                    return "";
                }

            }
        }
    }
    public static ArrayList<Object> toObjectList(ArrayList<String> types, byte[] bytes){
        ArrayList<Object> values = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        for(String type : types){
            byte[] b = new byte[(int)sizeof(type)];
            buffer.get(b);
            values.add(toObject(type, b));
        }

        return values;
    }
    public static ArrayList<Object> toObjectList(ArrayList<String> types, ArrayList<String> values){
        ArrayList<Object> objects = new ArrayList<>();
        for(int i = 0; i < types.size(); i++){
            objects.add(toObject(types.get(i), values.get(i)));
        }
        return objects;
    }
    public static Key toKey(ArrayList<String> keyStructure, ArrayList<String> values){
        return new Key(toObjectList(keyStructure, values), keyStructure);
    }

    public static byte[] toBytes(String type, String value){
        ByteBuffer buffer;
        switch (type) {
            case "int" -> {
                buffer = ByteBuffer.allocate(Integer.BYTES);
                buffer.putInt(Integer.parseInt(value));
                return buffer.array();
            }
            case "float" -> {
                buffer = ByteBuffer.allocate(Float.BYTES);
                buffer.putFloat(Float.parseFloat(value));
                return buffer.array();
            }
            case "char" -> {
                buffer = ByteBuffer.allocate(Character.BYTES);
                buffer.putChar(value.charAt(0));
                return buffer.array();
            }
            case "bit" -> {
                byte[] b = new byte[1];
                if (value.equals("1")) {
                    b[0] = 1;
                }
                return b;
            }
            default -> {
                try {
                    return RecordStandardizer.formatString(value, type).getBytes(StandardCharsets.US_ASCII);
                }catch (InvalidTypeException e){
                    return new byte[0];
                }

            }
        }
    }
    public static byte[] toBytes(String type, Object value){
        if(value == null){
            throw new RuntimeException();
        }
        ByteBuffer buffer;
        switch (type) {
            case "int" -> {
                buffer = ByteBuffer.allocate(Integer.BYTES);
                buffer.putInt((Integer) value);
                return buffer.array();
            }
            case "float" -> {
                buffer = ByteBuffer.allocate(Float.BYTES);
                buffer.putFloat((Float) value);
                return buffer.array();
            }
            case "char" -> {
                buffer = ByteBuffer.allocate(Character.BYTES);
                buffer.putChar((Character) value);
                return buffer.array();
            }
            case "bit" -> {
                byte[] b = new byte[1];
                b[0] = (byte) value;
                return b;
            }
            default -> {
                try {
                    return RecordStandardizer.formatString((String) value, type).getBytes(StandardCharsets.US_ASCII);
                } catch (InvalidTypeException e) {
                    return new byte[0];
                }
            }
        }
    }
    public static byte[] toBytes(ArrayList<String> types, ArrayList<Object> values){
        ByteBuffer buffer = ByteBuffer.allocate((int)sizeofStructure(types));
        for(int i = 0; i < types.size(); i++){
            buffer.put(toBytes(types.get(i), values.get(i)));
        }

        return buffer.array();
    }

    public static int compare(String type, Object o1, Object o2) {
        switch (type) {
            case "int" -> {
                return Integer.compare((int) o1, (int) o2);
            }
            case "float" -> {
                return Float.compare((float) o1, (float) o2);
            }
            case "char" -> {
                return Character.compare((char) o1, (char) o2);
            }
            case "bit" -> {
                byte b1 = (byte) o1, b2 = (byte) o2;
                return b1 - b2;
            }
            default -> {
                try{
                    String string1 = RecordStandardizer.formatString((String) o1, type), string2 = RecordStandardizer.formatString((String) o2, type);
                    return string1.compareTo(string2);
                }catch (InvalidTypeException e){
                    log.error("Invalid type given to comparator");
                    return 0;
                }
            }
        }
    }

    public static Key smallestKey(ArrayList<String> keyStructure){
        ArrayList<Object> minValues = new ArrayList<>();
        for (String type : keyStructure) {
            minValues.add(smallestValue(type));
        }
        return new Key(minValues, keyStructure);
    }
    public static Object smallestValue(String type) {
        switch (type) {
            case "int" -> {
                return Integer.MIN_VALUE;
            }
            case "float" -> {
                return -Float.MAX_VALUE;
            }
            case "char" -> {
                return Character.MIN_VALUE;
            }
            case "bit" -> {
                return (byte) 0;
            }
            default -> {
                try{
                    Pattern pattern = Pattern.compile("char\\((\\d+)\\)");
                    Matcher matcher = pattern.matcher(type);
                    if (matcher.find()) {
                        long size = Long.parseLong(matcher.group(1));
                        StringBuilder minString = new StringBuilder();
                        for (int i = 0 ; i < size; i++){
                            minString.append('\0');
                        }
                        return minString.toString();
                    }else{
                        throw new InvalidTypeException();
                    }
                }catch (InvalidTypeException e){
                    log.error("Invalid type given to comparator");
                    return 0;
                }
            }
        }
    }
}
