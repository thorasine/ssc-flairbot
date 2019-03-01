package ssc_flairbot.league;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.rithms.riot.api.endpoints.league.dto.LeaguePosition;
import org.springframework.stereotype.Component;

@Component
public class RankHandler {

    public String getSummonerHighestRank(Set<LeaguePosition> positions) {
        Set<String> ranks = new HashSet<>();
        for (LeaguePosition p : positions) {
            if (p.getQueueType().equalsIgnoreCase("RANKED_SOLO_5x5")) {
                System.out.println("Tier + Rank: " + p.getTier() + " " + p.getRank());
                ranks.add(p.getTier() + " " + p.getRank());
            }
        }
        return getHighestRank(ranks);
    }

    //Will use this for the updater too
    public String getHighestRank(Set<String> ranks) {
        String highestTier = "UNRANKED";
        String highestRNumber = "IV";
        for (String rank : ranks) {
            String[] splitted = rank.split(" ");
            String tier = splitted[0];
            String rnumber = splitted[1];

            if (Tier.valueOf(tier).isAbove(Tier.valueOf(highestTier))) {
                highestTier = tier;
                highestRNumber = rnumber;
            } else if (Tier.valueOf(tier).isEqual(Tier.valueOf(highestTier))) {
                if (RNumber.valueOf(rnumber).isAbove(RNumber.valueOf(highestRNumber))) {
                    highestRNumber = rnumber;
                }
            }
        }

        highestTier = highestTier.toLowerCase();
        highestTier = highestTier.substring(0, 1).toUpperCase() + highestTier.substring(1);
        if (highestTier.equalsIgnoreCase("Unranked") || highestTier.equalsIgnoreCase("Master") || highestTier.equalsIgnoreCase("Challenger")) {
            System.out.println("Highest rank: " + highestTier);
            return highestTier;
        } else {
            System.out.println("Highest rank: " + highestTier + " " + highestRNumber);
            return highestTier + " " + highestRNumber;
        }
    }

    private enum Tier {
        UNRANKED(0), IRON(1), BRONZE(2), SILVER(3), GOLD(4), PLATINUM(5), DIAMOND(6), MASTER(7), GRANDMASTER(8), CHALLENGER(9);

        private final Integer level;

        Tier(int level) {
            this.level = level;
        }

        public boolean isAbove(Tier other) {
            return this.level > other.level;
        }

        public boolean isEqual(Tier other) {
            return Objects.equals(this.level, other.level);
        }
    }

    private enum RNumber {
        IV(0), III(1), II(2), I(3);

        private final Integer level;

        RNumber(int level) {
            this.level = level;
        }

        public boolean isAbove(RNumber other) {
            return this.level > other.level;
        }
    }
}
