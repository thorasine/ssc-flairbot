package spring_oauth2_reddit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import spring_oauth2_reddit.persistence.*;

@SpringBootApplication
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
                .summonerId("fdsfethfdvd").server("EUW").rank("Diamond I").validated("validated")
                .validationCode("83ERF").validationTries(0).buildUser();

        User user2 = new UserBuilder().redditName("Thorasine").summonerName("Sniblets")
                .summonerId("uoigknrjbudfh").server("EUW").rank("Bronze V").validated("pending")
                .validationCode("83ERF").validationTries(0).buildUser();

        User user3 = new UserBuilder().redditName("Thorasine").summonerName("SecretSmurf")
                .summonerId("uoigknrjbudfh").server("NA").rank("Challenger").validated("failed")
                .validationCode("83ERF").validationTries(0).buildUser();

        User user4 = new UserBuilder().redditName("Vjostar").summonerName("Vjostar")
                .summonerId("trhgfefsfd").server("NA").rank("Bronze III").validated("validated")
                .validationCode("83ERF").validationTries(0).buildUser();

        db.addUser(user1);
        db.addUser(user2);
        db.addUser(user3);
        db.addUser(user4);
    }
}