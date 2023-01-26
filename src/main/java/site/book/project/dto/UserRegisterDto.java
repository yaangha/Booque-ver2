package site.book.project.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import site.book.project.domain.User;
import site.book.project.domain.UserRole;

@NoArgsConstructor
@Data
public class UserRegisterDto {
    // 요청 파라미터 이름과 같게 필드들을 선언. => dispatcher servelet이 알아서 해준다.

    private String username; // request로 들어오는 값
    private String password;
    private String email;
    private String nickname;
    private String name;
    private String phone;
    
    public User toEntity() {

        return User.builder().username(username).password(password).email(email).nickName(nickname).name(name).phone(phone)
                .build()
                .addRole(UserRole.USER);

        // addRole(MemberRole.USER) = 어드민이 아닌 USER 권한을 부여하겠다.
    }

}
