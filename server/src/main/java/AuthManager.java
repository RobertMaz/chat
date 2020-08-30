public interface AuthManager {
    String getNickNameByLoginAndPassword(
            String login, String password);
    void updateNickname(String oldNickname, String newNickname);
}
