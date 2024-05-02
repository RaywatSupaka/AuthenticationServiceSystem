package AuthenticationProcess.repository;

import AuthenticationProcess.entity.WebsiteEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebsiteRepository extends MongoRepository<WebsiteEntity,String> {
    List<WebsiteEntity> findByType(String type);
}
