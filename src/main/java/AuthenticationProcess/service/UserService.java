package AuthenticationProcess.service;

import AuthenticationProcess.entity.UserEntity;
import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    // test Auth Jwt Service
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userInfo = userRepository.findByUsername(username);
        return userInfo.map(UserInfoDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("User not found" + username));
    }

    // create,read,update,delete
    public String AddUser(UserModel user){
        UserEntity userinfo = new UserEntity();
        userinfo.setUid(UUID.randomUUID().toString().split("-")[0]);
        userinfo.setNid(user.getNid());
        userinfo.setUsername(user.getUsername());
        userinfo.setPassword(passwordEncoder.encode(user.getPassword()));
        userinfo.setAge(user.getAge());
        userinfo.setAddress(user.getAddress());
        userinfo.setRoles(user.getRoles());

        userRepository.save(userinfo);
        return "SUCCESS";
    }

    public Object FindAll(){
        return userRepository.findAll();
    }

    public UserModel FindById(String id){
        Optional<UserEntity> userInfo = userRepository.findById(id);

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
        Optional<UserEntity> userInfo = userRepository.findById(updateuser.getUid());

        if (userInfo.isPresent()) {
            UserEntity userRes = new UserEntity();
            userRes.setUid(updateuser.getUid());
            userRes.setNid(updateuser.getNid());
            userRes.setUsername(updateuser.getUsername());
            userRes.setPassword(updateuser.getPassword());
            userRes.setAge(updateuser.getAge());
            userRes.setAddress(updateuser.getAddress());

            userRepository.save(userRes);
            return "EDITE INFO SUCCESS";
        } else {
            throw new IllegalArgumentException("Invalid This Uid: " + updateuser.getUid());
        }
    }

    public String DeleteById(String id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            UserEntity userDel = user.get();
            userRepository.delete(userDel);

            return "DELETE SUCCESS";
        } else {
            // จัดการกรณีที่ไม่พบข้อมูลที่ต้องการลบ
            throw new IllegalArgumentException("Invalid id: " + id);
        }
    }
}
