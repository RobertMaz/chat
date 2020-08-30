import java.sql.*;


public class DatabaseAuthManager implements AuthManager {

    private static Connection connection;
    private static Statement ps;
    private static ResultSet rs;


    @Override
    public String getNickNameByLoginAndPassword(String login, String password) {
        try {
            connect();
            rs = ps.executeQuery("SELECT nickname, password FROM USERS WHERE login = '" + login.trim() + "';");
            rs.next();
            String nickname = rs.getString("nickname");
            String password1 = rs.getString("password");
            if (password.equals(password1)) {
                return nickname;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return null;
    }

    @Override
    public void updateNickname(String oldNickname, String newNickname) {
        try {
            connect();
            int count = ps.executeUpdate("UPDATE users set nickname = '" + newNickname + "' where nickname = '" + oldNickname + "';");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }


    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/MyComp/IdeaProjects/chat/db_for_chat.db");
        ps = connection.createStatement();

    }

    public static void disconnect() {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (ps != null)
                ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}