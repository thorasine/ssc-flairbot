package ssc_flairbot.league;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import no.stelar7.api.l4j8.basic.constants.types.GameQueueType;
import no.stelar7.api.l4j8.pojo.league.LeagueEntry;

import org.springframework.stereotype.Component;

/**
 * Class to format and retrieve the highest rank from a given collection.
 *
 * @author Thorasine
 */
@Component
public class LeagueRankHelper {

    /**
     * Return the highest 5v5 rank as a string from a list of LeagueEntry-s.
     *
     * @param leaguePositions the list containing LeagueEntry-s (ranks)
     * @return the highest rank string
     */
    String get5v5SoloRank(List<LeagueEntry> leaguePositions) {
        Set<String> ranks = new HashSet<>();
        leaguePositions.forEach(position -> {
            if (position.getQueueType().equals(GameQueueType.RANKED_SOLO_5X5)) {
                ranks.add(position.getTier() + " " + position.getRank());
            }
        });
        return getHighestRank(ranks);
    }

    /**
     * Return the highest rank as a string from a set of ranks.
     *
     * @param ranks the set containing ranks
     * @return the highest rank string
     */
    public String getHighestRank(Set<String> ranks) {
        String maxTier = "UNRANKED";
        String maxDivision = "IV";
        for (String rank : ranks) {
            String[] splitted = rank.split(" ");
            String tier = splitted[0].toUpperCase();
            if (tier.equalsIgnoreCase("UNRANKED")) continue;
            String division = splitted[1];
            if (Tier.valueOf(tier).isAbove(Tier.valueOf(maxTier))) {
                maxTier = tier;
                maxDivision = division;
            } else if (Tier.valueOf(tier).isEqual(Tier.valueOf(maxTier))) {
                if (Division.valueOf(division).isAbove(Division.valueOf(maxDivision))) {
                    maxDivision = division;
                }
            }
        }
        return rankFormatter(maxTier, maxDivision);
    }

    /**
     * Format the rank into a proper string (e.g. "Diamond IV") given the tier and division.
     *
     * @param tier     first part of the rank (e.g. "Diamond")
     * @param division second part of the rank (e.g. "IV")
     * @return the properly formatten rank string
     */
    private String rankFormatter(String tier, String division) {
        tier = tier.toLowerCase();
        tier = tier.substring(0, 1).toUpperCase() + tier.substring(1);
        if (tier.equals("Unranked")) {
            return tier;
        } else {
            return tier + " " + division;
        }
    }

    /**
     * Enumerator for every tier weighted based on their place on the ladder.
     */
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

    /**
     * Enumerator for every division weighted based on their place inside Tiers in-game.
     */
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
