package ssc_flairbot.persistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DBHandler {

    @Autowired
    JdbcTemplate database;

    public void updateVerification(User user) {
        String SQL = "UPDATE users SET validated = ?, validationTries = ? WHERE id = ?";
        database.update(SQL, user.getValidated(), user.getValidationTries(), user.getId());
    }

    public List<User> getPendingUsers() {
        String SQL = "SELECT * FROM users WHERE validated = 'pending'";
        List<User> userList = database.query(SQL, new Object[]{}, new UserMapper());
        return userList;
    }

    public void addUser(User user) {
        String SQL = "INSERT INTO users(redditName, summonerName, summonerId, server, rank, validated, validationCode, "
                + "validationTries, updateDate) VALUES (?,?,?,?,?,?,?,?,?)";
        database.update(SQL, user.getRedditName(), user.getSummonerName(), user.getSummonerId(), user.getServer(),
                user.getRank(), user.getValidated(), user.getValidationCode(), user.getValidationTries(), getDate());
    }

    public void deleteUser(long id) {
        String SQL = "DELETE FROM users WHERE id = ?";
        database.update(SQL, id);
    }

    public void updateUser(User user) {
        String SQL = "UPDATE users SET redditName = ?, summonerName = ?, "
                + "summonerId = ?, server = ?, rank = ?, validated = ?, validationCode = ?, "
                + "validationTries = ?, updateDate = ? WHERE id = ?";
        database.update(SQL, user.getRedditName(), user.getSummonerName(), user.getSummonerId(), user.getServer(),
                user.getRank(), user.getValidated(), user.getValidationCode(), user.getValidationTries(),
                getDate(), user.getId());
    }

    public User getUserById(Long id) {
        String SQL = "SELECT * FROM users WHERE id = ?";
        User user = database.queryForObject(SQL, new Object[]{id}, new UserMapper());
        return user;
    }

    public boolean isSummonerAlreadyValidatedBySomeone(User user) {
        int count;
        String SQL = "SELECT count(*) FROM users WHERE summonerName = ? AND server = ? AND validated = 'validated'";
        count = database.queryForObject(SQL, new Object[]{user.getSummonerName(), user.getServer()}, Integer.class);
        return count > 0;
    }

    public boolean isSummonerAlreadyRegisteredByUser(User user) {
        String SQL = "SELECT count(*) FROM users WHERE summonerName = ? AND server = ? AND redditName = ?";
        int count = database.queryForObject(SQL, new Object[]{user.getSummonerName(), user.getServer(), user.getRedditName()}, Integer.class);
        return count > 0;
    }

    public List<User> getUsersByRedditName(String redditName) {
        String SQL = "SELECT * FROM users WHERE redditName = ?";
        List<User> userList = database.query(SQL, new Object[]{redditName}, new UserMapper());
        return userList;
    }

    public List<User> getUsersBySummonerName(String summonerName) {
        String SQL = "SELECT * FROM users WHERE summonerName = ?";
        List<User> userList = database.query(SQL, new Object[]{summonerName}, new UserMapper());
        return userList;
    }

    public List<User> getAllUsers() {
        String SQL = "select * from users";
        List<User> users = database.query(SQL, new UserMapper());
        return users;
    }

    public void createTable() {
        database.execute("CREATE TABLE users(id SERIAL, redditName VARCHAR(255), summonerName VARCHAR(255), "
                + "summonerId VARCHAR(255), server VARCHAR(255), rank VARCHAR(255), validated VARCHAR(255), "
                + "validationCode VARCHAR(255), validationTries INT(255), updateDate DATETIME)");
    }

    public void dropTable() {
        database.execute("DROP TABLE IF EXISTS users");
    }

    private String getDate() {
        Date dateRaw = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return ft.format(dateRaw);
    }
}
