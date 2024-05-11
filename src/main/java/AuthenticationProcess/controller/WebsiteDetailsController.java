package AuthenticationProcess.controller;

import AuthenticationProcess.entity.WebsiteEntity;
import AuthenticationProcess.model.JwtResponse;
import AuthenticationProcess.model.ListDataWebsiteRes;
import AuthenticationProcess.model.WebsiteDetailsModel;
import AuthenticationProcess.repository.WebsiteRepository;
import AuthenticationProcess.service.JwtService;
import AuthenticationProcess.service.WebsiteDetailsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/WebsiteDetailsService")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Website API MANAGEMENT")
public class WebsiteDetailsController {
    @Autowired
    private WebsiteDetailsService websiteDetailsService;
    @Autowired
    private WebsiteRepository websiteRepository;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/allWebsite")
    public ListDataWebsiteRes getWebsiteDetails() throws Exception {
        try {
            List<WebsiteDetailsModel> model = websiteDetailsService.allWebsiteDetails();
            if (model != null && !model.isEmpty()){
                return new ListDataWebsiteRes("Website Details",true,model);
            }else{
                return new ListDataWebsiteRes("Don't have data",false,null);
            }
        }catch (Exception e){
            throw new Exception("Fail to execute: " + e.getMessage());
        }
    }

    @GetMapping("/findByType")
    public ListDataWebsiteRes findBytype(@RequestParam("type") String type) throws Exception{
        try {
            List<WebsiteDetailsModel> model = websiteDetailsService.findByType(type);
            if (model != null && !model.isEmpty()){
                return new ListDataWebsiteRes("Website Details of: " + type,true,model);
            }else {
                return new ListDataWebsiteRes("Don't have data",false,null);
            }
        }catch (Exception e){
            throw new Exception("Fail to execute: " + e.getMessage());
        }

    }

    @GetMapping("/status")
    public ListDataWebsiteRes getWebsitesWithTrueStatus(@RequestHeader("Authorization") String authHeader) {
        String userNid = null;
        String userName = null;
        boolean isValid = false;

        if(authHeader != null && authHeader.startsWith("Bearer")){
            String token = authHeader.substring(7);
            if (jwtService.isTokenBlocked(token)) {
                return new ListDataWebsiteRes("Token Was Blocked",false);
            }
            userName = jwtService.extractUsername(token);
            isValid = jwtService.validateToken(token, userName);
            userNid = jwtService.extractNid(token);

            if (isValid) {
                List<WebsiteDetailsModel> websites = websiteDetailsService.findWebsitesWithTrueStatus(userNid);
                if (!websites.isEmpty()) {
                    return new ListDataWebsiteRes("Success",true,websites);
                } else {
                    return new ListDataWebsiteRes("Not Found Data",false);
                }
            } else {
                return new ListDataWebsiteRes("Invalid or expired token",false);
            }
        } else {
            return new ListDataWebsiteRes("Invalid Token",false);
        }
    }

}
