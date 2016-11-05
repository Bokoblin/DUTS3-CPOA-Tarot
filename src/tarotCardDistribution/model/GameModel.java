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

import java.util.*;

import exceptions.*;

/**
 * The {@code GameModel} class consists in the MVC architecture model
 * @author Arthur
 * @version v0.5
 * @since v0.2
 *
 * @see Observable
 * @see Card
 * @see Hand
 * @see Chien
 */
public class GameModel extends Observable {
    private List<Card> cardList;
    private Map<Integer, Hand> playerMap;
    private Chien chien;
    private Hand dealer;
    private Hand shuffler;
    private Hand cutter;
    private int currentPlayer;

    /**
     * Constructs app model by creating players, chien and cards
     * @since v0.5
     *
     * @throws CardNumberException if user tries to create too much cards
     * @throws CardUniquenessException if user tries to create too identical cards
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    public GameModel()
            throws CardNumberException, CardUniquenessException, CardGroupNumberException {
        cardList = new ArrayList<>();
        playerMap = new HashMap<>();

        //Players creation
        for (int i = 0; i<Hand.getNbMaxHands(); i++ ) {
           try {
               playerMap.put(i, new Hand());
           } catch (CardGroupNumberException e) {
               System.err.println(e.getMessage());
           }
        }


        //Cards creation
        for (Suit s : Suit.values()) {
            if ( s != Suit.Trump) {
                for (Rank r : Rank.values()) {
                    try {
                        cardList.add(new Card(s,r));
                    } catch (CardUniquenessException | CardNumberException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            else {
                for (int i = 1; i <= Card.getNbMaxTrumps(); i++) {
                    try {
                        cardList.add(new Card(Suit.Trump,i));
                    } catch (CardUniquenessException | CardNumberException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }
        try {
            cardList.add(new Card("Excuse")); //create the fool/Excuse
        } catch (Exception e) {
            e.getMessage();
        }

        //Chien creation
        try {
            chien = new Chien();
        } catch (CardGroupNumberException e) {
            e.getMessage();
        }

        dealer = null;
        shuffler = null;
        cutter = null;
    }

    /**
     * Do the game dealing
     * by choosing the dealer, the shuffler and the cutter,
     * then by shuffling, cutting, dealing and doing the bids
     * @since v0.5
     */
    public void handleDealing() {
        chooseInitialDealer();

        //shuffles, cuts, deals (is repeated following result deck for each player)
        boolean hasPetitSec = false;
        do {
            shuffler = playerMap.get((getNumPlayer(dealer)+2)%Hand.getNbMaxHands());
            shuffleCards();
            cutter = playerMap.get((getNumPlayer(dealer)+1)%Hand.getNbMaxHands());
            cutCards();

            //Changing player number following counter-clockwise order
            Hand beginner = playerMap.get((getNumPlayer(dealer)+3)%Hand.getNbMaxHands());
            playerMap.clear();
            playerMap.put( 1, beginner );
            playerMap.put( 2, shuffler );
            playerMap.put( 3, cutter );
            playerMap.put( 4, dealer );
            currentPlayer = 1;

            //Dealing
            dealCards();

            //"Petit Sec" checking
            for (Map.Entry<Integer, Hand> entry : playerMap.entrySet()) {
                hasPetitSec = entry.getValue().checkHasPetitSec();
                if (hasPetitSec) {
                    dealer = playerMap.get((getNumPlayer(dealer)+1)%Hand.getNbHands());
                    System.out.println("Hello : \n " + toString());
                    break;
                }
            }
        }
        while(hasPetitSec);
    }

    /**
     * Choose this game first dealer
     * @since v0.5
     */
    private void chooseInitialDealer() {
        //list of picked cards
        List<Card> cardListTemp = new ArrayList<>();

        //Copies cardList and shuffles the copy
        List<Card> randomPickedCardsList = new ArrayList<>();
        randomPickedCardsList.addAll(cardList);
        long seed = System.nanoTime();
        Collections.shuffle(randomPickedCardsList, new Random(seed));

        //Randomly chooses a card
        for (int i=0; i<playerMap.size(); i++ ) {
            Card c = randomCard(randomPickedCardsList);
            randomPickedCardsList.remove(c);
            if (!Objects.equals(c.getName(), "Excuse"))
                cardListTemp.add(c);
            //If the player picked Excuse, it must pick another card
            else {
                c = randomCard(randomPickedCardsList);
                randomPickedCardsList.remove(c);
                cardListTemp.add(c);
            }
        }

        dealer = getDealerFromPicking(cardListTemp);
    }

