package AuthenticationProcess.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ListDataUserRes {
    private String message;
    private boolean result;
    private List<UserModel> data;

    public ListDataUserRes(){}

    public ListDataUserRes(String message , boolean result ){
        this.message = message;
        this.result = result;
        this.data = data;
    }
    public ListDataUserRes(String message , boolean result , List<UserModel> data){
        this.message = message;
        this.result = result;
        this.data = data;
    }
}
