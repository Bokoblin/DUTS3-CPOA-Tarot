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

package consoleApplication;

import exceptions.CardGroupNumberException;

/**
 * The {@code ConsoleMain} class of console-only app
 * @author Arthur
 * @version v1.0.0
 * @since v0.7
 */
public class ConsoleMain {
    public static void main(String[] args) throws CardGroupNumberException {

        try {
            ConsoleGameModel consoleGameModel = new ConsoleGameModel();
            consoleGameModel.chooseInitialDealer();
            consoleGameModel.handleDealing();
            consoleGameModel.handleBids();
            System.out.println(consoleGameModel.toString());
            consoleGameModel.quitGame();
        }
        catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
        }

        System.exit(0);
    }
}
