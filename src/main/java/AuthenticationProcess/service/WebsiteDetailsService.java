package AuthenticationProcess.service;

import AuthenticationProcess.entity.WebsiteEntity;
import AuthenticationProcess.model.WebsiteDetailsModel;
import AuthenticationProcess.repository.WebsiteRepository;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class WebsiteDetailsService {

    @Value("public/images/")
    private String uploadDir;

    @Autowired
    private WebsiteRepository websiteRepository;



    //service
    public void createWebsite(WebsiteDetailsModel websiteDetailsModel) {
        if(websiteDetailsModel == null){
            throw new IllegalArgumentException("Please fill in all required information.\n");
        }

        WebsiteEntity website = toEntity(websiteDetailsModel);
        websiteRepository.insert(website);
    }

    public List<WebsiteDetailsModel> allWebsiteDetails() throws IOException {
        List<WebsiteEntity> listEntity = websiteRepository.findAll();
        List<WebsiteDetailsModel> listModel = new ArrayList<>();

        for(WebsiteEntity entity : listEntity){
            WebsiteDetailsModel model = toModel(entity);
            listModel.add(model);
        }
        return listModel;
    }

    public List<WebsiteDetailsModel> findByType(String type) throws IOException {
        List<WebsiteEntity> entityLsit = websiteRepository.findByType(type);
        List<WebsiteDetailsModel> modelList = new ArrayList<>();

        for(WebsiteEntity entity : entityLsit){
            WebsiteDetailsModel model = toModel(entity);
            modelList.add(model);
        }
        return modelList;
    }

    //support func
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

    private WebsiteDetailsModel toModel(WebsiteEntity websiteEntity) throws IOException{
        if(websiteEntity == null){
            return null;
        }

        WebsiteDetailsModel model =  new WebsiteDetailsModel();

        model.setWid(websiteEntity.getWid());
        model.setWname(websiteEntity.getWname());
        model.setLocal(websiteEntity.getLocal());
        model.setStatus(Integer.toString(websiteEntity.getStatus()));
        model.setType(websiteEntity.getType());
        model.setDescription(websiteEntity.getDescription());
        model.setImageShow(websiteEntity.getImg() != null
                        ? imageToByteArray(websiteEntity.getImg())
                        : null);
        return model;
    }
    private byte[] imageToByteArray(String imagePath) throws IOException {
        // ใช้ FileInputStream เพื่ออ่านไฟล์รูปภาพจากที่ตั้งที่ระบุ
        FileInputStream fileInputStream = new FileInputStream(imagePath);
        // สร้าง ByteArrayOutputStream เพื่อเก็บข้อมูลรูปภาพที่อ่านได้
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        // อ่านข้อมูลจาก FileInputStream และเขียนลงใน ByteArrayOutputStream
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        // ปิด FileInputStream
        fileInputStream.close();

        // คืนค่าข้อมูลรูปภาพเป็น byte array
        return byteArrayOutputStream.toByteArray();
    }
}