package AuthenticationProcess.controller;

import AuthenticationProcess.model.ListDataUserRes;
import AuthenticationProcess.model.ListDataWebsiteRes;
import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.model.WebsiteDetailsModel;
import AuthenticationProcess.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/AdminService")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin API MANAGEMENT")
public class AdminController {

    @Autowired
    AdminService adminService;

    // user
    @GetMapping("/findAllUser")
    public ListDataUserRes findAll() throws Exception {
            try {
                List<UserModel> dataRes = adminService.FindAll();
                if (dataRes.isEmpty()) {
                    return new ListDataUserRes("USER NOT Found",false,null);
                } else {
                    return new ListDataUserRes("DATA LIST USER",true,dataRes);
                }
            } catch (Exception e){
                throw new Exception("Failed to execute : " + e.getMessage());
            }
    }

    @DeleteMapping("/userdel/{UID}")
    public ListDataUserRes DeleteUserById(@PathVariable String UID) throws Exception {
        try {
            boolean delstatus = adminService.DeleteUserById(UID);

            if (delstatus) {
                return new ListDataUserRes("Delete Success",true);
            } else {
                return new ListDataUserRes("Invalid id: " + UID,false);
            }
        }catch (Exception e){
            throw new Exception("Fail to execute :" + e.getMessage());
        }
    }

    // website
    @PostMapping(value = {"/addnewweb"},consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ListDataWebsiteRes insertWeb(@RequestPart("websiteDetailsModel") WebsiteDetailsModel websiteDetailsModel,
                            @RequestPart("image") MultipartFile image) throws Exception {
        try {
            String imageUrl = null;
            if(image != null){
                imageUrl = adminService.storeFile(image);
                if (!imageUrl.equals("Please Try Again")) {
                    websiteDetailsModel.setImage(imageUrl);
                    adminService.createWebsite(websiteDetailsModel);

                    adminService.saveStatusStartForNewWebsite();
                }else {
                    return new ListDataWebsiteRes(imageUrl,false);
                }
            }
            return new ListDataWebsiteRes("Success",true);
        }catch (Exception e){
            throw new Exception("Fail to execute: " + e.getMessage());
        }
    }

    @DeleteMapping("/website/{UID}")
    public ListDataWebsiteRes DeleteWebsiteById(@PathVariable String UID) throws Exception {
        try {
            boolean check = adminService.deleteStatusStartForNewWebsite(UID);
            boolean delstatus = adminService.DeleteWebsiteById(UID);
            if (delstatus && check) {
                return new ListDataWebsiteRes("Delete Success",true);
            } else {
                return new ListDataWebsiteRes("Invalid id: " + UID,false);
            }
        }catch (Exception e){
            throw new Exception("Fail to execute :" + e.getMessage());
        }
    }



}
