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

package tarotCardDistribution.modelClasses;

import exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The {@code Card} class contains all the information a card can contain,
 * statitistics on cards type, number and max number and a list of already
 * instantiated cards to check card uniqueness
 * because there can't be multiple card with same suit and rank
 * @author Arthur
 * @version v0.1
 * @since v0.1
 *
 * @see Suit
 * @see Rank
 */
public class Card{
    private static int nb = 0;
    private static final int nbMax = 78;
    private static final int nbClassicCards = 56;
    private static final int nbAtoutsCards = 22;
    private static List<String> cardList = new ArrayList<String>();
    private final Suit suit;
    private final Rank rank;

    /**
     * Constructs a void card
     * @since v0.1
     *
     * @throws CardNumberException if user tries to create too much cards
     */
    public Card() throws CardNumberException {
        suit = null;
        rank = null;
        if ( nb >= nbMax)
            throw new CardNumberException("Card number limit has been reached.", nbMax);
        else
            nb++;
    }

    /**
     * Constructs a card with a suit and a rank
     * @since v0.1
     *
     * @param suit defines card suit
     * @param rank defines card rank
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     */
    public Card(Suit suit, Rank rank)
            throws CardNumberException, CardUniquenessException {
        this.suit = suit;
        this.rank = rank;
        for ( String s : cardList)
            if (Objects.equals(s, String.valueOf(suit) + String.valueOf(rank)))
                throw new CardUniquenessException();
        cardList.add(String.valueOf(suit)+String.valueOf(rank));

        if ( nb >= nbMax)
            throw new CardNumberException("Card number limits has been reached.", nbMax);
        else
            nb++;
    }

    public static int getCardNumber() {
        return nb;
    }
    public static int getTotalCardNumber() {
        return nbMax;
    }
}
