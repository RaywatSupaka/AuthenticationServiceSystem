package AuthenticationProcess.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "User")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    private String uid;
    private String nid;
    private String username;
    private String password;
    private int age;
    private String address;
    private String roles;
    private Date ts;
    private String image;
}
