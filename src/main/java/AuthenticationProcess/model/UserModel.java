package AuthenticationProcess.model;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class UserModel {
    private String uid;
    private String nid;
    private String username;
    private String password;
    private int age;
    private String address;
}
