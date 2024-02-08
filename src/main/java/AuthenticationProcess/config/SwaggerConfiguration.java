package AuthenticationProcess.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

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
//        ,security = { //ทำให้ Controller ทั้งหมดใช้ Auth เดียวกัน
//                @SecurityRequirement(
//                        name = "bearerAuth"
//                )
//        }

)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfiguration {
}
