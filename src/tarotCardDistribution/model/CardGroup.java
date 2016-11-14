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
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The {@code CardGroup} class consists in a group of cards.
 * It is abstract so it only defines common attributes and methods
 * for {@code Hand} ans {@code Talon} classes
 * @author Arthur
 * @author Alexandre
 * @version v0.6.3
 * @since v0.1
 *
 * @see CardGroup
 * @see Card
 */
public class CardGroup {
    public class CardsArray extends ArrayList<Card>
    {
        /**
         To ensure that we can't call the defaults add methods with a risk of overflow the cards limit,
         we override it. These overrated methods will be call if we cast CardsArray into an ArrayList;
         **/
        @Override
        public boolean add(Card card)
        {
            boolean success = true;
            try {
                CardGroup.this.add(card);
            } catch (CardNumberException e) {
                e.printStackTrace();
                success = false;
            }
            return success;
        }

        @Override
        public boolean addAll(Collection cards)
        {
            boolean success = true;
            try {
                CardGroup.this.addAll(new ArrayList<Card>(cards));
            } catch (CardNumberException e) {
                System.err.println(e.getMessage());
                success = false;
            }
            return success;
        }

        @Override
        public boolean addAll(int index, Collection cards)
        {
            boolean success = true;
            try {
                CardGroup.this.addAll(new ArrayList<Card>(cards));
            } catch (CardNumberException e) {
                System.err.println(e.getMessage());
                success = false;
            }
            return success;
        }

        private void realAdd(Card card)
        {
            super.add(card);
        }
    }
    protected CardsArray cardList;
    protected final int NB_MAX_CARDS;

    /**
     * Constructs a CardGroup
     * @since v0.1
     * @param NB_MAX_CARDS the max number of card a subclass of CardGroup can have
     * @throws CardGroupNumberException
     */
    public CardGroup(int NB_MAX_CARDS) throws CardGroupNumberException {
        cardList = new CardsArray();
        this.NB_MAX_CARDS = NB_MAX_CARDS;
    }

    /**
     * Add a card
     * @since v0.1
     * @param card the card which is added to card list
     */
    public void add(Card card) throws CardNumberException {
        if ( cardList.size() >= NB_MAX_CARDS) {
            throw new CardNumberException("Card number limit has been reached.", NB_MAX_CARDS);
        } else {
            cardList.realAdd(card);
        }
    }

    /**
     * Add cards
     * @since v0.7
     * @param cards the cards which are added to card list
     */
    public void addAll(List<Card> cards) throws CardNumberException {
        for (Card card : cards)
        {
            add(card);
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
    public boolean findInCardsList(String nameToFind) {
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
    public Card getInCardsList(String nameToFind) {
        for (Card c : cardList)
            if (Objects.equals(c.getName(), nameToFind))
                return c;
        return null;
    }


    //GETTERS - no documentation needed

    public int size() {
        return cardList.size();
    }
    public CardsArray getCardList() {
        return cardList;
    }
}
