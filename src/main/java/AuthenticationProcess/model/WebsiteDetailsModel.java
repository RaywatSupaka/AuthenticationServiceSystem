package AuthenticationProcess.model;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.util.Date;

@Getter
@Setter
public class WebsiteDetailsModel {
    private String wid;
//    @NotEmpty(message = "the name is request")
    private String wname;
//    @NotEmpty(message = "the local is request")
    private String local;
//    @NotEmpty(message = "the status is request")
//    @Min(0)
    private String status;
//    @NotEmpty(message = "the description is request")
//    @Size(min = 10, message = "The discription should be at lest 10 characters")
//    @Size(max = 100, message = "The discription cannot exceed 100 characters")
    private String description;
    private String image;
    private String type;

    private byte[] imageShow;

}
