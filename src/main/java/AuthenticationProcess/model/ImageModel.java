package AuthenticationProcess.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
@Getter
@Setter
@ToString
public class ImageModel {
    private String name;
    private String type;
    private byte[] imageByte;
    ImageModel(){}
}
