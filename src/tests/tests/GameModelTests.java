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

package tests;

import tarotCardDistribution.model.*;
import exceptions.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * GameModel Unit tests
 *
 * @author Arthur
 * @version v0.5
 * @since v0.5
 */
public class GameModelTests {

    /**
     * Reset static fields before each test
     * @since v0.5
     */
    @Before
    public void cleanClasses() {
        Card.resetClassForTesting();
        Hand.resetClassForTesting();
        Chien.resetClassForTesting();
    }

    /**
     * Test if instantiation increments Card instances number
     * No exception should be fired
     * @since v0.5
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void GameModelInstantiationTest()
            throws CardNumberException, CardUniquenessException, CardGroupNumberException {
        try {
            GameModel gameModel = new GameModel();
            assertTrue(gameModel.getPlayerMap().size() == 4);
            assertTrue(gameModel.getCardList().size() == 78);
            assertTrue(gameModel.getChien() != null);
            assertTrue(gameModel.getChien().getCardList().size() == 0);

        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on handleDealingMethod
     * No exception should be fired
     * @since v0.5
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void handleDealingTest()
            throws CardNumberException, CardUniquenessException, CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();
            assertTrue(gameModel.getDealer() == null);

            gameModel.handleDealing();

            assertTrue(gameModel.getDealer() != null);
            assertTrue(gameModel.getShuffler() != null);
            assertTrue(gameModel.getCutter() != null);

            int numDealer = gameModel.getNumPlayer(gameModel.getDealer());
            int numShuffler = gameModel.getNumPlayer(gameModel.getShuffler());
            int numCutter = gameModel.getNumPlayer(gameModel.getCutter());

            //the player opposite the dealer shuffles
            assertTrue(numShuffler == (numDealer+2)%4);

            //the player to the left of the dealer cuts
            assertTrue(numCutter == (numDealer+3)%4);

            //each player has its 18 cards
            for (Map.Entry<Integer, Hand> entry : gameModel.getPlayerMap().entrySet())
                assertTrue(entry.getValue().getCardList().size() == 18);

            //so do the chien
            assertTrue(gameModel.getChien().getCardList().size() == 6);


        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }

    }

    /**
     * Test on shuffling Cards method
     * No exception should be fired
     * @since v0.5
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void shufflingCardsTest()
            throws CardNumberException, CardUniquenessException, CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();

            List<Card> cardListToBeShuffled = new ArrayList<>();
            List<Card> cardListToNotBeShuffled = new ArrayList<>();
            cardListToBeShuffled.addAll(gameModel.getCardList());
            cardListToNotBeShuffled.addAll(cardListToBeShuffled);

            assertEquals(cardListToBeShuffled, cardListToNotBeShuffled);

            gameModel.shuffleCards();
            cardListToBeShuffled.clear();
            cardListToBeShuffled.addAll(gameModel.getCardList());

            assertNotEquals(cardListToBeShuffled, cardListToNotBeShuffled);

        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on cutting Cards method
     * No exception should be fired
     * @since v0.5
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void cuttingCardsTest()
            throws CardNumberException, CardUniquenessException, CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();

            List<Card> cardListToBeShuffled = new ArrayList<>();
            List<Card> cardListToNotBeShuffled = new ArrayList<>();
            cardListToBeShuffled.addAll(gameModel.getCardList());
            cardListToNotBeShuffled.addAll(cardListToBeShuffled);

            assertEquals(cardListToBeShuffled, cardListToNotBeShuffled);

            gameModel.cutCards();
            cardListToBeShuffled.clear();
            cardListToBeShuffled.addAll(gameModel.getCardList());

            assertNotEquals(cardListToBeShuffled, cardListToNotBeShuffled);

        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }

    }
}