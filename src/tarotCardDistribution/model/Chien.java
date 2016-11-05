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

/**
 * The {@code Chien} class extends {@code CardGroup},
 * it consists in a group of cards representing the chien/talon
 *
 * @author Arthur
 * @version v0.5
 * @since v0.1
 *
 * @see CardGroup
 * @see Card
 */
public class Chien extends CardGroup{
    private static boolean exist = false;

    /**
     * Constructs a chien
     * @since v0.1
     *
     * @throws CardGroupNumberException if user tries to create more than one chien
     */
    public Chien() throws CardGroupNumberException {
        super(6); //Max number of cards for this group
        if (exist)
            throw new CardGroupNumberException("Only one Chien is possible.");
        else {
            cardList = new ArrayList<>();
            exist = true;
        }
    }

    /**
     * Reset static field for unit tests
     * @since v0.5
     */
    public static void resetClassForTesting() {
        exist = false;
    }

    /**
     * Return if chien exists
     * @since v0.5
     */
    public static boolean exists() {
        return exist;
    }
}
