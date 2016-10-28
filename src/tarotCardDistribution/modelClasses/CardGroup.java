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
import java.util.List;

/**
 * The {@code CardGroup} class consists in a group of cards.
 * It is abstract so it only defines common attributes and methods
 * for {@code Hand} ans {@code Chien} classes
 * @author Arthur
 * @version v0.2
 * @since v0.1
 *
 * @see CardGroup
 * @see Card
 */
public abstract class CardGroup {
    protected List<Card> cardList;
    protected final int CARD_NUMBER_MAX;

    /**
     * Constructs a CardGroup
     * @since v0.1
     *
     * @param CARD_NUMBER_MAX the max number of card a subclass of CardGroup can have
     * @throws CardGroupInstancesNumberException
     */
    public CardGroup(int CARD_NUMBER_MAX) throws CardGroupInstancesNumberException {
        this.CARD_NUMBER_MAX = CARD_NUMBER_MAX;
    }

    /**
     * Add a card
     * @since v0.1
     *
     * @param card the card which is added to card list
     */
    public void addCard(Card card) throws CardNumberException {
        if ( cardList.size() >= CARD_NUMBER_MAX)
            throw new CardNumberException(
                    "Card number limit has been reached.", CARD_NUMBER_MAX);
        else {
            cardList.add(card);
        }
    }


    public int getCardNumber() {
        return cardList.size();
    }
    public List<Card> getCardList() {
        return cardList;
    }
}
