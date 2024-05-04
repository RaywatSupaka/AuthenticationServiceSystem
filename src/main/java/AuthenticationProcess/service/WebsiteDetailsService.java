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

    @Autowired
    private WebsiteRepository websiteRepository;


    //service

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