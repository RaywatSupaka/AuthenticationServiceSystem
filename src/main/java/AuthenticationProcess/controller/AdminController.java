package AuthenticationProcess.controller;

import AuthenticationProcess.model.ListDataUserRes;
import AuthenticationProcess.model.ListDataWebsiteRes;
import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/AdminService")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin API MANAGEMENT")
public class AdminController {

    @Autowired
    AdminService adminService;

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

    @DeleteMapping("/user/{UID}")
    public String DeleteUserById(@PathVariable String UID) throws Exception {
        try {
            return adminService.DeleteUserById(UID);
        }catch (Exception e){
            throw new Exception("Fail to execute :" + e.getMessage());
        }
    }

    @DeleteMapping("/website/{UID}")
    public ListDataWebsiteRes DeleteWebsiteById(@PathVariable String UID) throws Exception {
        try {
            boolean delstatus = adminService.DeleteWebsiteById(UID);
            if (delstatus) {
                return new ListDataWebsiteRes("Delete Success",true);
            } else {
                return new ListDataWebsiteRes("Invalid id: " + UID,false);
            }
        }catch (Exception e){
            throw new Exception("Fail to execute :" + e.getMessage());
        }
    }



}
