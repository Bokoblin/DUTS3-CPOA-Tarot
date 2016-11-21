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

package app.model;

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
 * @version v0.6.2
 * @since v0.1
 *
 * @see Suit
 * @see Rank
 */
public class Card{
    private static final int NB_MAX_CARDS = 78;
    private static final int NB_MAX_CLASSICS = 56;
    private static final int NB_MAX_TRUMPS = 21;
    private static final int NB_MAX_FOOLS = 1;

    private static int nb = 0;
    private static List<String> cardList = new ArrayList<>();
    private boolean shown;

    private final String name;
    private final Suit suit;
    private final Rank rank;
    private final int trumpRank;

    /**
     * Constructs a void card
     * @since v0.1
     *
     * @throws CardNumberException if user tries to create too much cards
     */
    public Card() throws CardNumberException {
        if ( nb >= NB_MAX_CARDS)
            throw new CardNumberException("Card number limit has been reached.", NB_MAX_CARDS);
        else
            nb++;

        suit = null;
        rank = null;
        name = "";
        trumpRank = -1;
        shown = false;
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
        if ( nb >= NB_MAX_CARDS)
            throw new CardNumberException("Card number limits has been reached.", NB_MAX_CARDS);
        else
            nb++;

        for ( String s : cardList)
            if (Objects.equals(s, String.valueOf(suit) + String.valueOf(rank))) {
                throw new CardUniquenessException();
            }

        this.suit = suit;
        this.rank = rank;
        this.name = String.valueOf(suit)+String.valueOf(rank);
        trumpRank = -1;
        cardList.add(name);
    }

    /**
     * Constructs a trump card with a suit and a trump rank
     * @since v0.1
     *
     * @param suit defines card suit
     * @param rank defines card rank
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     */
    public Card(Suit suit, int rank)
            throws CardNumberException, CardUniquenessException {
        if ( nb >= NB_MAX_CARDS)
            throw new CardNumberException("Card number limits has been reached.", NB_MAX_CARDS);
        else
            nb++;

        for ( String s : cardList)
            if (Objects.equals(s, String.valueOf(suit) + String.valueOf(rank))) {
                throw new CardUniquenessException();
            }

        this.suit = suit;
        this.rank = null;
        this.name = String.valueOf(suit)+String.valueOf(rank);
        trumpRank = rank;
        cardList.add(name);
    }

    /**
     * Constructs a card with a suit and a rank
     * @since v0.5
     *
     * @param name card name
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     */
    public Card(String name)
            throws CardNumberException, CardUniquenessException, CardNameException {
        if (Objects.equals(name,"Excuse")) {
            if (nb >= NB_MAX_CARDS)
                throw new CardNumberException("Card number limits has been reached.", NB_MAX_CARDS);
            else
                nb++;

            for (String s : cardList)
                if (Objects.equals(s, name)) {
                    throw new CardUniquenessException();
                }

            this.suit = null;
            this.rank = null;
            this.name = name;
            trumpRank = -1;
            cardList.add(name);
        }
        else {
            throw new CardNameException();
        }
    }

    /**
     * Reset static field for unit tests
     * @since v0.5
     */
    public static void resetClassForTesting() {
        nb = 0;
        cardList.clear();
    }

    /**
     * Compare two cards following their Suit and Rank
     * @since v0.5
     * @return a boolean indicating if a card is smaller or not
     */
    public static boolean compareSmallerTo(Card c1, Card c2) {
        //Both are not trumps
        if ( c1.suit != Suit.Trump && c2.suit != Suit.Trump) {
            if (c1.rank.ordinal() < c2.rank.ordinal())
                return true;
            else if (c1.rank.ordinal() > c2.rank.ordinal())
                return false;
            else { //Same rank
                return c1.suit.ordinal() < c2.suit.ordinal();
            }
        }
        //Card1 is a trump, Card2 is not a trump
        else if ( c1.suit == Suit.Trump && c2.suit != Suit.Trump ) {
            if (c1.trumpRank < c2.rank.ordinal())
                return true;
            else if (c1.trumpRank > c2.rank.ordinal())
                return false;
            else { //Same rank
                return c1.trumpRank < c2.suit.ordinal();
            }
        }
        //Both are trumps
        else
            return c1.trumpRank < c2.trumpRank;
    }


    //GETTERS - no documentation needed

    public static int getNbCards() {
        return nb;
    }
    public static int getNbMaxCards() {
        return NB_MAX_CARDS;
    }
    public static int getNbMaxTrumps() {
        return NB_MAX_TRUMPS;
    }
    public String getName() {
        return name;
    }
    public Suit getSuit() {
        return suit;
    }
    public Rank getRank() {
        return rank;
    }
    public boolean isShown() {
        return shown;
    }


    //SETTERS - no documentation needed

    public void setShown(boolean shown) {
        this.shown = shown;
    }
}
