import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private List<ClientHandler> clients;
    private AuthManager authManager;

    public List<ClientHandler> getClients() {
        return clients;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public Server(int port) {
        clients = new ArrayList<>();
        authManager = new DatabaseAuthManager();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started");
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                ClientHandler clientHandler = new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMsg(String msg){
        for(ClientHandler client:clients){
            client.sendMsg(msg);
        }
    }

    public void broadcastClientList(){
        StringBuilder stringBuilder = new StringBuilder("/clients_list ");

        for(ClientHandler o : clients){
            stringBuilder.append(o.getNickname()).append(" ");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        String text = stringBuilder.toString();
        broadcastMsg(text);
    }

    public boolean isNickBusy(String nickname){
        for(ClientHandler o : clients){
            if (o.getNickname().equals(nickname)){
                return true;
            }
        }
        return false;
    }

    public synchronized void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        broadcastClientList();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastClientList();
    }

}
