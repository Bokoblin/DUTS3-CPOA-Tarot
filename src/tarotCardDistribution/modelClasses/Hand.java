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

/**
 * The {@code Hand} class extends {@code CardGroup},
 * it consists in a group of cards representing the player
 *
 * @author Arthur
 * @version v0.2
 * @since v0.1
 *
 * @see CardGroup
 * @see Card
 */
public class Hand extends CardGroup{
    private static int handNumber;
    private final int HAND_NUMBER_MAX = 4;

    /**
     * Constructs a hand
     * @since v0.1
     *
     * @throws CardGroupInstancesNumberException if user tries to create too much hands
     */
    public Hand() throws CardGroupInstancesNumberException {
        super(18);
        if ( handNumber >= HAND_NUMBER_MAX)
            throw new CardGroupInstancesNumberException(
                    "Hand instances limit has been reached.", HAND_NUMBER_MAX);
        else {
            cardList = new ArrayList<>();
            handNumber++;
        }
    }

    public static int getHandNumber() {
        return handNumber;
    }

    public int getHandMaxNumber() {
        return HAND_NUMBER_MAX;
    }
}
