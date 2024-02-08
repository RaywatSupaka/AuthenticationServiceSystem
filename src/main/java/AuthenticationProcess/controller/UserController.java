package AuthenticationProcess.controller;

import AuthenticationProcess.entity.AuthRequest;
import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.service.JwtService;
import AuthenticationProcess.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/UserService")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "USER API MANAGEMENT")
public class UserController {
    @Autowired
    private UserService userservice;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @Operation(  // รายละเอียดเอาไว้แจ้งกับ AIP แต่ละเส้น
            description = "Get Token For Another API",
            summary = "Login For Get Token",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invaild Token",
                            responseCode = "403"
                    )
            }
    )

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest){
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
            return jwtService.generateToken(authenticate.getName());
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }

    }
    @Hidden
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello this my project");
    }

    @PostMapping("/adduser")
    public String createUser(@RequestBody UserModel user){
        return userservice.AddUser(user);
    }

    @GetMapping("/findAll")
    public Object findAll(){
        return userservice.FindAll();
    }

    @GetMapping("/findById/{UID}")
    public UserModel findByUID(@PathVariable String UID){
        return userservice.FindById(UID);
    }

    @PutMapping("edit")
    public String updateUser(@RequestBody UserModel userupdate){
        return userservice.UpdateUser(userupdate);
    }

    @DeleteMapping("/{UID}")
    public String DeleteById(@PathVariable String UID){
        return userservice.DeleteById(UID);
    }
}
