package AuthenticationProcess.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Map;

@Getter
@Setter
@ToString
public class JwtResponse {

    private String message;
    private boolean result;
    private DataRes data;

    public JwtResponse (){}

    public JwtResponse ( String status , boolean result , DataRes token) { //for success
        this.message = status;
        this.result = result;
        this.data = token;
    }
    public JwtResponse ( String status , boolean result ) { // for fail
        this.message = status;
        this.result = result;
    }
}
