import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {

  private Socket clientSocket;

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {

    try {
      BufferedReader bufferIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter bufferOut= new PrintWriter(clientSocket.getOutputStream(), true);
      Map<String, String> values= new HashMap<>();
        
      String clientMessageLine;
      // *1\r\n$4\r\nping\r\n
      // *2\r\n$4\r\necho\r\n$4\r\nhello\r\n 
      // *3\r\n$3\r\nset\r\n$5\r\napple\r\n$10\r\nstrawberry\r\n
      // *2\r\n$3\r\nget\r\n$5\r\nmango\r\n
      
      if (((clientMessageLine = bufferIn.readLine()) != null) && (clientMessageLine.startsWith("*"))) {
      
        while ((clientMessageLine = bufferIn.readLine()) != null) {

          System.out.println("Client message: " + clientMessageLine);
          
          if (clientMessageLine.equalsIgnoreCase(RedisCommands.PING.toString())) {
            bufferOut.write(ResponseFormatter.responseToPing());
            bufferOut.flush();
          }  
        
          if (clientMessageLine.equalsIgnoreCase(RedisCommands.ECHO.toString())) {
            while ((clientMessageLine = bufferIn.readLine()) != null) {
              if (!clientMessageLine.startsWith("$")) {
                bufferOut.write(ResponseFormatter.responseMessage(clientMessageLine)); 
                bufferOut.flush();
                break;
              }
            } 
          }  
          
          if (clientMessageLine.equalsIgnoreCase(RedisCommands.SET.toString())) {
            String key; 
            while (((key = bufferIn.readLine()) != null)) {
              if ((!key.startsWith("$"))) {
                String value= bufferIn.readLine();
                values.put(key, value);
                bufferOut.write(ResponseFormatter.responseToSet()); 
                bufferOut.flush();
                break;
              }
            }  
          } 

          if (clientMessageLine.equalsIgnoreCase(RedisCommands.GET.toString())) {
            String key; 
            while ((key = bufferIn.readLine()) != null) {
              if (!key.startsWith("$")) { 
                //System.out.println("Search key: " + key);
                if (values.containsKey(key)) {
                  bufferOut.write(ResponseFormatter.responseMessage(values.get(key))); 
                  bufferOut.flush();
                  break;
                } else {
                  bufferOut.write(ResponseFormatter.responseToNullVariable()); 
                  bufferOut.flush();
                  break;
                }
              }
            }    
          }
        
        } 

        System.out.println("Good bye");
        clientSocket.close();
        bufferIn.close();
        bufferOut.close();

      } 
    
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
