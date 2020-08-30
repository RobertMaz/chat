import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

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
                        String[] tokens = msg.split("\\s", 3);
                        String nickFromAuthManager = server.getAuthManager().
                                getNickNameByLoginAndPassword(tokens[1], tokens[2]);
                        if (nickFromAuthManager != null) {
                            if (server.isNickBusy(nickFromAuthManager)) {
                                sendMsg("Пользователь уже в чате");
                                continue;
                            }
                            nickname = nickFromAuthManager;
                            sendMsg("/authok " + nickname);
                            server.subscribe(this);
                            server.broadcastMsg(this.nickname + " в сети");
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
                        } else if (msg.startsWith("/w ")) {
                            try {
                                String[] words = msg.split("\\s", 3);
                                sendPrivateMessage(words[1], words[2]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else if(msg.startsWith("/change_nick ")){
                            try {
                                String[] words = msg.split("\\s", 2);
                                server.getAuthManager().updateNickname(nickname, words[1]);
                                nickname = words[1];
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else if (msg.startsWith("/help")){
                            sendMsg(" /end closed chat\n" +
                                    " /w private message\n" +
                                    " /change_nick change your nick\n" );
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

    private void sendPrivateMessage(String nick, String msg) {
        List<ClientHandler> clients = server.getClients();
        for (ClientHandler client : clients) {
            if (nick.equals(client.getNickname())) {
                client.sendMsg(this.nickname + " (private): " + msg);
                sendMsg(nickname + " (private): " + msg);
                return;
            }
        }
        sendMsg("This user is not entered");
    }


    public void close() {
        server.broadcastMsg(nickname + " вышел из сети");
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
