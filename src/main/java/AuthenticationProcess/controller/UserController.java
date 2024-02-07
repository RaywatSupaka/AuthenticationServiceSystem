package AuthenticationProcess.controller;

import AuthenticationProcess.entity.AuthRequest;
import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.service.JwtService;
import AuthenticationProcess.service.UserService;
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
public class UserController {
    @Autowired
    private UserService userservice;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest){
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
            return jwtService.generateToken(authenticate.getName());
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }

    }
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello this my project");
    }

    @PostMapping("/adduser")
    public String createUser(@RequestBody UserModel user){
        return userservice.AddUser(user);
    }

    @GetMapping
    public Object findAll(){
        return userservice.FindAll();
    }

    @GetMapping("/{UID}")
    public UserModel findByUID(@PathVariable String UID){
        return userservice.FindById(UID);
    }

    @PutMapping
    public String updateUser(@RequestBody UserModel userupdate){
        return userservice.UpdateUser(userupdate);
    }

    @DeleteMapping("/{UID}")
    public String DeleteById(@PathVariable String UID){
        return userservice.DeleteById(UID);
    }
}
