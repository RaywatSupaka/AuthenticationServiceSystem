package AuthenticationProcess.repository;

import AuthenticationProcess.entity.UserWebsiteStatusEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserWebsiteStatusRepository extends MongoRepository<UserWebsiteStatusEntity, String> {
    UserWebsiteStatusEntity findByUserId(String userId);

    List<UserWebsiteStatusEntity> findByWebsiteId(String websiteId);

    UserWebsiteStatusEntity findOneByWebsiteId(String websiteId);
}