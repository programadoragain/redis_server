import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  private static final ExecutorService EXECUTOR_SERVICE= Executors.newFixedThreadPool(5);

  public static void main(String args[]) {
    Integer port= 6379;
  
    try {

      @SuppressWarnings("resource")
      ServerSocket serverSocket= new ServerSocket(port);
      Socket clientSocket= null;

      serverSocket.setReuseAddress(true);

      while (true) {
        clientSocket = serverSocket.accept();
        EXECUTOR_SERVICE.submit(new ClientHandler(clientSocket));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
    
}

