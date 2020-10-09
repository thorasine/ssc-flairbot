package ssc_flairbot.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * Class that helps with database queries.
 *
 * @author Thorasine
 */
public class UserMapper implements RowMapper<User> {

    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setRedditName(rs.getString("redditName"));
        user.setSummonerName(rs.getString("summonerName"));
        user.setSummonerId(rs.getString("summonerId"));
        user.setServer(rs.getString("server"));
        user.setRank(rs.getString("rank"));
        user.setValidated(rs.getString("validated"));
        user.setValidationCode(rs.getString("validationCode"));
        user.setValidationTries(Integer.valueOf(rs.getString("validationTries")));
        user.setUpdateDate(rs.getString("updateDate"));
        return user;
    }
}
