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

package appConsole;

import exceptions.CardGroupNumberException;
import exceptions.CardNumberException;
import exceptions.CardUniquenessException;

/**
 * The {@code Main} class of console-only app
 * @author Arthur
 * @version v0.7
 * @since v0.7
 */
public class Main {
    public static void main(String[] args)
            throws CardNumberException, CardUniquenessException, CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();

            //shuffling, cut, dealing
            gameModel.chooseInitialDealer();
            gameModel.handleDealing();
            gameModel.handleBids();
            System.out.println(gameModel.toString());
        }
        catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
        }

        //Game playing is not to be done
        System.exit(0);
    }
}
