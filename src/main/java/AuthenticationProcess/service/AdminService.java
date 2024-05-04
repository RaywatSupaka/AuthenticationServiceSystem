package AuthenticationProcess.service;

import AuthenticationProcess.entity.UserEntity;

import AuthenticationProcess.entity.WebsiteEntity;
import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.model.WebsiteDetailsModel;
import AuthenticationProcess.repository.UserRepository;
import AuthenticationProcess.repository.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AdminService {

    @Value("public/images/")
    private String uploadDir;

    @Autowired
    UserRepository userRepository;
    @Autowired
    WebsiteRepository websiteRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserModel> FindAll() throws IOException {
        List<UserEntity> entityList = userRepository.findAll();
        List<UserModel> modelList = new ArrayList<>();
        for(UserEntity entity: entityList){
            UserModel model = toModel(entity);
            modelList.add(model);
        }
        return modelList;
    }

    public String DeleteUserById(String id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            UserEntity userDel = user.get();
            userRepository.delete(userDel);

            return "Delete Success";
        } else {
            return "Invalid id: " + id;
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

    public void createWebsite(WebsiteDetailsModel websiteDetailsModel) {
        if(websiteDetailsModel == null){
            throw new IllegalArgumentException("Please fill in all required information.\n");
        }

        WebsiteEntity website = toEntity(websiteDetailsModel);
        websiteRepository.insert(website);
    }

    public  String storeFile( MultipartFile file){
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty.");
            }

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Date tmp = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = formatter.format(tmp);
            String filename = timestamp + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(filename);

            if (!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            Files.copy(file.getInputStream(), filePath , StandardCopyOption.REPLACE_EXISTING);
            return  filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Fail to store the file. \n", e);
        }
    }

    // fc ช่วย

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
        if(userEntity == null){
            throw new IllegalArgumentException("Invalid User: ");
        }

        UserModel model =  new UserModel();
        model.setUid(userEntity.getUid());
        model.setNid(userEntity.getNid());
        model.setUsername(userEntity.getUsername());
        model.setAge(userEntity.getAge());
        model.setAddress(userEntity.getAddress());
        model.setRoles(userEntity.getRoles());

        return model;
    }


}
