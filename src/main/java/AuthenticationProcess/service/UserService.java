package AuthenticationProcess.service;

import AuthenticationProcess.entity.UserEntity;
import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.repository.UserRepository;
import jdk.jshell.Snippet;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.annotation.Repeatable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userrepository;

    // create,read,update,delete
    public String AddUser(UserModel user){
        UserEntity userinfo = new UserEntity();
        userinfo.setUid(UUID.randomUUID().toString().split("-")[0]);
        userinfo.setNid(user.getNid());
        userinfo.setUsername(user.getUsername());
        userinfo.setPassword(user.getPassword());
        userinfo.setAge(user.getAge());
        userinfo.setAddress(user.getAddress());

        userrepository.save(userinfo);
        return "SUCCESS";
    }

    public Object FindAll(){
        return userrepository.findAll();
    }

    public UserModel FindById(String id){
        Optional<UserEntity> userInfo = userrepository.findById(id);

        if(userInfo.isPresent()){
            UserEntity user = userInfo.get();

            UserModel userRes = new UserModel();
            userRes.setNid(user.getNid());
            userRes.setUsername(user.getUsername());
            userRes.setPassword(user.getPassword());
            userRes.setAge(user.getAge());
            userRes.setAddress(user.getAddress());

            return userRes;
        } else {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
    }

    public String UpdateUser(UserModel updateuser) {
        Optional<UserEntity> userInfo = userrepository.findById(updateuser.getUid());

        if (userInfo.isPresent()) {
            UserEntity userRes = new UserEntity();
            userRes.setUid(updateuser.getUid());
            userRes.setNid(updateuser.getNid());
            userRes.setUsername(updateuser.getUsername());
            userRes.setPassword(updateuser.getPassword());
            userRes.setAge(updateuser.getAge());
            userRes.setAddress(updateuser.getAddress());

            userrepository.save(userRes);
            return "EDITE INFO SUCCESS";
        } else {
            throw new IllegalArgumentException("Invalid This Uid: " + updateuser.getUid());
        }
    }

    public String DeleteById(String id) {
        Optional<UserEntity> user = userrepository.findById(id);
        if (user.isPresent()) {
            UserEntity userDel = user.get();
            userrepository.delete(userDel);

            return "DELETE SUCCESS";
        } else {
            // จัดการกรณีที่ไม่พบข้อมูลที่ต้องการลบ
            throw new IllegalArgumentException("Invalid id: " + id);
        }
    }



}
