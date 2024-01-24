package AuthenticationProcess.controller;

import AuthenticationProcess.model.UserModel;
import AuthenticationProcess.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/UserService")
public class UserController {
    @Autowired
    private UserService userservice;

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello this my project");
    }

    @PostMapping
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
