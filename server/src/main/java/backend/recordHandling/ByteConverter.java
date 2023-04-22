package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidTypeException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteConverter {
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
    public static String decode(String type, byte[] bytes){
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
    public static Object fromBytes(String type, byte[] bytes){
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
            case "bool" -> {
                if(bytes[0] == 0){
                    return 0;
                }
                return 1;
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
    public static ArrayList<Object> toList(ArrayList<String> types, byte[] bytes){
        ArrayList<Object> values = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        for(String type : types){
            byte[] b = new byte[(int)sizeof(type)];
            buffer.get(b);
            values.add(fromBytes(type, b));
        }

        return values;
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
}
