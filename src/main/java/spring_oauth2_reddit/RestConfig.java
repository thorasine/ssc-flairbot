package spring_oauth2_reddit;

import java.security.Principal;
import java.util.Map;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestConfig {
    
    @RequestMapping("/user")
    public Principal user(Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        //System.out.println("NAME: " + details.get("name"));
        return principal;
    }
}
