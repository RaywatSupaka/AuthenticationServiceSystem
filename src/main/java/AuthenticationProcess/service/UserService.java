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

import java.util.Date;
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

        String validationMessage = validateUserData(user);
        boolean validateduplicate = validateduplicate(user);
        if (!validationMessage.isEmpty()) {
            return validationMessage;
        }else if (validateduplicate) {
            return "Nid is already used ";
        }else {
            UserEntity userinfo = new UserEntity();

            userinfo.setUid(UUID.randomUUID().toString().split("-")[0]);
            userinfo.setNid(user.getNid());
            userinfo.setUsername(user.getUsername());
            userinfo.setPassword(passwordEncoder.encode(user.getPassword()));
            //userinfo.setPassword(user.getPassword());
            userinfo.setAge(user.getAge());
            userinfo.setAddress(user.getAddress());
            if(user.getRoles() == null || user.getRoles().isEmpty()){
                userinfo.setRoles("USER");
            }else {
                userinfo.setRoles(user.getRoles());
            }
            userinfo.setTs(new Date());

            userRepository.save(userinfo);
            return validationMessage;
        }
    }


    public UserModel findByUsername(String username){
        Optional<UserEntity> userInfo = userRepository.findByUsername(username);

        if(userInfo.isPresent()){
            UserEntity user = userInfo.get();

            UserModel userRes = new UserModel();
            userRes.setUid(user.getUid());
            userRes.setNid(user.getNid());
            userRes.setUsername(user.getUsername());
            userRes.setPassword(user.getPassword());
            userRes.setAge(user.getAge());
            userRes.setAddress(user.getAddress());
            userRes.setRoles(user.getRoles());

            return userRes;
        } else {
            throw new IllegalArgumentException("Invalid id: " + username);
        }
    }

    public String UpdateUser(UserModel updateuser) {
        Optional<UserEntity> userInfo = userRepository.findById(updateuser.getUid());

        if (userInfo.isPresent()) {
            UserEntity userRes = new UserEntity();
            userRes.setUid(updateuser.getUid());
            userRes.setNid(updateuser.getNid());
            userRes.setUsername(updateuser.getUsername());
            userRes.setPassword(userInfo.get().getPassword());
            userRes.setAge(updateuser.getAge());
            userRes.setAddress(updateuser.getAddress());
            userRes.setRoles(userInfo.get().getRoles());

            userRepository.save(userRes);
            return "EDITE INFO SUCCESS";
        } else {
            throw new IllegalArgumentException("Invalid This Uid: " + updateuser.getUid());
        }
    }


    public boolean validateduplicate(UserModel user){
        Optional<UserEntity> users = userRepository.findByNid(user.getNid());
        return users.isPresent();
    }

    private String validateUserData(UserModel user) {
        StringBuilder validationMessage = new StringBuilder();

        // ตรวจสอบ null
        if (user.getNid() == null || user.getNid().isEmpty()) {
            validationMessage.append("Nid is null. ");
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            validationMessage.append("Username is null. ");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            validationMessage.append("Password is null. ");
        }
        if (user.getAge() == null) {
            validationMessage.append("Age is null. ");
        }
        if (user.getAddress() == null || user.getAddress().isEmpty()) {
            validationMessage.append("Address is null. ");
        }
//        if (user.getRoles() == null) {
//            validationMessage.append("Roles is null. ");
//        }

        // ตรวจสอบความยาว
        if (user.getNid().length() > 13) {
            validationMessage.append("Nid exceeds the maximum length. ");
        }
        if (user.getUsername().length() > 50) {
            validationMessage.append("Username exceeds the maximum length. ");
        }
        if (user.getPassword().length() > 15) {
            validationMessage.append("Password exceeds the maximum length. ");
        }
        if (user.getAge().toString().length() > 3) {
            validationMessage.append("Age exceeds the maximum length. ");
        }
        if (user.getAddress().length() > 75) {
            validationMessage.append("Address exceeds the maximum length. ");
        }
//        if (user.getRoles().length() > 10) {
//            validationMessage.append("Roles exceeds the maximum length. ");
//        }

        // ตรวจสอบความถูกต้องเพิ่มเติม (ตามความต้องการของคุณ)

        return validationMessage.toString();
    }

}
