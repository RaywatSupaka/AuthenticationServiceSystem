package AuthenticationProcess.controller;

import AuthenticationProcess.model.ListDataWebsiteRes;
import AuthenticationProcess.model.WebsiteDetailsModel;
import AuthenticationProcess.repository.WebsiteRepository;
import AuthenticationProcess.service.WebsiteDetailsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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


    @PostMapping(value = {"/addnewweb"},consumes =
            {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String insertWeb(@RequestPart("websiteDetailsModel")WebsiteDetailsModel websiteDetailsModel,
                            @RequestPart("image") MultipartFile image) throws Exception {
        try {
            String imageUrl = null;
            if(image != null){
                imageUrl = websiteDetailsService.storeFile(image);
                websiteDetailsModel.setImage(imageUrl);
            }
            websiteDetailsService.createWebsite(websiteDetailsModel);
            return "Success";
        }catch (Exception e){
            throw new Exception("Fail to execute: " + e.getMessage());
        }

    }

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

}
