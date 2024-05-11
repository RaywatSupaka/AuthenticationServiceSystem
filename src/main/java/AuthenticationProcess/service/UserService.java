package AuthenticationProcess.service;

import AuthenticationProcess.entity.UserEntity;
import AuthenticationProcess.entity.UserWebsiteStatusEntity;
import AuthenticationProcess.entity.WebsiteEntity;
import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.model.UserWebsiteStatusModel;
import AuthenticationProcess.repository.UserRepository;
import AuthenticationProcess.repository.UserWebsiteStatusRepository;
import AuthenticationProcess.repository.WebsiteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebsiteRepository websiteRepository;
    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserWebsiteStatusRepository userWebsiteStatusRepository;

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



    public boolean saveStatusStart(String userNid) {
        Optional<UserEntity> user = userRepository.findByNid(userNid);
        List<WebsiteEntity> websites = websiteRepository.findAll();

        if (user == null || user.isEmpty()) {
            return false;
        }

        List<String> websiteIds = websites.stream()
                .map(WebsiteEntity::getWid)
                .collect(Collectors.toList());

        Map<String, Boolean> websiteStatusMap = new HashMap<>();
        for (String websiteId : websiteIds) {
            websiteStatusMap.put(websiteId, false);
        }

        String jsonWebsiteStatus;
        try {
            jsonWebsiteStatus = new ObjectMapper().writeValueAsString(websiteStatusMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }

        UserWebsiteStatusEntity userWebsiteStatus = new UserWebsiteStatusEntity();
        userWebsiteStatus.setUserId(userNid);
        userWebsiteStatus.setWebsiteId(jsonWebsiteStatus);

        userWebsiteStatusRepository.save(userWebsiteStatus);
        return true;
    }

    public boolean updateStatus(String userNID, String websiteId) {
        try {
            UserWebsiteStatusEntity userNid = userWebsiteStatusRepository.findByUserId(userNID);

            if (userNid != null) {
                Object websiteIdJson = userNid.getWebsiteId();
                String websiteIdString = websiteIdJson.toString();

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Boolean> websiteIdMap = objectMapper.readValue(websiteIdString, new TypeReference<Map<String, Boolean>>() {});

                websiteIdMap.put(websiteId, true);

                String updatedWebsiteIdJson = objectMapper.writeValueAsString(websiteIdMap);

                userNid.setWebsiteId(updatedWebsiteIdJson);
                userWebsiteStatusRepository.save(userNid);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkStatus(String userNID, String websiteId) {
        try {
            UserWebsiteStatusEntity userNid = userWebsiteStatusRepository.findByUserId(userNID);

            if (userNid != null) {
                Object websiteIdJson = userNid.getWebsiteId();
                String websiteIdString = websiteIdJson.toString();

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Boolean> websiteIdMap = objectMapper.readValue(websiteIdString, new TypeReference<Map<String, Boolean>>() {});

                // เรียกใช้ฟังก์ชันเพื่อตรวจสอบว่า websiteId นี้มีค่าเป็น true หรือไม่
                boolean isTrue = checkIfTrue(websiteIdMap, websiteId);

                return isTrue;
            } else {
                return false; // บอกว่าไม่พบ userNID ที่ระบุ
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // บอกว่าเกิดข้อผิดพลาด
        }
    }

    // ฟังก์ชันตรวจสอบว่าค่าใน Map เป็น true หรือไม่
    private boolean checkIfTrue(Map<String, Boolean> websiteIdMap, String websiteId) {
        // ตรวจสอบว่า websiteId นี้มีค่าเป็น true อยู่แล้วหรือไม่
        if (websiteIdMap.containsKey(websiteId) && websiteIdMap.get(websiteId)) {
            return true;
        } else {
            return false;
        }
    }


}
