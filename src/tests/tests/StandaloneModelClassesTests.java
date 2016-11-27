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

import app.model.*;
import org.junit.*;
import static org.junit.Assert.*;
import exceptions.*;

/**
 * Standalone Unit tests for model classes
 *
 * @author Arthur
 * @version v0.8.1
 * @since v0.1
 */
public class StandaloneModelClassesTests {

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
     * @since v0.1
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     */
    @Test
    public void CardInstantiationTest()
            throws CardNumberException, CardUniquenessException{
        assertTrue(Card.getNbCards() == 0);
        assertTrue(Card.getNbMaxCards() == 78);

        try {
            new Card(Suit.Heart, Rank.Ace);
            assertTrue(Card.getNbCards() == 1);
        } catch (CardNumberException | CardUniquenessException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Tests if Card class is not too much instantiated :
     * only a limited number of cards is allowed
     * No exception should be fired
     * @since v0.1
     *
     * @throws CardNumberException if user tries to create too much cards
     */
    @Test
    public void MultipleCardInstantiationTest()
            throws CardNumberException {
        try {
            for (int i = 1; i <= 75; i++) {
                new Card();
            }
        }
        catch (CardNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
        finally {
            assertTrue(Card.getNbCards() == 75);
            assertTrue(Card.getNbCards() <= Card.getNbMaxCards());
        }
    }

    /**
     * Tests adding cards to the hand
     * Note : test is similar for the Talon
     * No exception should be fired
     * @since v0.1
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void AddCardsToTheHand()
            throws CardNumberException, CardGroupNumberException {
        try {
            Hand hand = new Hand();
            for (int i = 1; i <= 18; i++) {
                hand.add(new Card() );
            }
            assertTrue(hand.size() == 18);
        }
        catch (CardNumberException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }

    /**
     * Tests creating the hand and the Talon
     * Two exceptions should be fired
     * @since v0.1
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void handAndChienInstantiation()
            throws CardGroupNumberException {

        try {
            assertTrue(Hand.getNbHands() == 0);
            for (int i = 1; i < 4; i++) {
                assertTrue(new Hand().size() == 0);
            }
            assertTrue(Hand.getNbHands() == 3);
        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }

        try {
            assertTrue( !Talon.exists() );
            assertTrue(new Talon().size() == 0);
            assertTrue( Talon.exists() );
        }
        catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }


    /**
     * Test if cards are correctly compared
     * following their Suit and Rank
     * @since v0.7.2
     */
    @Test
    public void CardComparisonTest() {
        try {
            Card trumpCard1 = new Card( Suit.Trump, 11);
            Card trumpCard2 = new Card( Suit.Trump, 18);
            Card classicCard1 = new Card( Suit.Heart, Rank.Eight);
            Card classicCard2 = new Card( Suit.Heart, Rank.Queen);
            Card classicCard3 = new Card( Suit.Spade, Rank.King);
            Card excuse = new Card(Suit.Excuse, -1);

            Card.CardComparator cardComparator = new Card.CardComparator();

            //Same Type comparison
            assertTrue(cardComparator.compare(trumpCard1, trumpCard1) == 0);      //11 = 11
            assertTrue(cardComparator.compare(classicCard1, classicCard1) == 0);  //Eight = Eight
            assertTrue(cardComparator.compare(excuse, excuse) == 0);              //Excuse = Excuse
            assertTrue(cardComparator.compare(trumpCard1, trumpCard2) == -1);     //11 < 18
            assertTrue(cardComparator.compare(classicCard1, classicCard2) == -1); //Eight < Queen

            //NotSame Type comparison
            assertTrue(cardComparator.compare(trumpCard1, classicCard1) == 1);  //Trump > Heart
            assertTrue(cardComparator.compare(classicCard3, trumpCard1) == -1); //Spade < Trump
            assertTrue(cardComparator.compare(classicCard1, excuse) == -1);     //Heart < Excuse
            assertTrue(cardComparator.compare(trumpCard1, excuse) == -1);       //Trump < Excuse

        } catch (CardNumberException | CardUniquenessException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        }
    }



    /**
     * Tests CardNumberException that should be fired
     * @since v0.5
     *
     * @throws CardNumberException if user tries to create too much cards
     */
    @Test
    public void MultipleCardInstantiationExceptionTest()
            throws CardNumberException {
        //Instancing too much cards test
        try {
            for (int i = 1; i <= 79; i++) {
                new Card();
            }
            fail("Exception should be fired");
        }
        catch (CardNumberException e) {
            System.err.println(e.getMessage());
        }
        finally {
            assertTrue(Card.getNbCards() <= Card.getNbMaxCards());
        }
    }

    /**
     * Tests CardUniquenessException that should be fired
     * @since v0.1
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     */
    @Test
    public void CardUniquenessExceptionTest()
            throws CardNumberException, CardUniquenessException {

        Card c1 = null;
        Card c2 = null;

        try {
            c1 = new Card(Suit.Diamond, Rank.King);
        } catch (CardNumberException e) {
            System.err.println(e.getMessage());
            fail("Exception shouldn't be fired");
        } finally {
            assertTrue(c1 != null);
        }

        try {
            c2 = new Card(Suit.Diamond, Rank.King);
            fail("Exception should be fired");
        } catch (CardNumberException | CardUniquenessException e) {
            System.err.println(e.getMessage());
            assertTrue( e instanceof CardUniquenessException);
        } finally {
            assertTrue(c2 == null);
        }
    }

    /**
     * Tests CardGroupNumberException that should be fired
     * @since v0.5
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void CardNumberExceptionTest()
            throws CardNumberException, CardGroupNumberException {
        try {
            Hand hand = new Hand();
            for (int i = 1; i < 20; i++) {
                if (!hand.add(new Card() ))
                    throw new CardNumberException("Card number limit has been reached.", hand.getNbMaxCards());
            }
            fail("Exception should be fired");
        }
        catch (CardNumberException | CardGroupNumberException e) {
            System.err.println(e.getMessage());
            assertTrue( e instanceof CardNumberException);
        }
    }

    /**
     * Tests creating the hand and the Talon
     * Two exceptions should be fired
     * @since v0.5
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    @Test
    public void CardGroupNumberException()
            throws CardGroupNumberException {

        try {
            for (int i = 0; i < 5; i++) {
                new Hand();
            }
            fail("Exception should be fired");
        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            assertTrue( Hand.getNbHands() == 4);
        }

        try {
            new Talon();
            new Talon();
            fail("Exception should be fired");
        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            assertTrue( Talon.exists() );
        }
    }
}