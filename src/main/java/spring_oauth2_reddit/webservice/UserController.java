package spring_oauth2_reddit.webservice;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spring_oauth2_reddit.persistence.DBHandler;
import spring_oauth2_reddit.persistence.User;

@Controller
public class UserController {

    @Autowired
    private DBHandler db;

    @RequestMapping("/test")
    public String test(Model model, Principal principal) {
        model.addAttribute("user", new User());
        return "test";
    }

    @RequestMapping("/")
    public String startPage(Model model, Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String redditName = (String) details.get("name");
        List<User> users = db.getUserByRedditName(redditName);
        
        model.addAttribute("users", users);
        model.addAttribute("user", new User());
        return "index";
    }

    @RequestMapping("/newSummoner")
    public String newSummoner(@ModelAttribute User user, Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String redditName = (String) details.get("name");

        user.setRedditName(redditName);
        db.addUser(user);
        System.out.println("CREATED USER: " + user.getSummonerName());
        return "redirect:/";
    }

    @RequestMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String userName, Model model) {
        model.addAttribute("name", userName);
        return "greeting";
    }

    @RequestMapping("/mgreeting")
    public String multipleGreeting(@RequestParam Map<String, String> requestParams, Model model) {
        model.addAttribute("map", requestParams);
        return "mgreeting";
    }

}
