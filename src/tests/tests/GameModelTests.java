/*
Copyright 2016 Jacquot Alexandre, Jolivet Arthur S3A
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

import app.model.*;
import exceptions.CardGroupNumberException;
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
 * @version v0.9
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
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void GameModelInstantiationTest() throws CardGroupNumberException {
        try {
            GameModel gameModel = new GameModel();
            gameModel.createCards();
            assertTrue(gameModel.getWholeCardsDeck().size() == 78);
            assertTrue(gameModel.getTalon() != null);
            assertTrue(gameModel.getTalon().size() == 0);

        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on shuffling Cards method
     * No exception should be fired
     * @since v0.5
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void shufflingCardsTest() throws CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();
            gameModel.createCards();

            List<Card> cardListToBeShuffled = new ArrayList<>();
            List<Card> cardListToNotBeShuffled = new ArrayList<>();
            cardListToBeShuffled.addAll(gameModel.getWholeCardsDeck());
            cardListToNotBeShuffled.addAll(cardListToBeShuffled);

            assertEquals(cardListToBeShuffled, cardListToNotBeShuffled);

            gameModel.shuffleCards();
            cardListToBeShuffled.clear();
            cardListToBeShuffled.addAll(gameModel.getWholeCardsDeck());

            assertNotEquals(cardListToBeShuffled, cardListToNotBeShuffled);

        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on cutting Cards method
     * No exception should be fired
     * @since v0.5
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void cuttingCardsTest() throws CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();
            gameModel.createCards();

            List<Card> cardListToBeShuffled = new ArrayList<>();
            List<Card> cardListToNotBeShuffled = new ArrayList<>();
            cardListToBeShuffled.addAll(gameModel.getWholeCardsDeck());
            cardListToNotBeShuffled.addAll(cardListToBeShuffled);

            assertEquals(cardListToBeShuffled, cardListToNotBeShuffled);

            gameModel.cutDeck();
            cardListToBeShuffled.clear();
            cardListToBeShuffled.addAll(gameModel.getWholeCardsDeck());

            assertNotEquals(cardListToBeShuffled, cardListToNotBeShuffled);

        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on card dealing
     * No exception should be fired
     * @since v0.6
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void dealAllCardsTest() throws CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();
            gameModel.createCards();

            //Card repartition before dealing
            assertTrue( !gameModel.getWholeCardsDeck().isEmpty());
            assertTrue( gameModel.getTalon().isEmpty());
            gameModel.getPlayerHandler().getPlayersMap().forEach( (cardinal,player)->
                    assertTrue(player.isEmpty()) );

            gameModel.dealAllCards();

            assertTrue( gameModel.getWholeCardsDeck().isEmpty());

            //each player has its 18 cards
            gameModel.getPlayerHandler().getPlayersMap().forEach( (cardinal,player)->
                    assertTrue(player.size() == 18) );

            //so do the chien
            assertTrue(gameModel.getTalon().size() == 6);

        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on card gather after dealing
     * No exception should be fired
     * @since v0.6
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void gatherAllCardsTest() throws CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();
            gameModel.createCards();
            gameModel.dealAllCards();

            assertTrue( gameModel.getWholeCardsDeck().isEmpty());

            gameModel.gatherAllCards();

            //Card repartition after gathering
            assertTrue( gameModel.getWholeCardsDeck().size() == 78);
            assertTrue( gameModel.getTalon().isEmpty());
            gameModel.getPlayerHandler().getPlayersMap().forEach( (cardinal,player)->
                    assertTrue(player.isEmpty()) );

        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on card moving between two decks
     * No exception should be fired
     * @since v0.6
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void moveCardsBetweenDecksTest() throws CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();
            gameModel.createCards();

            CardGroup list1 = new CardGroup(100);
            CardGroup list2 = new CardGroup(100);
            assertTrue( list1.isEmpty());
            assertTrue( list2.isEmpty());


            Card c = gameModel.randomCard(gameModel.getWholeCardsDeck());
            list1.add(c);

            assertTrue( list1.size() == 1);
            assertTrue( list2.isEmpty());

            gameModel.moveCardBetweenDecks(list1, list2, c, true);

            assertTrue( list1.isEmpty());
            assertTrue( list2.size() == 1);


        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Test on random class method
     * No exception should be fired
     * @since v0.6
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void randomCardTest() throws CardGroupNumberException {
        GameModel gameModel;
        try {
            gameModel = new GameModel();
            gameModel.createCards();

            Map<Card, Integer> occurrenceCard = new HashMap<>();

            //Initialize with each card and occurrence 0
            for( int i=0; i < 78; i++) {
                occurrenceCard.put( gameModel.getWholeCardsDeck().get(i), 0);
            }

            //Calling randomCard() method a high number of time to check if it is random
            for( int i=0; i < 1_000_000; i++) {
                Card c = gameModel.randomCard(gameModel.getWholeCardsDeck());
                occurrenceCard.replace(c, occurrenceCard.get(c), occurrenceCard.get(c)+1);
            }

            //Checking percent rate
            double mean = 1_000_000/gameModel.getWholeCardsDeck().size();
            double delta = mean*0.05; //standard deviation of 5%

            for (Map.Entry<Card, Integer> mapEntry : occurrenceCard.entrySet()) {
                assertEquals(mean, mapEntry.getValue(), delta);
            }
            System.out.println("I=[" + (mean-delta) + ", " + (mean+delta) +"]");


        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }
}