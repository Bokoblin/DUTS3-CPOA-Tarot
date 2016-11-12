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
import java.util.HashMap;
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
        Talon.resetClassForTesting();
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
            assertTrue(gameModel.getInitialDeck().size() == 78);
            assertTrue(gameModel.getTalon() != null);
            assertTrue(gameModel.getTalon().getCardList().size() == 0);

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
            cardListToBeShuffled.addAll(gameModel.getInitialDeck());
            cardListToNotBeShuffled.addAll(cardListToBeShuffled);

            assertEquals(cardListToBeShuffled, cardListToNotBeShuffled);

            gameModel.shuffleCards();
            cardListToBeShuffled.clear();
            cardListToBeShuffled.addAll(gameModel.getInitialDeck());

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
            cardListToBeShuffled.addAll(gameModel.getInitialDeck());
            cardListToNotBeShuffled.addAll(cardListToBeShuffled);

            assertEquals(cardListToBeShuffled, cardListToNotBeShuffled);

            gameModel.cutCards();
            cardListToBeShuffled.clear();
            cardListToBeShuffled.addAll(gameModel.getInitialDeck());

            assertNotEquals(cardListToBeShuffled, cardListToNotBeShuffled);

        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on card dealing
     * No exception should be fired
     * @since v0.6
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void dealAllCardsTest()
            throws CardNumberException, CardUniquenessException, CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();

            //Card repartition before dealing
            assertTrue( !gameModel.getInitialDeck().isEmpty());
            assertTrue( gameModel.getTalon().getCardList().isEmpty());
            gameModel.getPlayerHandler().getPlayersMap().forEach( (cardinal,player)->
                    assertTrue(player.getCardList().isEmpty()) );

            gameModel.dealAllCards();

            assertTrue( gameModel.getInitialDeck().isEmpty());

            //each player has its 18 cards
            gameModel.getPlayerHandler().getPlayersMap().forEach( (cardinal,player)->
                assertTrue(player.getCardList().size() == 18) );

            //so do the chien
            assertTrue(gameModel.getTalon().getCardList().size() == 6);

        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on card gather after dealing
     * No exception should be fired
     * @since v0.6
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void gatherAllCardsTest()
            throws CardNumberException, CardUniquenessException, CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();
            gameModel.dealAllCards();

            assertTrue( gameModel.getInitialDeck().isEmpty());

            gameModel.gatherAllCards();

            //Card repartition after gathering
            assertTrue( gameModel.getInitialDeck().size() == 78);
            assertTrue( gameModel.getTalon().getCardList().isEmpty());
            gameModel.getPlayerHandler().getPlayersMap().forEach( (cardinal,player)->
                    assertTrue(player.getCardList().isEmpty()) );

        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on card moving between two decks
     * No exception should be fired
     * @since v0.6
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void moveCardsBetweenDecksTest()
            throws CardNumberException, CardUniquenessException, CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();

            List<Card> list1 = new ArrayList<>();
            List<Card> list2 = new ArrayList<>();
            assertTrue( list1.isEmpty());
            assertTrue( list2.isEmpty());


            Card c = gameModel.randomCard(gameModel.getInitialDeck());
            list1.add(c);

            assertTrue( list1.size() == 1);
            assertTrue( list2.isEmpty());

            gameModel.moveCardBetweenDecks(list1, list2, c);

            assertTrue( list1.isEmpty());
            assertTrue( list2.size() == 1);


        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on random class method
     * No exception should be fired
     * @since v0.6
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void randomCardTest()
            throws CardNumberException, CardUniquenessException, CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();

            Map<Card, Integer> occurenceCard = new HashMap<>();

            //Initialize with each card and occurence 0
            for( int i=0; i < 78; i++) {
                occurenceCard.put( gameModel.getInitialDeck().get(i), 0);
            }

            //Calling randomCard() method a high number of time to check if it is random
            for( int i=0; i < 1_000_000; i++) {
                Card c = gameModel.randomCard(gameModel.getInitialDeck());
                occurenceCard.replace(c, occurenceCard.get(c), occurenceCard.get(c)+1);
            }

            //Checking percent rate
            double mean = 1_000_000/gameModel.getInitialDeck().size();
            double delta = mean*0.05; //standard deviation of 5%

            for (Map.Entry<Card, Integer> mapEntry : occurenceCard.entrySet()) {
                assertEquals(mean, mapEntry.getValue(), delta);
            }
            System.out.println("I=[" + (mean-delta) + ", " + (mean+delta) +"]");


        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }
}