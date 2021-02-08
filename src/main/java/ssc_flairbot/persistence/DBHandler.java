package ssc_flairbot.persistence;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Class that handles the database requests.
 *
 * @author Thorasine
 */
@Component
public class DBHandler {

    private final JdbcTemplate database;

    @Autowired
    public DBHandler(JdbcTemplate database) {
        this.database = database;
    }

    /**
     * Adds multiple users to the database in one session.
     *
     * @param users we want to add to the database
     * @return the amount of affected rows
     */
    int[] batchAddUsers(List<User> users) {
        String SQL = "INSERT INTO users(redditName, summonerName, summonerId, server, gamerank, validated, validationCode, "
                + "validationTries, updateDate) VALUES (?,?,?,?,?,?,?,?,?)";
        int[] updateCounts = database.batchUpdate(SQL,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, users.get(i).getRedditName());
                        ps.setString(2, users.get(i).getSummonerName());
                        ps.setString(3, users.get(i).getSummonerId());
                        ps.setString(4, users.get(i).getServer());
                        ps.setString(5, users.get(i).getGamerank());
                        ps.setString(6, users.get(i).getValidated());
                        ps.setString(7, users.get(i).getValidationCode());
                        ps.setString(8, String.valueOf(users.get(i).getValidationTries()));
                        ps.setString(9, getDate());
                    }

                    public int getBatchSize() {
                        return users.size();
                    }
                });
        return updateCounts;
    }

    /**
     * Updates the rank and updateDate for multiple users in one session.
     *
     * @param users we want to update
     * @return the amount of affected rows
     */
    public int[] batchUpdateUsersRank(List<User> users) {
        int[] updateCounts = database.batchUpdate(
                "UPDATE users SET gamerank = ?, updateDate = ? WHERE id = ?",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, users.get(i).getGamerank());
                        ps.setString(2, getDate());
                        ps.setString(3, String.valueOf(users.get(i).getId()));
                    }

                    public int getBatchSize() {
                        return users.size();
                    }
                });
        return updateCounts;
    }

    public void addUser(User user) {
        String SQL = "INSERT INTO users(redditName, summonerName, summonerId, server, gamerank, validated, validationCode, "
                + "validationTries, updateDate) VALUES (?,?,?,?,?,?,?,?,?)";
        database.update(SQL, user.getRedditName(), user.getSummonerName(), user.getSummonerId(), user.getServer(),
                user.getGamerank(), user.getValidated(), user.getValidationCode(), user.getValidationTries(), getDate());
    }

    public void deleteUser(long id) {
        String SQL = "DELETE FROM users WHERE id = ?";
        database.update(SQL, id);
    }

    public void updateUser(User user) {
        String SQL = "UPDATE users SET redditName = ?, summonerName = ?, summonerId = ?, server = ?, gamerank = ?, " +
                "validated = ?, validationCode = ?, validationTries = ?, updateDate = ? WHERE id = ?";
        database.update(SQL, user.getRedditName(), user.getSummonerName(), user.getSummonerId(), user.getServer(),
                user.getGamerank(), user.getValidated(), user.getValidationCode(), user.getValidationTries(),
                getDate(), user.getId());
    }

    public User getUserById(Long id) {
        String SQL = "SELECT * FROM users WHERE id = ?";
        return database.queryForObject(SQL, new Object[]{id}, new UserMapper());
    }

    public boolean isSummonerAlreadyValidatedBySomeone(User user) {
        int count;
        String SQL = "SELECT count(*) FROM users WHERE summonerId = ? AND server = ? AND validated = 'validated'";
        count = database.queryForObject(SQL, new Object[]{user.getSummonerId(), user.getServer()}, Integer.class);
        return count > 0;
    }

    public boolean isSummonerAlreadyRegisteredByUser(User user) {
        String SQL = "SELECT count(*) FROM users WHERE summonerId = ? AND server = ? AND redditName = ?";
        int count = database.queryForObject(SQL, new Object[]{user.getSummonerId(), user.getServer(),
                user.getRedditName()}, Integer.class);
        return count > 0;
    }

    public List<User> getValidatedAccountsByServer(String server) {
        String SQL = "SELECT * FROM users WHERE server = ? AND validated = 'validated'";
        return database.query(SQL, new Object[]{server}, new UserMapper());
    }

    public List<User> getValidatedAccountsByRedditName(String redditName) {
        String SQL = "SELECT * FROM users WHERE redditName = ? AND validated = 'validated'";
        return database.query(SQL, new Object[]{redditName}, new UserMapper());
    }

    public List<User> getAccountsByRedditName(String redditName) {
        String SQL = "SELECT * FROM users WHERE redditName = ?";
        return database.query(SQL, new Object[]{redditName}, new UserMapper());
    }

    public List<User> getAllUsers() {
        String SQL = "SELECT * FROM users";
        return database.query(SQL, new UserMapper());
    }

    public List<User> getPendingUsers() {
        String SQL = "SELECT * FROM users WHERE validated = 'pending'";
        return database.query(SQL, new Object[]{}, new UserMapper());
    }

    public void createTable() {
        database.execute("CREATE TABLE users(id SERIAL, redditName VARCHAR(255), summonerName VARCHAR(255), "
                + "summonerId VARCHAR(255), server VARCHAR(255), gamerank VARCHAR(255), validated VARCHAR(255), "
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
