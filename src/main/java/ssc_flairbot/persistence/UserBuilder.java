package ssc_flairbot.persistence;

public class UserBuilder {

    private String redditName;
    private String summonerName;
    private String summonerId;
    private String server;
    private String rank;
    private String validated;
    private String validationCode;
    private int validationTries;
    private String updateDate;

    public UserBuilder() {

    }

    public User buildUser() {
        return new User(redditName, summonerName, summonerId, server, rank, validated, validationCode, validationTries);
    }

    public UserBuilder redditName(String name) {
        this.redditName = name;
        return this;
    }

    public UserBuilder summonerName(String name) {
        this.summonerName = name;
        return this;
    }

    public UserBuilder summonerId(String id) {
        this.summonerId = id;
        return this;
    }
    
    public UserBuilder server(String server){
        this.server = server;
        return this;
    }
    
    public UserBuilder rank(String rank){
        this.rank = rank;
        return this;
    }
    
    public UserBuilder validated(String validated){
        this.validated = validated;
        return this;
    }
    
    public UserBuilder validationCode(String validationCode){
        this.validationCode = validationCode;
        return this;
    }
    
    public UserBuilder validationTries(int validationTries){
        this.validationTries = validationTries;
        return this;
    }

}
