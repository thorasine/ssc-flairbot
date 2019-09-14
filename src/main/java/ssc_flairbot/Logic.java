package ssc_flairbot;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.stelar7.api.l4j8.pojo.summoner.Summoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.reddit.FlairUpdater;

@Component
public class Logic {

    @Autowired
    DBHandler db;
    @Autowired
    LeagueApi lolApi;
    @Autowired
    FlairUpdater flairUpdater;

    public String addUser(User user) {
        //Check if user's summoner is a name
        if (user.getSummonerName().equals("")) {
            return "You must fill the Summoner Name section!";
        }

        //Check if summoner exists
        Summoner summoner = lolApi.getSummoner(user);
        if (summoner == null) {
            return "Summoner " + user.getSummonerName() + " (" + user.getServer() + ") does not exists.";
        }
        user.setSummonerName(summoner.getName());
        user.setSummonerId(summoner.getSummonerId());

        //Check if he have already added this summoner
        if (db.isSummonerAlreadyRegisteredByUser(user)) {
            return "You have already registered this account.";
        }
        //Check if validated one already exists
        if (db.isSummonerAlreadyValidatedBySomeone(user)) {
            return "Summoner is already validated by someone else.";
        }

        //User passed the checks, complete registration
        user.setValidationCode(randomStringGenerator());
        user.setValidated("pending");
        user.setValidationTries(0);
        user.setRank(lolApi.getHighestRank(user));
        db.addUser(user);
        Logger.getLogger(Logic.class.getName()).log(Level.INFO, "Created user: /u/" + user.getRedditName() + " " + user.getSummonerName() + " (" + user.getServer() + ") " + "Highest rank: " + user.getRank());
        return "ok";
    }

    public String deleteUser(String redditName, Long id) {
        User user;
        try {
            user = db.getUserById(id);
        } catch (EmptyResultDataAccessException ex) {
            return "The account you tried to delete doesn't exists.";
        }

        if (!redditName.equals(user.getRedditName())) {
            return "The account you tried to delete is not yours!";
        }
        db.deleteUser(id);
        Logger.getLogger(Logic.class.getName()).log(Level.INFO, "Deleted user: /u/" + user.getRedditName() + " " + user.getSummonerName() + " (" + user.getServer() + ")");
        return "ok";
    }

    public List<User> getUserAccounts(Principal principal) {
        Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String redditName = (String) details.get("name");
        List<User> users = db.getUsersByRedditName(redditName);
        users.sort(Comparator.comparing(User::getId));
        return users;
    }

    public void test() {
        flairUpdater.test();
    }

    public void test2(){
        flairUpdater.test2();
    }

    private String randomStringGenerator() {
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
