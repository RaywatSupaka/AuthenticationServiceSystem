package AuthenticationProcess.repository;

import AuthenticationProcess.entity.ImageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<ImageEntity,String> {
    ImageEntity findByName(String filename);
}
