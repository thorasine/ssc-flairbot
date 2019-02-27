package spring_oauth2_reddit.webservice;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import spring_oauth2_reddit.Logic;
import spring_oauth2_reddit.persistence.User;

@Controller
public class UserController {
    
    @Autowired
    private Logic logic;

    @RequestMapping("/summonerCards")
    public String summonerCards(Model model, Principal principal) {
        List<User> users = logic.getUserAccounts(principal);
        model.addAttribute("users", users);
        return "fragments :: summonerCards";
    }

    @RequestMapping("/")
    public String startPage(Model model, Principal principal) {
        model.addAttribute("user", new User());
        return "index";
    }

    @RequestMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }
}
