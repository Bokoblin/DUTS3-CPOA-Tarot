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
 * The {@code Chien} class extends {@code CardGroup},
 * it consists in a group of cards representing the chien/talon
 *
 * @author Arthur
 * @version v0.2
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
     * @throws CardGroupInstancesNumberException if user tries to create more than one chien
     */
    public Chien() throws CardGroupInstancesNumberException {
        super(6);
        if (exist)
            throw new CardGroupInstancesNumberException("Only one Chien is possible.");
        else {
            cardList = new ArrayList<>();
            exist = true;
        }
    }

}
