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

package app;

import exceptions.*;
import java.util.List;

/**
 * Hand class extending CardGroup :
 * Group of cards representing the player
 *
 * @author Arthur
 * @version v0.1
 * @since v0.1
 *
 * @see CardGroup
 * @see Card
 */
public abstract class CardGroup {
    protected List<Card> cardList;

    /**
     * Default Ctor :
     * Create a new hand
     * @since v0.1
     *
     * @throws CardGroupInstancesNumberException
     */
    public CardGroup() throws CardGroupInstancesNumberException {

    }

    /**
     * Method : add a card to the CardGroup
     * @since v0.1
     */
    public abstract void addCard(Card card) throws CardNumberException;


    /**
     * Getter on card number
     * @since v0.1
     *
     * @return number of cards in the group
     */
    public int getCardNumber() {
        return cardList.size();
    }

    /**
     * Getter on card list
     * @since v0.1
     *
     * @return a list of cards
     */
    public List<Card> getCardList() {
        return cardList;
    }
}
