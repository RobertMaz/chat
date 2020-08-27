import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainApp {


    public static void main(String[] args) {


        try (ServerSocket serverSocket = new ServerSocket(8181)) {
            System.out.println("Server started");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String msg = in.readUTF();
                if ("/end".equals(msg)) {
                    out.writeUTF("/end_confirm");
                    break;
                }
                System.out.println("Client message: " + msg);
                out.writeUTF("echo: " + msg);
            }
            in.close();
            out.close();
            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
