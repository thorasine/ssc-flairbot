package ssc_flairbot.webcontrollers;

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

/**
 * Class that processes incoming user requests.
 *
 * @author Thorasine
 */
@Component
public class WebLogic {

    private final Logger logger = Logger.getLogger(WebLogic.class.getName());
    private final DBHandler database;
    private final LeagueApi lolApi;

    @Autowired
    public WebLogic(DBHandler database, LeagueApi lolApi) {
        this.database = database;
        this.lolApi = lolApi;
    }

    /**
     * Add a user to the database if it have passed the checks.
     *
     * @param user the user we want to add
     * @return "ok" string if every check have passed, an error message otherwise
     */
    String addUser(User user) {
        // user's summoner is a name
        if (user.getSummonerName().equals("")) {
            return "You must fill the Summoner Name section!";
        }

        // summoner (in-game account) exists
        Summoner summoner = lolApi.getSummoner(user);
        if (summoner == null) {
            return "Summoner " + user.getSummonerName() + " (" + user.getServer() + ") does not exists.";
        }
        user.setSummonerName(summoner.getName());
        user.setSummonerId(summoner.getSummonerId());

        // user have already have this summoner added
        if (database.isSummonerAlreadyRegisteredByUser(user)) {
            return "You have already registered this account.";
        }

        // someone else have already verified this summoner on their account
        if (database.isSummonerAlreadyValidatedBySomeone(user)) {
            return "Summoner is already validated by someone else.";
        }

        // user passed the checks, complete the registration
        user.setValidationCode(randomStringGenerator());
        user.setValidated("pending");
        user.setValidationTries(0);
        user.setRank(lolApi.getRank(user));
        database.addUser(user);
        logger.log(Level.INFO, "Created user: /u/" + user.getRedditName() + " " + user.getSummonerName() + " (" +
                user.getServer() + ") " + "Highest rank: " + user.getRank());
        return "ok";
    }

    /**
     * Delete a user from the database if it have passed the checks.
     *
     * @param redditName the user's account name
     * @param id         the ID of the account we want to delete
     * @return "ok" string if every check have passed, an error message otherwise
     */
    String deleteUser(String redditName, Long id) {
        User user;
        try {
            user = database.getUserById(id);
        } catch (EmptyResultDataAccessException ex) {
            return "The account you tried to delete doesn't exists.";
        }

        if (!redditName.equals(user.getRedditName())) {
            return "The account you tried to delete is not yours!";
        }
        database.deleteUser(id);
        logger.log(Level.INFO, "Deleted user: /u/" + user.getRedditName() + " " + user.getSummonerName() + " (" +
                user.getServer() + ")");
        return "ok";
    }

    /**
     * Return a list that contains all of the user's accounts in an ordered fashion.
     *
     * @param principal the user's principal (for the username)
     * @return a list of the user's account.
     */
    List<User> getUserAccounts(Principal principal) {
        Map<?, ?> details = (Map<?, ?>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        String redditName = (String) details.get("name");
        List<User> accounts = database.getAccountsByRedditName(redditName);
        accounts.sort(Comparator.comparing(User::getId));
        return accounts;
    }

    /**
     * Return a 5 long random string. Used to generate a verification string for third party verification.
     *
     * @return a randomly generated string
     */
    private String randomStringGenerator() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    /**
     * Method used for testing purposes.
     */
    void test() {
    }

    /**
     * Method used for testing purposes.
     */
    void test2() {
    }

}
