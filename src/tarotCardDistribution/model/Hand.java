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

import exceptions.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The {@code Hand} class extends {@code CardGroup},
 * it consists in a group of cards representing a player
 *
 * @author Arthur
 * @version v0.6
 * @since v0.1
 *
 * @see CardGroup
 * @see Card
 */
public class Hand extends CardGroup{
    private static int nbHands;
    private static final int NB_MAX_HANDS = 4;
    private Bids bidChosen;

    /**
     * Constructs a hand
     * @since v0.1
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    public Hand() throws CardGroupNumberException {
        super(18); //Max number of cards for this group
        if ( nbHands >= NB_MAX_HANDS)
            throw new CardGroupNumberException(
                    "Hand instances limit has been reached.", NB_MAX_HANDS);
        else {
            cardList = new ArrayList<>();
            nbHands++;
        }
        bidChosen = null;
    }

    /**
     * Check if a hand has Petit Sec
     * (First of Trump as only Trump and no excuse)
     * @since v0.5
     */
    boolean checkHasPetitSec() {
        int nbTrumps = 0;
        boolean hasLePetit = false;
        boolean hasExcuse = false;
        for (Card c : cardList) {
            if (c.getSuit() == Suit.Trump )
                nbTrumps++;
            if ( c.getSuit() == null)
                hasExcuse = true;
            if (Objects.equals(c.getName(), "Trump1"))
                hasLePetit = true;
        }
        return (nbTrumps == 1 && hasLePetit && !hasExcuse);
    }


    /**
     * Reset static field for unit tests
     * @since v0.5
     */
    public static void resetClassForTesting() {
        nbHands = 0;
    }


    //GETTERS - no documentation needed

    public static int getNbHands() {
        return nbHands;
    }
    public static int getNbMaxHands() {
        return NB_MAX_HANDS;
    }
    public Bids getBidChosen() {
        return bidChosen;
    }

    //SETTERS - no documentation needed

    public void setBidChosen(Bids bidChosen) {
        this.bidChosen = bidChosen;
    }
}
