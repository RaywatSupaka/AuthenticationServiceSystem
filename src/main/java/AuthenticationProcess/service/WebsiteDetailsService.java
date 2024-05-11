package AuthenticationProcess.service;

import AuthenticationProcess.entity.ImageEntity;
import AuthenticationProcess.entity.UserWebsiteStatusEntity;
import AuthenticationProcess.entity.WebsiteEntity;
import AuthenticationProcess.model.WebsiteDetailsModel;
import AuthenticationProcess.repository.ImageRepository;
import AuthenticationProcess.repository.UserWebsiteStatusRepository;
import AuthenticationProcess.repository.WebsiteRepository;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.stream.Collectors;

@Service
public class WebsiteDetailsService {

    @Autowired
    private WebsiteRepository websiteRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserWebsiteStatusRepository userWebsiteStatusRepository;
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

    private WebsiteDetailsModel toModel(WebsiteEntity websiteEntity) throws IOException {
        if(websiteEntity == null){
            return null;
        }

        WebsiteDetailsModel model = new WebsiteDetailsModel();

        model.setWid(websiteEntity.getWid());
        model.setWname(websiteEntity.getWname());
        model.setLocal(websiteEntity.getLocal());
        model.setStatus(Integer.toString(websiteEntity.getStatus()));
        model.setType(websiteEntity.getType());
        model.setDescription(websiteEntity.getDescription());

        ImageEntity imageEntity = imageRepository.findByName(websiteEntity.getImg());
        if (imageEntity != null) {
            byte[] imageBytes = imageEntity.getImageByte();
            model.setImageShow(imageBytes);
        } else {
            System.out.println("ไม่พบไฟล์รูปภาพที่ค้นหา");
        }
        return model;
    }

    public List<WebsiteDetailsModel> findWebsitesWithTrueStatus(String userNID) {
        try {
            UserWebsiteStatusEntity userWebsiteStatus = userWebsiteStatusRepository.findByUserId(userNID);

            if (userWebsiteStatus != null) {
                Object websiteIdJson = userWebsiteStatus.getWebsiteId();
                String websiteIdString = websiteIdJson.toString();

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Boolean> websiteIdMap = objectMapper.readValue(websiteIdString, new TypeReference<Map<String, Boolean>>() {});

                List<String> trueWebsiteIds = websiteIdMap.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                List<WebsiteEntity> listEntity = websiteRepository.findAllById(trueWebsiteIds);
                List<WebsiteDetailsModel> listModel = new ArrayList<>();

                for(WebsiteEntity entity : listEntity){
                    WebsiteDetailsModel model = toModel(entity);
                    listModel.add(model);
                }

                return listModel;

            } else {
                // Handle case where userNID not found
                return Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}