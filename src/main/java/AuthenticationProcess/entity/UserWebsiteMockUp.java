package AuthenticationProcess.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "UserWebsiteMockup")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWebsiteMockUp {
    @Id
    private String uid;
    private String nid;
    private String username;
    private String password;
    private int age;
    private String address;
    private String roles;
    private Date ts;
}
