package AuthenticationProcess.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserWebsiteStatusModel {
    private String id;
    private String userId;
    private List<String> websiteId;
    private String isMember;
}
