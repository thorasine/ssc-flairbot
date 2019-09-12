package ssc_flairbot.league;

import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.league.LeaguePositions;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RankHandler {

    public String getSummonerHighestRank(LeaguePositions leaguePositions) {
        Set<String> ranks = new HashSet<>();
        for (LeagueEntry position : leaguePositions) {
            if (position.getQueue().equals(Queue.RANKED_SOLO_5x5)) {
                ranks.add(position.getTier() + " " + position.getDivision());
            }
        }

        return getHighestRank(ranks);
    }

    public String getHighestRank(Set<String> ranks) {
        String highestTier = "UNRANKED";
        String highestDivision = "IV";
        for (String rank : ranks) {
            String[] splitted = rank.split(" ");
            String tier = splitted[0];
            String division = splitted[1];

            if (Tier.valueOf(tier).isAbove(Tier.valueOf(highestTier))) {
                highestTier = tier;
                highestDivision = division;
            } else if (Tier.valueOf(tier).isEqual(Tier.valueOf(highestTier))) {
                if (Division.valueOf(division).isAbove(Division.valueOf(highestDivision))) {
                    highestDivision = division;
                }
            }
        }

        return rankFormatter(highestTier, highestDivision);
    }

    private String rankFormatter(String tier, String division) {
        tier = tier.toLowerCase();
        tier = tier.substring(0, 1).toUpperCase() + tier.substring(1);
        if (tier.equalsIgnoreCase("Unranked") || tier.equalsIgnoreCase("Master") || tier.equalsIgnoreCase("Challenger")) {
            return tier;
        } else {
            return tier + " " + division;
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

    private enum Division {
        IV(0), III(1), II(2), I(3);

        private final Integer level;

        Division(int level) {
            this.level = level;
        }

        public boolean isAbove(Division other) {
            return this.level > other.level;
        }
    }
}
