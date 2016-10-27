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

import java.util.ArrayList;

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
public class Hand extends CardGroup{
    private static int nb;
    private static final int nbMax = 4;
    private final int cardNumberMax = 18;

    /**
     * Default Ctor :
     * Create a new hand
     * @since v0.1
     *
     * @throws CardGroupInstancesNumberException if user tries to create too much hands
     */
    public Hand() throws CardGroupInstancesNumberException {
        if ( nb >= nbMax)
            throw new CardGroupInstancesNumberException(
                    "Hand instances limit has been reached.", nbMax);
        else {
            cardList = new ArrayList<>();
            nb++;
        }
    }

    /**
     * Method : add a card to the hand
     * @since v0.1
     */
    @Override
    public void addCard(Card card) throws CardNumberException {
        if ( cardList.size() >= cardNumberMax)
            throw new CardNumberException(
                    "Hand card number limit has been reached.", cardNumberMax);
        else {
            cardList.add(card);
        }
    }

    /**
     * Getter on instance number
     * @since v0.1
     *
     * @return number of instances
     */
    public static int getNumber() {
        return nb;
    }
}
