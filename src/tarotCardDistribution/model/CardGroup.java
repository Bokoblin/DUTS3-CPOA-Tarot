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
import java.util.List;
import java.util.Objects;

/**
 * The {@code CardGroup} class consists in a group of cards.
 * It is abstract so it only defines common attributes and methods
 * for {@code Hand} ans {@code Talon} classes
 * @author Arthur
 * @version v0.6
 * @since v0.1
 *
 * @see CardGroup
 * @see Card
 */
public abstract class CardGroup {
    protected List<Card> cardList;
    protected final int NB_MAX_CARDS;

    /**
     * Constructs a CardGroup
     * @since v0.1
     * @param NB_MAX_CARDS the max number of card a subclass of CardGroup can have
     * @throws CardGroupNumberException
     */
    public CardGroup(int NB_MAX_CARDS) throws CardGroupNumberException {
        this.NB_MAX_CARDS = NB_MAX_CARDS;
    }

    /**
     * Add a card
     * @since v0.1
     * @param card the card which is added to card list
     */
    public void addCard(Card card) throws CardNumberException {
        if ( cardList.size() >= NB_MAX_CARDS)
            throw new CardNumberException(
                    "Card number limit has been reached.", NB_MAX_CARDS);
        else {
            cardList.add(card);
        }
    }

    /**
     * Display all the cards of a card group
     * @since v0.6
     * @return a string containing all CardGroup's cards name
     */
    public String cardListToString() {
        String result = "";
        for (Card c : cardList ) {
            if ( c.isShown())
                result += c.getName() + "; ";
            else
                result += "?? ; ";
        }
        return result;
    }

    /**
     * Find a card by its name
     * @since v0.6
     * @return a boolean indicating if card has been found
     */
    public boolean findInCards(String nameToFind) {
        for (Card c : cardList)
            if (Objects.equals(c.getName(), nameToFind))
                return true;
        return false;
    }

    /**
     * Find a card by its name
     * @since v0.6
     * @return the card if found
     */
    public Card getInCards(String nameToFind) {
        for (Card c : cardList)
            if (Objects.equals(c.getName(), nameToFind))
                return c;
        return null;
    }


    //GETTERS - no documentation needed

    public int getNbCards() {
        return cardList.size();
    }
    public List<Card> getCardList() {
        return cardList;
    }
}
