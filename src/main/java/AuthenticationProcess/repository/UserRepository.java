package AuthenticationProcess.repository;

import AuthenticationProcess.entity.UserEntity;
import AuthenticationProcess.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity,String> {
    Optional<UserEntity> findByUsername(String username);

}
