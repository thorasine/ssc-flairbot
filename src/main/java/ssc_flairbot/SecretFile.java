package ssc_flairbot;

import no.stelar7.api.l4j8.basic.*;

public class SecretFile
{
    public static final String RIOT_API_KEY            = "RGAPI-5b66deda-1e06-4503-bbb7-dd0e69f40051";
    public static final String TOURNAMENT_API_KEY = "FAKE KEY";
    public static final APICredentials CREDS      = new APICredentials(SecretFile.RIOT_API_KEY, SecretFile.TOURNAMENT_API_KEY);
}