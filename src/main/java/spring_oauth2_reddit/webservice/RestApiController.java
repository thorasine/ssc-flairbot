package spring_oauth2_reddit.webservice;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring_oauth2_reddit.Logic;
import spring_oauth2_reddit.persistence.DBHandler;
import spring_oauth2_reddit.persistence.User;

@RestController
public class RestApiController {

    @Autowired
    DBHandler db;
    
    @Autowired
    Logic logic;

    @PostMapping("/addSummoner")
    public String addSummoner(User user, Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String redditName = (String) details.get("name");
        user.setRedditName(redditName);
        String response = logic.addUser(user);
        return response;
    }

    @PostMapping("/deleteSummoner")
    public String deleteSummoner(@RequestParam(value = "id") Long id, Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String redditName = (String) details.get("name");
        String response = logic.deleteUser(redditName, id);
        return response;
    }

    @GetMapping("/principalName")
    public String principalName(Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String principalName = (String) details.get("name");
        return principalName;
    }
}
