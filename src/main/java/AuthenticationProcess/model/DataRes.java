package AuthenticationProcess.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DataRes {

    private String token;

    public DataRes (){}
    public DataRes(String token){
        this.token = token;
    }
}
