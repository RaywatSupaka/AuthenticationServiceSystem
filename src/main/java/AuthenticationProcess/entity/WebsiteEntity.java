package AuthenticationProcess.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@Document(collection = "WebsiteDetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebsiteEntity {
    @Id
    private String wid;
    private String wname;
    private String local;
    private int status;
    private String description;
    private Date tmp;
    private String type;
    private String img;
}
