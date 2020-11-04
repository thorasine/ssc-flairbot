# SummonerSchool-Flairbot

This repository is the source of a site meant be used by [/r/summonerschool](https://reddit.com/r/summonerschool) to add verified ranked flairs to contributors. The purpose of this project was to practice Full Stack Development along with [Spring](https://spring.io/) (as it is my first Spring project).

**Configuration**

Import the dependencies through Maven and have an SQL database running and the dev must also create and fill in the following files with the necessary credentials:  
* [application.yml](https://github.com/thorasine/ssc-flairbot/blob/master/src/main/resources/application.yml.example)  
* [SecretFile.java](https://github.com/thorasine/ssc-flairbot/blob/master/src/main/java/ssc_flairbot/SecretFile.java.example)   

The following REST APIs are used:  
* [Riot Games API](https://developer.riotgames.com/): Used to access the user's in-game ranks.  
* [Reddit API](https://www.reddit.com/dev/api/):      Used for OAuth2 authorization and to manage user's reddit flairs. These are two separate entities, both require their own separate keys.  

## What's this all about?

[League of Legends](https://www.leagueoflegends.com) is the [astonishingly popular](https://www.google.ca/url?sa=t&rct=j&q=&esrc=s&source=web&cd=3&cad=rja&uact=8&ved=0ahUKEwjthcqmzsrLAhVDmYMKHa86CVEQFggmMAI&url=http%3A%2F%2Fwww.forbes.com%2Fsites%2Finsertcoin%2F2014%2F01%2F27%2Friots-league-of-legends-reveals-astonishing-27-million-daily-players-67-million-monthly%2F&usg=AFQjCNHMpPx45j6T40Fs9F6DvhkAP1JUng&sig2=abwf3efOnI3xx15Wvinxzg) action strategy game by Riot Games.
Over the course of year-long sprints, or "seasons", more than 100 million players worldwide compete to achieve standings on a competitive ladder. Those who successfully climb the ladder are awarded ranks: Iron, Bronze, Silver, Gold, Platinum, Diamond, Master, Grandmaster and Challenger.

[Summoner School](https://reddit.com/r/summonerschool) is an online community counting close to half a million users, who have come together to help one another be better players.

Every day, hundreds of thousands of players come together to share information on Summoner School. In order for the information given there to be vetted correctly, it's sometimes important to know the rank of the person giving the information. A "Silver" player may not be wrong, but players might want to prefer the opinion of a "Master" or "Challenger" instead.

**Troubled Beginnings**

![Image of Flair](http://i.imgur.com/k5PDjdg.png)

> *A reddit user with a ranked flair.*

The [moderator team](https://www.reddit.com/r/summonerschool/about/moderators) at Summoner School made several attempts to highlight the contributions of high-calibre players. Some of these included the use of Reddit's "flair" system. Flairs are badges that appear next to a person's name in all of their contributions.

Unfortunately, previous attempts at solving the flair system suffered from dishonesty. Contributors were cheating and finding ways to obtain flairs which they hadn't earned. Meanwhile, personally validating each player was too time consuming.

The flair system was discontinued.

**Systematic verification**

In the following years, Summoner School's founder put out a call for somebody to try to solve the flair system's problem: verifying contributors in a systematic way. This application is a solution for the issue.

## How it works

Reddit users visit the site and are prompted to sign in using their Reddit account. Reddit's OAuth 2.0 protocol allows the site to verify that 
their Reddit account is authentic.

After signing in, the visitor can specify one or more League of Legends accounts that they own. To verify ownership of those accounts, a challenge is made:

* The user must sign in to League of Legends and enter the given verification code.

* Using Riot Games' [Developer API](https://developer.riotgames.com/) the code is retrieved and checked for authenticity.

Once these steps have been completed, the user is considered registered. From time to time, the user's rank is retrieved using the Riot API and a flair is composed and sent using Reddit's API.  

## Screenshots
<img src="https://i.imgur.com/IQCSWdN.jpg" width="45%" style="margin-right: 20px;"></img>&nbsp;&nbsp;&nbsp;<img src="https://i.imgur.com/woiRAWk.png" width="45%"></img> <br/> <img src="https://i.imgur.com/4IPlNAL.png" width="45%" style="margin-right: 20px;"></img>&nbsp;&nbsp;&nbsp;<img src="https://i.imgur.com/IKgCX7l.png" width="45%"></img><br /><img src="https://i.imgur.com/ZIYx5bt.png" width="45%" style="margin-right: 20px;"></img>&nbsp;&nbsp;&nbsp;<img src="https://i.imgur.com/mGbTdTt.png" width="45%"></img> 

# Technical Info

The solution was built on **Java 10** in **IntelliJ IDEA** using **Spring Boot 2.1.1** as the main framework along with various Spring components.  

The majority of the website is a single **HTML** file which behaves like an app using **Javascript**, **Bootstrap** and **CSS**. The data exchange between the site and the server is handled by **Thymeleaf** and **jQuery**.  

Data persistence is handled by **H2 Database** with **JDBC**. The users data is stored in an **MySQL** database.  

Tests were created with the help of **JUnit 4**, **AssertJ** and in some places **Mockito**).  

## Contributions

* [Spring Framework](https://spring.io/) - The framework that the app built on.
* [L4J8](https://github.com/domisum/L4J8) - Micromanages requests towards Riot API.
* [Jesse Hallam](https://github.com/jessehallam) - For his version of the site, inspiring the site layout and the readme.
