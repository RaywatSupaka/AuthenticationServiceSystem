package AuthenticationProcess.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.stereotype.Service;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "York",
                        email = "raywat@gmail.com",
                        url = "https://www.facebook.com/raywat.supaka"
                ),
                description = "API Document For Authentication Service System",
                title = "API Document",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "http://some-url.com"

                ),
                termsOfService = "Terma of service"

        ),
        servers = {
                @Server(
                        description = "Local DEV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "https://www.facebook.com/raywat.supaka"
                )
        }
)
public class SwaggerConfiguration {
}
