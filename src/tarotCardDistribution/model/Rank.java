/*
Copyright 2016 Jacquot Alexandre, Jolivet Arthur
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package tarotCardDistribution.model;

import java.util.Random;

/**
 * The {@code Rank} enumeration defines all ranks
 * that can be assigned to a card
 * @author Arthur
 * @version v0.5
 * @since v0.1
 */
public enum Rank {
    Ace,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
    Jack,
    Knight,
    Queen,
    King;

    /**
     * Method returning a random rank among enum ones
     * @since v0.5
     *
     * @return a random rank
     * @see Random
     */
    public static Rank randomRank() {
        int pick = new Random().nextInt(Rank.values().length);
        return Rank.values()[pick];
    }
}
