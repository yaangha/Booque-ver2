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
    
    public void setUser(String nickName) throws Exception {
        if (users.contains(nickName)) {
            throw new Exception("User already Exist/" + nickName);
        }
        users.add(nickName);
        System.out.println("웹소켓 연결되어 있는 유저들");
        for (String s : users) {
            System.out.println("}"+s);
            
        }
    }
    
    public void unsetUser(String nickName) {
        users.remove(nickName);
        System.out.println("웹소켓 연결되어 있는 유저들");
        for (String s : users) {
            System.out.println("}"+s);
            
        }
    }
}
