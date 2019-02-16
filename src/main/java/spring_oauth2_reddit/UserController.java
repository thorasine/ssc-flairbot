package spring_oauth2_reddit;

import java.security.Principal;
import java.util.Map;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @RequestMapping("/")
    public String startPage(Model model) {
        return "index";
    }

    @RequestMapping("/user2")
    public String user2(Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        System.out.println("NAME: " + details.get("name"));
        return "greeting";
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
