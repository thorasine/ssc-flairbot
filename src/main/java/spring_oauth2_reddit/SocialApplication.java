package spring_oauth2_reddit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import spring_oauth2_reddit.persistence.*;

@SpringBootApplication
public class SocialApplication implements CommandLineRunner {

    @Autowired
    private DBHandler db;

    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }

    @Override
    public void run(String... strings) {
        db.dropTable();
        db.createTable();

        User user1 = new UserBuilder().redditName("Thorasine").summonerName("Trefort")
                .summonerId("fdsfethfdvd").server("EUW").rank("Diamond I").validated("validated")
                .validationCode("83ERF").validationTries(0).buildUser();

        User user2 = new UserBuilder().redditName("Thorasine").summonerName("Sniblets")
                .summonerId("uoigknrjbudfh").server("EUW").rank("Bronze V").buildUser();

        User user3 = new UserBuilder().redditName("Vjostar").summonerName("Vjostar")
                .summonerId("trhgfefsfd").server("NA").rank("Bronze III").buildUser();

        db.addUser(user1);
        db.addUser(user2);
        db.addUser(user3);

        System.out.println("getAllUsers test: ");
        for (User user : db.getAllUsers()) {
            System.out.println(user.toString());
        }
    }
}
