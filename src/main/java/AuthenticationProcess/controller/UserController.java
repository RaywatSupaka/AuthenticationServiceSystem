package AuthenticationProcess.controller;

import AuthenticationProcess.entity.AuthRequest;
import AuthenticationProcess.model.DataRes;
import AuthenticationProcess.model.JwtResponse;
import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.service.JwtService;
import AuthenticationProcess.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private JwtService jwtService;

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
    public String validateToken(@RequestHeader("Authorization") String authHeader) {
        String userName = null;
        boolean isValid = false; // กำหนดให้ isValid เป็นเท็จเริ่มต้น

        if(authHeader != null && authHeader.startsWith("Bearer")){
            String token = authHeader.substring(7); // ดึง token จาก Authorization header
            userName = jwtService.extractUsername(token); // ดึงชื่อผู้ใช้จาก token
            isValid = jwtService.validateToken(token, userName); // ตรวจสอบความถูกต้องของ token
        }
        if (isValid) {
            return "Token is valid.";
        } else {
            return "Token is not valid.";
        }
    }


}
