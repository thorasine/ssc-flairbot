package ssc_flairbot;

import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;
import ssc_flairbot.persistence.DBHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {

    @Autowired
    private DBHandler db;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) {
        db.dropTable();
        db.createTable();

        User user1 = new UserBuilder().redditName("Thorasine").summonerName("Trefort")
                .summonerId("8kFIUtL2QHnAKmyI485jY7bWifUk6poPC1KQehEbjtr6zCc").server("EUW").rank("Diamond I").validated("validated")
                .validationCode("83ERFK").validationTries(0).buildUser();

        User user2 = new UserBuilder().redditName("Thorasine").summonerName("Oreena")
                .summonerId("YKuxOWZtcpkiE1V07iLisX1s93FdVSEf3TnHEYHBqy0Qe-8").server("EUW").rank("Gold IV").validated("pending")
                .validationCode("83ITES").validationTries(0).buildUser();

        User user3 = new UserBuilder().redditName("Thorasine").summonerName("SecretSmurf")
                .summonerId("dDsBSlaEk34758KbRuwTnydTNaC1nZQZ5kGOwboGfbb-Zz4").server("NA").rank("Challenger").validated("failed")
                .validationCode("TEST754").validationTries(0).buildUser();

        User user4 = new UserBuilder().redditName("Vjostar").summonerName("Vjostar")
                .summonerId("wWb-WB9G7sMRNTKCjnze7MX84wkIEJhmqe-2JsRwuu_WsEk").server("EUW").rank("Silver III").validated("pending")
                .validationCode("TEST434").validationTries(0).buildUser();

        User user5 = new UserBuilder().redditName("Holo").summonerName("Holó")
                .summonerId("_mwCqsqdyUplPbKpVdc9_k65LAmIm6RonekeZ36L0x5F1Ek").server("EUW").rank("Bronze II").validated("validated")
                .validationCode("TEST12").validationTries(0).buildUser();

        User user6 = new UserBuilder().redditName("Oprah").summonerName("Oprah Winfrey")
                .summonerId("O06SyEvi6NVHMbIuisc_iAKh1g82vAEk6jvzzca25pov8oE").server("EUW").rank("Bronze I").validated("validated")
                .validationCode("TEST239").validationTries(0).buildUser();

        db.addUser(user1);
        db.addUser(user2);
        db.addUser(user3);
        db.addUser(user4);
        db.addUser(user5);
        db.addUser(user6);
    }
}
