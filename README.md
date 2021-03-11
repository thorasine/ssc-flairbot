# SummonerSchool-Flairbot

This repository is the source of a site <em>meant</em> to be used by [/r/summonerschool](https://reddit.com/r/summonerschool) to add verified ranked flairs to contributors. The purpose of this project was to practice full stack development.

**Install and Configuration**

The project uses [Maven](https://maven.apache.org/download.cgi) and [Docker](https://www.docker.com/products/docker-desktop).

Create and fill in the following files with the necessary credentials:  
* [application.yaml](https://github.com/thorasine/ssc-flairbot/blob/master/src/main/resources/application.yaml.example) - Configuration for Spring.
* [docker-compose.yaml](https://github.com/thorasine/ssc-flairbot/blob/master/docker-compose.yaml.example) - Docker composition for the app's environment.

Additional configuration is available in the [Configuration.java](https://github.com/thorasine/ssc-flairbot/blob/master/src/main/java/ssc_flairbot/Configuration.java) file.  

Build the application with Maven:
```
mvn -Dspring-boot.run.profiles=prod package
```
Create the app's Docker image:
```
docker build -t ssc-flairbot:1.0 .
```
Run the Docker composition:
```
docker-compose up -d
```

After a minute or so the app will be online and accessible on http://localhost:8080/.

# Technical Info

The solution was built on **Java 10** with **Maven** and **Docker** in **IntelliJ IDEA** using **Spring Boot 2.3.5** as the main framework along with various Spring components.  

The majority of the website is a single **HTML** file which behaves like an app using **jQuery**, **Bootstrap** and **CSS**.  
User authentication is done with **OAuth2**. The communication between the site and the server is handled by **REST**, **Thymeleaf** and **Ajax**. Data exchange with Reddit and Riots API is through **REST** as well.  
Data persistence is handled by **JDBC** in a MySQL database. Tests were created with the help of **JUnit**, and in some places **Mockito**.

## What's this all about?

[League of Legends](https://www.leagueoflegends.com) is the [astonishingly popular](https://www.forbes.com/sites/insertcoin/2016/09/13/riot-games-reveals-league-of-legends-has-100-million-monthly-players/?sh=744dc14d5aa8) action strategy game by Riot Games.
Over the course of year-long sprints or "seasons", more than 100 million players worldwide compete to achieve standings on a competitive ladder. Those who successfully climb the ladder are awarded ranks: Iron, Bronze, Silver, Gold, Platinum, Diamond, Master, Grandmaster and Challenger.

[Summoner School](https://reddit.com/r/summonerschool) is an online community counting close to half a million users, who have come together to help one another be better players.

Every day, tens thousands of players come together to share information on Summoner School. In order for the information given there to be vetted correctly, it's sometimes important to know the rank of the person giving the information. A "Silver" player may not be wrong, but players might want to prefer the opinion of a "Master" or "Challenger" instead.

**Troubled Beginnings**

![Image of Flair](http://i.imgur.com/k5PDjdg.png)

> *A reddit user with a ranked flair.*

The [moderator team](https://www.reddit.com/r/summonerschool/about/moderators) at Summoner School made several attempts to highlight the contributions of high-calibre players. Some of these included the use of Reedits "flair" system. Flairs are badges that appear next to a person's name in all of their contributions.

Unfortunately, previous attempts at solving the flair system suffered from dishonesty. Contributors were cheating and finding ways to obtain flairs which they hadn't earned. Meanwhile, personally validating each player was too time consuming.

The flair system was discontinued.

**Systematic verification**

In the following years, Summoner School's founder put out a call for somebody to try to solve the flair system's problem: verifying contributors in a systematic way. This application is a solution for the issue.

## How it works

Reddit users visit the site and are prompted to sign in using their Reddit account. Reedits OAuth 2.0 protocol allows the site to verify that 
their Reddit account is authentic.

After signing in, the visitor can specify one or more League of Legends accounts that they own. To verify ownership of those accounts, a challenge is made:

* The user must sign in to League of Legends and enter the given verification code.

* Using Riot Games' [Developer API](https://developer.riotgames.com/) the code is retrieved and checked for authenticity.

Once these steps have been completed, the user is considered registered. From time to time, the user's rank is retrieved using the Riot API and a flair is composed and sent using Reddit API.  

## Screenshots
<img src="https://i.imgur.com/IQCSWdN.jpg" width="45%"></img>&nbsp;&nbsp;&nbsp;<img src="https://i.imgur.com/woiRAWk.png" width="45%"></img>
<img src="https://i.imgur.com/4IPlNAL.png" width="45%"></img>&nbsp;&nbsp;&nbsp;<img src="https://i.imgur.com/IKgCX7l.png" width="45%"></img>
<img src="https://i.imgur.com/ZIYx5bt.png" width="45%"></img>&nbsp;&nbsp;&nbsp;<img src="https://i.imgur.com/mGbTdTt.png" width="45%"></img> 

## Model
Below there is a **simplified** layout representing the app's model.

<img src="https://i.imgur.com/ZcKf2c9.png.jpg" width="65%">  

## Contributions

* [Spring Framework](https://spring.io/) - The framework that the app was built on.
* [L4J8](https://github.com/domisum/L4J8) - Provides caching and micro managing towards Riot API.
* [Jesse Hallam](https://github.com/jessehallam) - For his version of the site, inspiring the site layout and the readme.
