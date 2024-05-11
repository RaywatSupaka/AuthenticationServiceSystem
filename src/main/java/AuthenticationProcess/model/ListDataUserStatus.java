package AuthenticationProcess.model;

import AuthenticationProcess.entity.UserWebsiteStatusEntity;

import java.util.List;

public class ListDataUserStatus {
    private String message;
    private boolean result;
    private List<UserWebsiteStatusEntity> data;

    public ListDataUserStatus(){}

    public ListDataUserStatus(String message , boolean result){
        this.message = message;
        this.result = result;
    }

    public ListDataUserStatus(String message , boolean result , List<UserWebsiteStatusEntity> data){
        this.message = message;
        this.result = result;
        this.data = data;
    }
}
