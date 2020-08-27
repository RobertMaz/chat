import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                while (true) {
                    String msg = in.readUTF();
                    System.out.println("Client message: " + msg);
                    if (msg.startsWith("/auth")) {
                        String[] tokens = msg.split("\\s",3);
                        String nickFromAuthManager = server.getAuthManager().
                                getNickNameByLoginAndPassword(tokens[1], tokens[2]);
                       if (nickFromAuthManager != null) {
                           if (server.isNickBusy(nickFromAuthManager)){
                               sendMsg("Пользователь уже в чате");
                               continue;
                           }
                           nickname = nickFromAuthManager;
                           server.subscribe(this);
                           sendMsg("/authok " + nickname);
                           break;
                       } else {
                           sendMsg("Указан неверный логин или пароль");
                       }
                    }
                }
                while (true) {
                    String msg = in.readUTF();
                    System.out.println("Client message: " + msg);
                    if (msg.startsWith("/")) {
                        if ("/end".equals(msg)) {
                            out.writeUTF("/end_confirm");
                            break;
                        }
                    } else {
                        server.broadcastMsg(nickname + " : " + msg);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }

        }).start();
    }


    public void close() {
        server.unsubscribe(this);
        nickname = null;
        try {
            if (in != null)
                in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
