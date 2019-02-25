package spring_oauth2_reddit;

import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring_oauth2_reddit.persistence.DBHandler;
import spring_oauth2_reddit.persistence.User;

@Component
public class Logic {

    @Autowired
    DBHandler db;

    public String addUser(User user) {
        //Check if validated one already exists
        List<User> users = db.getUserBySummonerName(user.getSummonerName());
        for (User u : users) {
            if (u.getServer().equals(user.getServer())) {
                if (u.getValidated().equals("validated")) {
                    return "Summoner is already registered.";
                }
                if (u.getRedditName().equals(user.getRedditName())) {
                    return "You've already added this account";
                }
            }
        }

        //User passed the checks, complete registration
        user.setValidationCode(randomString());
        user.setValidated("pending");
        user.setValidationTries(0);
        db.addUser(user);
        System.out.println("CREATED USER: /u/" + user.getRedditName() + " " + user.getSummonerName() + " (" + user.getServer() + ")");

        return "ok";
    }

    private String randomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

}
