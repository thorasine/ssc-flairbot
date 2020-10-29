package ssc_flairbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class  of the application
 *
 * @author Thorasine
 */
@SpringBootApplication
@EnableScheduling
public class Application{

    //wipes the database, puts in sample data, and changes League API rate limits
    public static final boolean IN_TEST_PHASE = true;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
