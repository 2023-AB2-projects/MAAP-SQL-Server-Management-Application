package backend.recordHandling;

import backend.exceptions.recordHandlingExceptions.InvalidTypeException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteConverter {
    public static long sizeof(String type){
        switch (type) {
            case "int" -> { return Integer.BYTES; }
            case "long" -> { return Long.BYTES; }
            case "float" -> { return Float.BYTES; }
            case "double" -> { return Double.BYTES; }
            case "char" -> { return Character.BYTES; }
            case "bool" -> { return 1;}
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

    public static byte[] toBytes(String type, String value){
        ByteBuffer buffer;
        switch (type) {
            case "int" -> {
                buffer = ByteBuffer.allocate(Integer.BYTES);
                buffer.putInt(Integer.parseInt(value));
                return buffer.array();
            }
            case "long" -> {
                buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(Long.parseLong(value));
                return buffer.array();
            }
            case "float" -> {
                buffer = ByteBuffer.allocate(Float.BYTES);
                buffer.putFloat(Float.parseFloat(value));
                return buffer.array();
            }
            case "double" -> {
                buffer = ByteBuffer.allocate(Double.BYTES);
                buffer.putDouble(Double.parseDouble(value));
                return buffer.array();
            }
            case "char" -> {
                buffer = ByteBuffer.allocate(Character.BYTES);
                buffer.putChar(value.charAt(0));
                return buffer.array();
            }
            case "bool" -> {
                byte[] b = new byte[1];
                if (value.equals("true") || value.equals("1")) {
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

    public static String decode(String type, byte[] bytes){
        ByteBuffer buffer;
        switch (type) {
            case "int" -> {
                return Integer.toString(ByteBuffer.wrap(bytes).getInt());
            }
            case "long" -> {
                return Long.toString(ByteBuffer.wrap(bytes).getLong());
            }
            case "float" -> {
                return Float.toString(ByteBuffer.wrap(bytes).getFloat());
            }
            case "double" -> {
                return Double.toString(ByteBuffer.wrap(bytes).getDouble());
            }
            case "char" -> {
                return Character.toString(ByteBuffer.wrap(bytes).getChar());
            }
            case "bool" -> {
                if(bytes[0] == 0){
                    return "false";
                }
                return "true";
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
}
