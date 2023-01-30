package site.book.project.domain;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChatLog {

    private static UserChatLog instance;
    private Set<String> users;
    
    private UserChatLog() {
        users = new HashSet<>();
    }
    
    public static synchronized UserChatLog getInstance() {
        if(instance == null) {
            instance = new UserChatLog();
        }
        return instance;
    }
    
    public void setUser(String userName) throws Exception {
        if (users.contains(userName)) {
            throw new Exception("User already Exist" + userName);
        }
        users.add(userName);
    }
}
