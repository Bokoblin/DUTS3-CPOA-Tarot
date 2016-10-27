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
 * Chien class :
 * Group of cards put at the center of the table
 *
 * @author Arthur
 * @version v0.1
 * @since v0.1
 *
 * @see CardGroup
 * @see Card
 */
public class Chien extends CardGroup{
    private static int nb;
    private static final int nbMax = 1;
    private final int cardNumberMax = 6;

    /**
     * Default Ctor :
     * Create a new chien
     * @since v0.1
     *
     * @throws CardGroupInstancesNumberException user can only create 1 Chien
     */
    public Chien() throws CardGroupInstancesNumberException {
        if ( nb >= nbMax)
            throw new CardGroupInstancesNumberException("Only one Chien is possible.");
        else {
            cardList = new ArrayList<>();
            nb++;
        }
    }

    /**
     * Method : add a card to the chien
     * @since v0.1
     */
    @Override
    public void addCard(Card card) throws CardNumberException {
        if ( cardList.size() >= cardNumberMax)
            throw new CardNumberException(
                    "Hand card number limit has been reached.\n", nbMax);
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
