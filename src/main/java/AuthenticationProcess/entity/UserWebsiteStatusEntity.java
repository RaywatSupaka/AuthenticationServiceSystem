package AuthenticationProcess.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "UserWebsiteStatus")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWebsiteStatusEntity {
    @Id
    private String id;
    private String userId;
    private Object websiteId;
    private String isMember;
}
