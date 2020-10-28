package ssc_flairbot.webcontrollers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ssc_flairbot.persistence.User;

/**
 * Class that handles the website's pages.
 *
 * @author Thorasine
 */
@Controller
public class WebController {

    private final WebLogic webLogic;

    @Autowired
    public WebController(WebLogic webLogic) {
        this.webLogic = webLogic;
    }

    /**
     * Return the index page of the site with a fresh User object for the purpose of adding new summoners.
     *
     * @param model     the site's model
     * @param principal the user's principal
     * @return index.html
     */
    @RequestMapping("/")
    public String startPage(Model model, Principal principal) {
        model.addAttribute("user", new User());
        return "index";
    }

    /**
     * Return the cards that represents the user's summoners (in-game accounts) attached to their account.
     *
     * @param model     the site's model
     * @param principal the user's principal
     * @return the summonerCards thymeleaf fragment in fragments.html
     */
    @RequestMapping("/summonerCards")
    public String summonerCards(Model model, Principal principal) {
        List<User> users = webLogic.getUserAccounts(principal);
        model.addAttribute("users", users);
        return "fragments :: summonerCards";
    }

    /**
     * Return the login page of the site.
     *
     * @param model the site's model
     * @return login.html
     */
    @RequestMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    /**
     * Return the test page (for testing purposes)
     *
     * @return test.html
     */
    @RequestMapping("/test")
    public String test() {
        return "test";
    }
}
