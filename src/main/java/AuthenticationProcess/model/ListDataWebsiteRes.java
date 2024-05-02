package AuthenticationProcess.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
public class ListDataWebsiteRes {
    private String message;
    private boolean result;
    private List<WebsiteDetailsModel> data;

    public ListDataWebsiteRes(){}

    public ListDataWebsiteRes(String message , boolean result){
        this.message = message;
        this.result = result;
    }

    public ListDataWebsiteRes(String message , boolean result , List<WebsiteDetailsModel> data){
        this.message = message;
        this.result = result;
        this.data = data;
    }

}
