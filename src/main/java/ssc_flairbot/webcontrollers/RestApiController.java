package ssc_flairbot.webcontrollers;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssc_flairbot.persistence.User;

@RestController
public class RestApiController {

    private final Logic logic;

    @Autowired
    public RestApiController(Logic logic) {
        this.logic = logic;
    }

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

    @PostMapping("/testFunction")
    public String test(Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String principalName = (String) details.get("name");
        if (principalName.equalsIgnoreCase("thorasine")) {
            logic.test();
        }
        return "Tested.";
    }

    @PostMapping("/testFunction2")
    public String test2(Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String principalName = (String) details.get("name");
        if (principalName.equalsIgnoreCase("thorasine")) {
            logic.test2();
        }
        return "Tested.";
    }
}
