public class ResponseFormatter {

    public static String responseToPing() {
        return "+PONG\r\n";
    }

    public static String responseMessage(String message) {
        StringBuilder messageReturn= new StringBuilder();
        message= message.trim();
        messageReturn.append("$");
        messageReturn.append(message.length());
        messageReturn.append("\r\n");
        messageReturn.append(message);
        messageReturn.append("\r\n");

        return messageReturn.toString();
    }    

    public static String responseToSet() {
        return "+OK\r\n";
    }
        
    public static String responseToNullVariable() {
        return "$-1\r\n";
    }

}