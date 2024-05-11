package AuthenticationProcess.service;

import AuthenticationProcess.entity.ImageEntity;
import AuthenticationProcess.entity.UserEntity;

import AuthenticationProcess.entity.UserWebsiteStatusEntity;
import AuthenticationProcess.entity.WebsiteEntity;
import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.model.WebsiteDetailsModel;
import AuthenticationProcess.repository.ImageRepository;
import AuthenticationProcess.repository.UserRepository;
import AuthenticationProcess.repository.UserWebsiteStatusRepository;
import AuthenticationProcess.repository.WebsiteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    WebsiteRepository websiteRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    private UserWebsiteStatusRepository userWebsiteStatusRepository;
    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserModel> FindAll() throws IOException {
        List<UserEntity> entityList = userRepository.findAll();
        List<UserModel> modelList = new ArrayList<>();
        for (UserEntity entity : entityList) {
            UserModel model = toModel(entity);
            modelList.add(model);
        }
        return modelList;
    }

    public boolean DeleteUserById(String id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            UserEntity userDel = user.get();
            userRepository.delete(userDel);

            UserWebsiteStatusEntity userWebsiteStatus = userWebsiteStatusRepository.findByUserId(userDel.getNid());
            if (userWebsiteStatus != null) {
                userWebsiteStatusRepository.delete(userWebsiteStatus);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean DeleteWebsiteById(String id) {
        Optional<WebsiteEntity> website = websiteRepository.findById(id);
        if (website.isPresent()) {
            WebsiteEntity websiteDel = website.get();


            String imagePath = websiteDel.getImg();
            if (imagePath != null) {
                try {
                    Path imagePathToDelete = Paths.get(imagePath);
                    Files.deleteIfExists(imagePathToDelete);
                } catch (IOException e) {
                    // การลบรูปภาพเกิดข้อผิดพลาด
                    // คุณสามารถจัดการข้อผิดพลาดนี้ตามความเหมาะสม
                    e.printStackTrace();
                }
            }

            websiteRepository.delete(websiteDel);
            return true;
        } else {
            return false;
        }
    }

    public UserModel findByUsername(String username) {
        Optional<UserEntity> userInfo = userRepository.findByUsername(username);

        if (userInfo.isPresent()) {
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

    public void createWebsite(WebsiteDetailsModel websiteDetailsModel) {
        if (websiteDetailsModel == null) {
            throw new IllegalArgumentException("Please fill in all required information.\n");
        }

        WebsiteEntity website = toEntity(websiteDetailsModel);
        websiteRepository.insert(website);
    }


    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty.");
            }

            // Convert MultipartFile to byte array
            byte[] fileData = file.getBytes();

            // Generate unique filename based on timestamp
            Date timestamp = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = formatter.format(timestamp) + "_" + StringUtils.cleanPath(file.getOriginalFilename());

            ImageEntity existingImage = imageRepository.findByName(filename);
            if (existingImage == null) {
                ImageEntity imageEntity = ImageEntity.builder()
                        .name(filename)
                        .type(file.getContentType())
                        .imageByte(fileData)
                        .uploadDate(timestamp)
                        .build();
                imageRepository.save(imageEntity);

                return filename;
            } else {
                return "Please Try Again";
            }
        } catch (IOException e) {
            throw new RuntimeException("Fail to store the file. \n", e);
        }
    }

    // fc ช่วย

    public static byte[] convertFileToByteArray(MultipartFile file) throws IOException {
        byte[] buffer = new byte[1024];
        try (InputStream inputStream = file.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }

    private WebsiteEntity toEntity(WebsiteDetailsModel websiteDetailsModel) {
        return WebsiteEntity.builder()
                .wid(UUID.randomUUID().toString().split("-")[0])
                .wname(websiteDetailsModel.getWname())
                .local(websiteDetailsModel.getLocal())
                .status(Integer.parseInt(websiteDetailsModel.getStatus()))
                .description(websiteDetailsModel.getDescription())
                .type(websiteDetailsModel.getType())
                .tmp(new Date())
                .img(websiteDetailsModel.getImage())
                .build();
    }

    private UserModel toModel(UserEntity userEntity) throws IOException {
        if (userEntity == null) {
            throw new IllegalArgumentException("Invalid User: ");
        }

        UserModel model = new UserModel();
        model.setUid(userEntity.getUid());
        model.setNid(userEntity.getNid());
        model.setUsername(userEntity.getUsername());
        model.setAge(userEntity.getAge());
        model.setAddress(userEntity.getAddress());
        model.setRoles(userEntity.getRoles());

        return model;
    }

    public boolean saveStatusStartForNewWebsite() {
        List<UserEntity> users = userRepository.findAll();
        List<WebsiteEntity> websites = websiteRepository.findAll();

        for (UserEntity user : users) {
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
                e.printStackTrace(); // แสดง error กรณีไม่สามารถแปลงเป็น JSON ได้
                return false;
            }

            // ค้นหา userWebsiteStatus ที่มี userId เท่ากับ nid ของ user
            UserWebsiteStatusEntity existingUserWebsiteStatus = userWebsiteStatusRepository.findByUserId(user.getNid());

            // ถ้ามี userWebsiteStatus ที่เก่าอยู่แล้ว
            if (existingUserWebsiteStatus != null) {
                existingUserWebsiteStatus.setWebsiteId(jsonWebsiteStatus); // กำหนดค่า websiteId ใหม่
                userWebsiteStatusRepository.save(existingUserWebsiteStatus); // บันทึกการเปลี่ยนแปลง
            }
        }

        return true;
    }
    public boolean deleteStatusStartForNewWebsite(String uid) {
        try {
            List<UserWebsiteStatusEntity> userWebsiteStatusList = userWebsiteStatusRepository.findAll();
            for (UserWebsiteStatusEntity userWebsiteStatus : userWebsiteStatusList) {
                Object websiteIdJson = userWebsiteStatus.getWebsiteId();
                // แปลง JSON string เป็น string ก่อน
                String websiteIdString = websiteIdJson.toString();
                // แปลง string เป็น Map
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Boolean> websiteIdMap = objectMapper.readValue(websiteIdString, new TypeReference<Map<String, Boolean>>() {});
                // ตรวจสอบว่ามี uid อยู่ใน Map หรือไม่
                if (websiteIdMap.containsKey(uid)) {
                    // ลบ uid ออกจาก Map
                    websiteIdMap.remove(uid);

                    // แปลง Map กลับเป็น JSON string
                    String updatedWebsiteIdJson = objectMapper.writeValueAsString(websiteIdMap);

                    // อัพเดทข้อมูลใหม่ลงใน UserWebsiteStatusEntity และบันทึกลงในฐานข้อมูล
                    userWebsiteStatus.setWebsiteId(updatedWebsiteIdJson);
                    userWebsiteStatusRepository.save(userWebsiteStatus);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

