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

import org.junit.Test;
import static org.junit.Assert.*;
import exceptions.*;
import tarotCardDistribution.modelClasses.*;

/**
 * App Unit tests
 *
 * @author Arthur
 * @version v0.1
 * @since v0.1
 */
public class AppTest {

    /**
     * Test if instantiation increments Card instances number
     * No exception should be fired
     * @since v0.1
     */
    @Test
    public void CardInstantiationTest() {
        assertTrue(Card.getCardNumber() == 0);
        assertTrue(Card.getTotalCardNumber() == 78);

        try {
            Card c = new Card(Suit.Heart, Rank.Ace);
        } catch (CardNumberException | CardUniquenessException e) {
            System.err.println(e.getMessage());
        }

        assertTrue(Card.getCardNumber() == 1);
    }

    /**
     * Tests uniqueness of instances :
     * prevent instancing twice the same card
     * One exception should be fired
     * @since v0.1
     */
    @Test
    public void CardUniquenessTest()
            throws CardNumberException, CardUniquenessException {

        Card c1 = null;
        try {
            c1 = new Card(Suit.Diamond, Rank.King);
        } catch (CardNumberException e) {
            System.err.println(e.getMessage());
        }
        Card c2 = null;
        try {
            c2 = new Card(Suit.Diamond, Rank.King);
        } catch (CardUniquenessException e) {
            System.err.println(e.getMessage());
        }

        assertTrue(c2 == null);
    }

    /**
     * Tests adding cards to the hand
     * Note : test is similar for the Chien
     */
    @Test
    public void AddCardsToTheHand() throws CardNumberException {
        Hand hand = null;
        try {
            hand = new Hand();
            for (int i = 1; i < 20; i++) {
                hand.addCard(new Card() );
            }
        } catch (CardNumberException | CardGroupInstancesNumberException e) {
            System.err.println(e.getMessage());
        }

        assert hand != null;
        assertTrue(hand.getCardNumber() == 18);
    }

    /**
     * Tests creating the hand and the Chien
     * Two exceptions should be fired
     */
    @Test
    public void handAndChienInstantiation()
            throws CardGroupInstancesNumberException {

        try {
            for (int i = 1; i < 5; i++) {
                Hand hand = new Hand();
            }
        } catch (CardGroupInstancesNumberException e) {
            System.err.println(e.getMessage());
        }

        try {
            Chien chien1 = new Chien();
            Chien chien2 = new Chien();
        } catch (CardGroupInstancesNumberException e) {
            System.err.println(e.getMessage());
        }

        assertTrue( Hand.getHandNumber() == 4);
    }

    /**
     * Tests if Card class is not too much instantiated :
     * only a limited number of cards is allowed
     * One exception should be fired
     * @since v0.1
     */
    @Test
    public void MultipleCardInstantiationTest()
            throws CardNumberException, CardUniquenessException {
        //Instancing too much cards test
        try {
            for (int i = 1; i < 80; i++) {
                Card c = new Card();
            }
        } catch (CardNumberException e) {
            System.err.println(e.getMessage());
        }
        assertTrue(Card.getCardNumber() <= Card.getTotalCardNumber());
    }
}