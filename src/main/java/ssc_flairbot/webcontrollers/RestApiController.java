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

/**
 * Class that handles the REST API connections.
 *
 * @author Thorasine
 */
@RestController
public class RestApiController {

    private final WebLogic webLogic;

    @Autowired
    public RestApiController(WebLogic webLogic) {
        this.webLogic = webLogic;
    }

    /**
     * Attempt to add a user's summoner (in-game account) to the user's account and save it into
     * the database.
     *
     * @param user      the user whose in-game account gets added
     * @param principal the user's principal
     * @return the response based on the outcome of the operation
     */
    @PostMapping("/addSummoner")
    public String addSummoner(User user, Principal principal) {
        Map<?, ?> details = (Map<?, ?>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String redditName = (String) details.get("name");
        user.setRedditName(redditName);
        return webLogic.addUser(user);
    }

    /**
     * Attempt to delete a user's summoner (in-game account) from the user's account.
     *
     * @param id        the ID of the summoner in the database
     * @param principal the user's principal
     * @return the response based on the outcome of the operation
     */
    @PostMapping("/deleteSummoner")
    public String deleteSummoner(@RequestParam(value = "id") Long id, Principal principal) {
        Map<?, ?> details = (Map<?, ?>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String redditName = (String) details.get("name");
        return webLogic.deleteUser(redditName, id);
    }

    /**
     * Get the princial name (aka reddit username) from the user's principal.
     *
     * @param principal the user's principal
     * @return the user's reddit name
     */
    @GetMapping("/principalName")
    public String principalName(Principal principal) {
        Map<?, ?> details = (Map<?, ?>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        return (String) details.get("name");
    }

    /**
     * Method one used for testing purposes. Only calls underlying functions if the user is named "thorasine".
     *
     * @param principal the user's principal
     * @return the string "test 1"
     */
    @PostMapping("/testFunction")
    public String test(Principal principal) {
        Map<?, ?> details = (Map<?, ?>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String principalName = (String) details.get("name");
        if (principalName.equalsIgnoreCase("thorasine")) {
            webLogic.test();
        }
        return "test 1";
    }

    /**
     * Method two used for testing purposes. Only calls underlying functions if the user is named "thorasine".
     *
     * @param principal the user's principal
     * @return the string "test 2"
     */
    @PostMapping("/testFunction2")
    public String test2(Principal principal) {
        Map<?, ?> details = (Map<?, ?>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String principalName = (String) details.get("name");
        if (principalName.equalsIgnoreCase("thorasine")) {
            webLogic.test2();
        }
        return "test 2";
    }
}
