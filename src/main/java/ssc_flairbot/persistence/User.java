package ssc_flairbot.persistence;

/**
 * Class to represent a user entity.
 *
 * @author Thorasine
 */
public class User {

    private long id;
    private String redditName;
    private String summonerName;
    private String summonerId;
    private String server;
    private String gamerank;
    private String validated;
    private String validationCode;
    private int validationTries;
    private String updateDate;

    public User() {
    }

    public User(String redditName, String summonerName, String summonerId, String server, String gamerank,
                String validated, String validationCode, int validationTries) {
        this.redditName = redditName;
        this.summonerName = summonerName;
        this.summonerId = summonerId;
        this.server = server;
        this.gamerank = gamerank;
        this.validated = validated;
        this.validationCode = validationCode;
        this.validationTries = validationTries;
    }

    public long getId() {
        return id;
    }

    public String getRedditName() {
        return redditName;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public String getSummonerId() {
        return summonerId;
    }

    public String getServer() {
        return server;
    }

    public String getGamerank() {
        return gamerank;
    }

    public String getValidated() {
        return validated;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public int getValidationTries() {
        return validationTries;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setRedditName(String redditName) {
        this.redditName = redditName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public void setSummonerId(String summonerId) {
        this.summonerId = summonerId;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setGamerank(String gamerank) {
        this.gamerank = gamerank;
    }

    public void setValidated(String validated) {
        this.validated = validated;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public void setValidationTries(int validationTries) {
        this.validationTries = validationTries;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", redditName=" + redditName + ", summonerName=" + summonerName + ", summonerId" +
                "=" + summonerId + ", server=" + server + ", gamerank=" + gamerank + ", validated=" + validated + ", " +
                "validationCode=" + validationCode + ", validationTries=" + validationTries + ", updateDate=" + updateDate + '}';
    }

}
