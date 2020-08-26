import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainApp {

    public static void main(String[] args) {


        try (ServerSocket serverSocket = new ServerSocket(8181)) {
            System.out.println("Server started");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected");
            while (true) {
                int in = socket.getInputStream().read();
                System.out.print((char) in);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
