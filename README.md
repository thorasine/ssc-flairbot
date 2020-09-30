# SummonerSchool-Flairbot

This repository is the source of a site meant be used by [/r/summonerschool](https://reddit.com/r/summonerschool) to add verified ranked flairs to contributors.  
Another purpose of this project was to practice Full Stack Development along with [Spring](https://spring.io/) (as it is my first Spring project).

**Configuration**

Other than importing dependencies through Maven and have an SQL database running the dev must also create and fill in an application.yml [(example)](https://github.com/thorasine/ssc-flairbot/blob/master/src/main/resources/application.yml.example) and a SecretFile.java [(example)](https://github.com/thorasine/ssc-flairbot/blob/master/src/main/java/ssc_flairbot/SecretFile.java.example) with the necessary creditenals.  
The following rest APIs are used:  
[Riot Games API](https://developer.riotgames.com/): Used to access the user's in-game ranks.  
[Reddit API](https://www.reddit.com/dev/api/):      Used for OAuth2 authorization and to manage user's reddit flairs. These are two separate entities, both require their own separate keys.  