    /**
     * Shuffles cards
     * @since v0.5
     */
    public void shuffleCards() {
        long seed = System.nanoTime();
        Collections.shuffle(cardList, new Random(seed));
    }

    /**
     * Cuts cards
     * @since v0.5
     */
    public void cutCards() {

        //a cut list must contain more than 3 cards
        int splitIt;
        boolean isValidIterator = false;

        do {
            splitIt = cardList.indexOf(randomCard(cardList));
            if ( splitIt < 74 && splitIt > 3)
                isValidIterator = true;
        }
        while (!isValidIterator);

        List<Card> cut1 = new ArrayList<>();
        List<Card> cut2 = new ArrayList<>();

        for (int i=0; i <= splitIt; i++)
            cut1.add(cardList.get(i));
        for (int i=splitIt+1; i < cardList.size(); i++)
            cut2.add(cardList.get(i));

        cardList.clear();
        cardList.addAll(cut2);
        cardList.addAll(cut1);
    }

    /**
     * Deals card
     * @since v0.5
     */
    private void dealCards() {
        int cptNbCardGivenToSameHand = 0;
        for( Card c : cardList) {
            boolean chienReceiveCard = false;
            if( cardList.indexOf(c) >= 1 //Don't give first card to chien
                    && chien.getCardList().size() < 6 ) {
                if (cardList.indexOf(c) == Card.getNbCards()-2) { //Don't give last card to chien
                    chien.getCardList().add(c);
                    chienReceiveCard = true;
                }
                else {
                    chienReceiveCard = ( (new Random().nextInt(2) != 0)); //simulate a "heads or tails"
                    if (chienReceiveCard) {
                        chien.getCardList().add(c);
                    }
                }
            }
            if (!chienReceiveCard) {
                playerMap.get(currentPlayer).getCardList().add(c);
                cptNbCardGivenToSameHand++;
            }
            if (cptNbCardGivenToSameHand == 3) {
                currentPlayer = (currentPlayer%Hand.getNbHands())+1;
                cptNbCardGivenToSameHand = 0;
            }
        }
    }

    public void handleBids() {
        //TODO : Cards Bids
    }

    /**
     * Get the dealer by comparing cards
     * It's the hand that has picked the littlest/weakest card
     * @since v0.5
     */
    private Hand getDealerFromPicking(List<Card> list) {
        //players and cards picked have same index
        int minIndex = 0;
        for (int i=1; i<list.size(); i++)
            if ( Card.compareSmallerTo(list.get(i), list.get(minIndex)) )
                minIndex = i;
        return playerMap.get(minIndex);
    }


    /**
     * Get a random card in a card list
     * @since v0.5
     */
    private Card randomCard(List<Card> randomPickedCardsList) {
        int index = new Random().nextInt(randomPickedCardsList.size());
        return randomPickedCardsList.get(index);
    }

    /**
     * Displays card repartition after distribution
     * @since v0.5
     */
    @Override
    public String toString() {

        String result = "CARD REPARTITION :\n\n";

        for (Map.Entry<Integer, Hand> entry : playerMap.entrySet()) {
            result += "Player n°" + entry.getKey() + " : ";
            for (Card c : entry.getValue().getCardList() )
                result += c.getName() + "; ";
            result += "\n";
        }
        result += "Chien : ";
        for (Card c : chien.getCardList() )
            result += c.getName() + "; ";
        result += "\n";

        return result;
    }


    //GETTERS - no documentation needed

    public List<Card> getCardList() {
        return cardList;
    }
    public Map<Integer, Hand> getPlayerMap() {
        return playerMap;
    }
    public Chien getChien() {
        return chien;
    }
    public Hand getDealer() {
        return dealer;
    }
    public Hand getShuffler() {
        return shuffler;
    }
    public Hand getCutter() {
        return cutter;
    }
    public int getNumPlayer(Hand hand) {
        for (int key : playerMap.keySet())
            if (playerMap.get(key)== hand)
                return key;
        return 0;
    }
}
