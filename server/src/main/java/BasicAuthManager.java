import java.util.ArrayList;
import java.util.List;

public class BasicAuthManager implements AuthManager {


    @Override
    public String getNickNameByLoginAndPassword(String login, String password) {
        for(Entry entry : users){
            if (entry.login.equals(login) && entry.password.equals(password)){
                return entry.nickname;
            }
        }
        return null;
    }

    @Override
    public void updateNickname(String oldNickname, String newNickname) {
    }

    private class Entry {
        private String login;
        private String password;
        private String nickname;

        public Entry(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }

    }

    private List<Entry> users;

    public BasicAuthManager(){
        this.users = new ArrayList<>();
        users.add(new Entry("log1", "pass1", "nick1"));
        users.add(new Entry("log2", "pass2", "nick2"));
        users.add(new Entry("log3", "pass3", "nick3"));
    }

}

