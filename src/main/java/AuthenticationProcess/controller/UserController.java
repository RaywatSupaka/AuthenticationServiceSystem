package AuthenticationProcess.controller;

import AuthenticationProcess.entity.AuthRequest;
import AuthenticationProcess.entity.UserWebsiteStatusEntity;
import AuthenticationProcess.model.*;
import AuthenticationProcess.repository.UserWebsiteStatusRepository;
import AuthenticationProcess.service.JwtService;
import AuthenticationProcess.service.UserService;
import AuthenticationProcess.service.WebsiteDetailsService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/UserService")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "USER API MANAGEMENT")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userservice;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserWebsiteStatusRepository userWebsiteStatusRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private WebsiteDetailsService websiteDetailsService;

    @Operation(  // รายละเอียดเอาไว้แจ้งกับ AIP แต่ละเส้น
            description = "Get Token For Another API",
            summary = "ไม่ใช้ Token"
//            ,responses = {
//                    @ApiResponse(
//                            description = "Success",
//                            responseCode = "200"
//                    ),
//                    @ApiResponse(
//                            description = "Unauthorized / Invaild Token",
//                            responseCode = "403"
//                    )
//            }
    )

    @PostMapping("/login")
    public JwtResponse login(@RequestBody AuthRequest authRequest){
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
            DataRes token = new DataRes(jwtService.generateToken(authenticate.getName()));
            return new JwtResponse("Login Success",true, token);
        } catch (AuthenticationException ex) {
            return new JwtResponse("Login Fail", false);
        }
    }

    @Operation(  // รายละเอียดเอาไว้แจ้งกับ AIP แต่ละเส้น
            description = "Get Token For Another API",
            summary = "ไม่ใช้ Token"
    )

    @PostMapping("/register")
    public ResponseEntity createUser(@RequestBody UserModel user) throws Exception {
        try {
            String dataRes = userservice.AddUser(user);
            if (dataRes.isEmpty()) {
                userservice.saveStatusStart(user.getNid());
                return ResponseEntity.ok("User added successfully.");
            } else {
                // กรณีมีข้อผิดพลาด
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Validation failed");
                response.put("details", dataRes);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e){
            throw new Exception("Failed to execute : " + e.getMessage());
        }
    }


    @GetMapping("/findById/{username}")
    public UserModel findByUID(@PathVariable String username){

        return userservice.findByUsername(username);
    }

    @PutMapping("edit")
    public String updateUser(@RequestBody UserModel userupdate){
        return userservice.UpdateUser(userupdate);
    }


    @GetMapping("/validate")
    public JwtResponse validateToken(@RequestHeader("Authorization") String authHeader) {
        String userName = null;
        boolean isValid = false;

        if(authHeader != null && authHeader.startsWith("Bearer")){
            String token = authHeader.substring(7);
            if (jwtService.isTokenBlocked(token)) {
                return new JwtResponse("Token Was Blocked",false);
            }
            userName = jwtService.extractUsername(token);
            isValid = jwtService.validateToken(token, userName);

            if (isValid) {
                return new JwtResponse("validate success", true);
            } else {
                return new JwtResponse("Invalid or expired token",false);
            }
        } else {
            return new JwtResponse("Invalid Token",false);
        }
    }

    @GetMapping("/extractRoles")
    public JwtResponse extractRoles(@RequestHeader("Authorization") String authHeader) {
        String userRole = null;
        String userName = null;
        boolean isValid = false;

        if(authHeader != null && authHeader.startsWith("Bearer")){
            String token = authHeader.substring(7);
            if (jwtService.isTokenBlocked(token)) {
                return new JwtResponse("Token Was Blocked",false);
            }
            userName = jwtService.extractUsername(token);
            isValid = jwtService.validateToken(token, userName);
            userRole = jwtService.extractRoles(token);

            if (isValid) {
                return new JwtResponse(userRole, true);
            } else {
                return new JwtResponse("Invalid or expired token",false);
            }
        } else {
            return new JwtResponse("Invalid Token",false);
        }
    }


    @PostMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String authHeader) {
        String userName = null;
        boolean isValid = false;

        if(authHeader != null && authHeader.startsWith("Bearer")){
            String token = authHeader.substring(7);
            userName = jwtService.extractUsername(token);
            isValid = jwtService.validateToken(token, userName);
            // ตรวจสอบ Token
            if (isValid) {
                jwtService.blockToken(token);
                return ResponseEntity.ok("Logout successful");
            } else {
                return ResponseEntity.badRequest().body("Invalid or expired token");
            }
        } else {
            return ResponseEntity.badRequest().body("Authorization header is missing or invalid");
        }
    }

    @PostMapping("/showallStatusUser")
    public List<UserWebsiteStatusEntity> showallStatusUser() throws Exception {
        try {
            List<UserWebsiteStatusEntity> dataRes = userWebsiteStatusRepository.findAll();
            return dataRes;
        }catch (Exception e){
            throw new Exception("Failed to execute : " + e.getMessage());
        }
    }

    @PutMapping("/updateStatus/{userId}/{websiteId}")
    public boolean updateStatus(@PathVariable String userId, @PathVariable String websiteId) {
        return userservice.updateStatus(userId, websiteId);
    }

    @GetMapping("/checkStatus/{websiteId}")
    public ListDataWebsiteRes checkStatus(@RequestHeader("Authorization") String authHeader, @PathVariable String websiteId) throws Exception {
        try {
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
                    boolean isTrue = userservice.checkStatus(userNid, websiteId);
                    if (isTrue) {
                        return new ListDataWebsiteRes("Is Member",true);
                    } else {
                        return new ListDataWebsiteRes("Isn't Member",false);
                    }
                } else {
                    return new ListDataWebsiteRes("Invalid or expired token",false);
                }
            } else {
                return new ListDataWebsiteRes("Invalid Token",false);
            }
        }catch (Exception e){
            throw new Exception("Failed to execute : " + e.getMessage());
        }

    }


}
