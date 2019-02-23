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
import spring_oauth2_reddit.persistence.DBHandler;
import spring_oauth2_reddit.persistence.User;

@RestController
public class RestApiController {

    @Autowired
    DBHandler db;

    @PostMapping("/addSummoner")
    public String addSummoner2(User user, Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String redditName = (String) details.get("name");

        user.setRedditName(redditName);
        db.addUser(user);
        System.out.println("CREATED USER: " + user.getSummonerName());
        return "WOOO IT WORKED";
    }

    @GetMapping("/principalName")
    public String principalName(Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String principalName = (String) details.get("name");
        return principalName;
    }

    @GetMapping("/userById")
    public User userById(@RequestParam(value = "id") Long id) {
        Long longId = 1L;
        return db.getUserById(id);
    }

    @GetMapping("/allUsers")
    public Collection<User> allUsers() {
        return db.getAllUsers();
    }
}
