package spring_oauth2_reddit;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController {

    @RequestMapping("/")
    public String startPage() {
        return "index";
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
