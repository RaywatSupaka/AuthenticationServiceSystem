package AuthenticationProcess.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Image")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageEntity {

    @Id
    private String pid;
    private String name;
    private String type;
    @Length(max = 50000000)
    private byte[] imageByte;
    private Date uploadDate;

}
