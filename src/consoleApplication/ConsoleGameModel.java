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

import app.model.*;
import exceptions.CardGroupNumberException;
import exceptions.CardNumberException;
import exceptions.CardUniquenessException;

import java.util.*;

/**
 * The {@code ConsoleGameModel} class contains tarot game model
 * It handles dealer choosing, dealing, bids choosing
 * and ecart constituting (if applicable)
 * @author Arthur
 * @version v1.0.2
 * @since v0.2
 *
 * @see Observable
 * @see Card
 * @see Hand
 * @see PlayerHandler
 * @see Talon
 */

class ConsoleGameModel extends Observable {

    private CardGroup wholeCardsDeck;
    private CardGroup toPickDeck;
    private CardGroup pickedCardsDeck;
    private Map<Card, Hand> pickedCardsMap;
    private PlayerHandler playerHandler;
    private Talon chien;
    private Hand myPlayer;
    private GameState gameState;

    /**
     * Constructs app model by creating players, chien and cards
     * @since v0.5
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    ConsoleGameModel() throws CardGroupNumberException {
        toPickDeck = new CardGroup(78);
        wholeCardsDeck = new CardGroup(78);
        pickedCardsMap = new HashMap<>();
        pickedCardsDeck = new CardGroup(4);

        //Players creation
        playerHandler = new PlayerHandler();
        myPlayer = playerHandler.getPlayer(PlayerHandler.PlayersCardinalPoint.South);

        //Chien creation
        try {
            chien = new Talon();
        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
        }

        createCards();
    }

    /**
     * Creates all cards and puts them in wholeCardsDeck
     * @since v0.7.2
     */
    private void createCards() {
        for (Suit s : Suit.values()) {
            if ( s != Suit.Trump && s != Suit.Excuse) {
                for (Rank r : Rank.values()) {
                    try {
                        Card c = new Card(s,r);
                        if(!wholeCardsDeck.add(c))
                            throw new CardNumberException("Card number limit has been reached.",
                                    wholeCardsDeck.getNbMaxCards());

                    } catch (CardUniquenessException | CardNumberException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            else if ( s != Suit.Excuse){
                for (int i = 1; i <= Card.getNbMaxTrumps(); i++) {
                    try {
                        Card c = new Card(Suit.Trump,i);
                        if(!wholeCardsDeck.add(c))
                            throw new CardNumberException("Card number limit has been reached.",
                                    wholeCardsDeck.getNbMaxCards());

                    } catch (CardUniquenessException | CardNumberException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            else {
                try {
                    Card c = new Card(Suit.Excuse, -1);
                    if(!wholeCardsDeck.add(c))
                        throw new CardNumberException("Card number limit has been reached.", wholeCardsDeck.getNbMaxCards());
                } catch (CardNumberException | CardUniquenessException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        changeGameState(GameState.CARDS_SPREADING);
    }

    /**
     * Choose this game first dealer
     * by picking a card among the 78 ones
     * The hand that has picked the littlest/weakest card
     * becomes the dealer
     * @since v0.5
     */
    void chooseInitialDealer() {

        shuffleCards();
        spreadCards();

        //Handle dealer choosing
        changeGameState(GameState.DEALER_CHOOSING);
        System.out.println("=== DEALER CHOOSING ===\n");
        playerHandler.getPlayersMap().forEach((cardinalPoint,player)-> {
            Card c;
            if ( player == myPlayer) {
                System.out.println("Choose your card by entering its number between 0 and 77");
                Scanner sc = new Scanner(System.in);
                boolean choiceValid = false;
                int numCard = 0;
                do {
                    try {
                        numCard = sc.nextInt();
                        if ( numCard >= 0 && numCard < 78 && toPickDeck.get(numCard).getSuit() == Suit.Excuse) {
                            moveCardBetweenDecks(toPickDeck, pickedCardsDeck, toPickDeck.get(numCard));
                            pickedCardsMap.put( pickedCardsDeck.get(numCard), player);
                        }
                        else if ( numCard >= 0 && numCard < 78 && Objects.nonNull(toPickDeck.get(numCard)) ) {
                            choiceValid = true;
                            System.out.println("Your choice have been saved.");
                        }
                        else
                            System.out.println("Your choice isn't valid, try again");
                    } catch ( Exception e) {
                        System.out.println("Your choice isn't valid, try again");
                        sc.nextLine();
                    }
                }
                while (!choiceValid);
                c = toPickDeck.get(numCard);
            }
            else {
                do {
                    c = randomCard(toPickDeck);
                }
                while (c.getSuit() == Suit.Excuse);
            }

            moveCardBetweenDecks(toPickDeck, pickedCardsDeck, c);
            pickedCardsMap.put(c, player);
        });

        //Flip cards for players to see which is the lowest
        flipDeck(pickedCardsDeck, true);

        //Set dealer from picking
        Card minCard = null;
        for (Map.Entry<Card, Hand> mapEntry : pickedCardsMap.entrySet())
            if (Objects.isNull(minCard) ||
                    (new Card.CardComparator().compare(mapEntry.getKey(), minCard) == -1))
                minCard = mapEntry.getKey();

        playerHandler.setFirstDealer(pickedCardsMap.get(minCard));

        changeGameState(GameState.DEALER_CHOSEN);

        //Flip cards again and put them back to wholeCardsDeck
        flipDeck(pickedCardsDeck, false);
        gatherAllCards();
    }


    /**
     * Do Tarot Dealing by shuffling, cutting and dealing cards
     * If a player has Petit Sec after dealing,
     * new dealer, shuffler and cutter are designated
     * and card deck is again shuffled, cut and dealt
     * @since v0.5
     */
    void handleDealing() {
        boolean hasPetitSec = false;
        do {
            shuffleCards();
            cutDeck();
            dealAllCards();

            flipDeck(myPlayer, true);

            playerHandler.getPlayersMap().forEach( (cardinalPoint, playerHand) -> sortDeck(playerHand));


            //"Petit Sec" checking
            System.out.println("Petit Sec checking...");
            for (Map.Entry<PlayerHandler.PlayersCardinalPoint, Hand> player
                    : playerHandler.getPlayersMap().entrySet()) {

                hasPetitSec = player.getValue().checkHasPetitSec();
                if (hasPetitSec) {
                    changeGameState(GameState.PETIT_SEC_DETECTED);
                    flipDeck(myPlayer, false);
                    playerHandler.changeDealer();
                    gatherAllCards();
                    System.out.println("The player has Petit Sec, re-dealing...");
                    break;
                }
            }
        }
        while (hasPetitSec);
    }


    /**
     * Deals card
     * @since v0.5
     */
    private void dealAllCards() {
        changeGameState(GameState.CARDS_DEALING);
        System.out.println("Dealing cards...");
        int cptNbCardGivenToSameHand = 0;
        while( !wholeCardsDeck.isEmpty()) {
            boolean chienReceiveCard = false;
            if( chien.size() < 6
                    && wholeCardsDeck.size() < 78) { //Don't give first card to chien
                if ( wholeCardsDeck.size() == 2 ) { //Don't give last card to chien
                    moveCardBetweenDecks(wholeCardsDeck, chien, wholeCardsDeck.get(0));
                    chienReceiveCard = true;
                }
                else {
                    chienReceiveCard = ( (new Random().nextInt(4) == 0)); //25% it chooses to put it in Talon
                    if (chienReceiveCard) {
                        moveCardBetweenDecks(wholeCardsDeck, chien, wholeCardsDeck.get(0));
                    }
                }
            }
            if (!chienReceiveCard) {
                moveCardBetweenDecks(wholeCardsDeck, playerHandler.getCurrentPlayer(), wholeCardsDeck.get(0));
                cptNbCardGivenToSameHand++;
            }
            if (cptNbCardGivenToSameHand == 3) {
                playerHandler.changeCurrentPlayer();
                cptNbCardGivenToSameHand = 0;
            }
        }
    }


    /**
     * Retrieve all cards from players and chien
     * to the initial deck
     * @since v0.6
     */
    private void gatherAllCards() {
        playerHandler.getPlayersMap().forEach((cardinalPoint,player)-> {
            while ( !player.isEmpty() ) {
                moveCardBetweenDecks(player, wholeCardsDeck, player.get(0));
            }
        });
        while ( !chien.isEmpty() ) {
            moveCardBetweenDecks(chien, wholeCardsDeck, chien.get(0));
        }
        while ( !pickedCardsDeck.isEmpty() ) {
            moveCardBetweenDecks(pickedCardsDeck, wholeCardsDeck, pickedCardsDeck.get(0));
        }
        while ( !toPickDeck.isEmpty() ) {
            moveCardBetweenDecks(toPickDeck, wholeCardsDeck, toPickDeck.get(0));
        }
    }


    /**
     * Handle the Bids
     * @since v0.6
     */
    void handleBids() {
        chooseBids();
        while (myPlayer.getBidChosen() == Bids.Pass) {
            System.out.println("You've chosen to pass. Re-dealing...");
            flipDeck(myPlayer, false);
            gatherAllCards();
            playerHandler.changeDealer();
            handleDealing();
            chooseBids();
        }
        System.out.println("You are the taker");
        if ( myPlayer.getBidChosen()== Bids.Small || myPlayer.getBidChosen()== Bids.Guard ) {
            System.out.println("You're allowed to constitute your ecart");
            constituteEcart();
            changeGameState(GameState.ECART_CONSTITUTED);
        }
    }


    /**
     * Choose a bid for each player
     * @since v0.6
     */
    private void chooseBids() {
        System.out.println("\n=== BIDS ===\n");
        playerHandler.getPlayersMap().forEach((cardinal,player)-> {
            if ( player == myPlayer) {
                flipDeck(myPlayer, true);
                System.out.println("Here are your cards :");
                System.out.println(myPlayer.cardListToString());
                System.out.println("Choose your Bids among those one :");
                System.out.println("1. Small\n2. Guard\n3. GuardWithoutTheKitty\n4. GuardAgainstTheKitty\n5. Pass");
                Scanner sc = new Scanner(System.in);
                boolean choiceValid = false;
                int choice = 5;
                do {
                    try {
                        choice = sc.nextInt();
                        if (choice <= 5 && choice > 0) {
                            choiceValid = true;
                        } else
                            System.out.println("Your choice isn't valid, try again");
                    } catch (Exception e) {
                        System.out.println("Your choice isn't valid, try again");
                        sc.nextLine();
                    }
                }
                while (!choiceValid);

                try {
                    myPlayer.setBidChosen(Bids.valueOf(choice));
                } catch (Exception e) {
                    e.getMessage();
                }
                System.out.println("You have chosen the bid : " + String.valueOf(myPlayer.getBidChosen()));
            }
            else {
                player.setBidChosen(Bids.Pass); //Other players passes
            }
        });

        changeGameState(GameState.BID_CHOSEN);
    }


    /**
     * Constitute player Ecart
     * @since v0.6
     */
    private void constituteEcart() {
        changeGameState(GameState.ECART_CONSTITUTING);
        System.out.println("Showing the chien to all...");
        flipDeck(chien, true);
        System.out.println(chien.cardListToString());
        System.out.println("Placing chien's cards into taker's deck...");

        while ( !chien.isEmpty() ) {
            moveCardBetweenDecks(chien, myPlayer, chien.get(0));
        }

        System.out.println("Now, constitute your ecart by putting 6 of your deck's cards in the chien :");
        System.out.println(myPlayer.cardListToString());

        for (int i=0; i < 6; i++) {
            Scanner sc = new Scanner(System.in);
            boolean choiceValid = false;
            Card c;
            String choice;
            do {
                choice = sc.nextLine();
                c = myPlayer.getInCardsList(choice);

                if ( myPlayer.findInCardsList(choice) ) {
                    if ( c.getSuit() != Suit.Trump && c.getSuit() != Suit.Excuse && c.getRank() != Rank.King) {
                        choiceValid = true;
                    }
                    else if ( c.getSuit() == Suit.Trump && !Objects.equals(c.getName(), "Trump1")
                            && !Objects.equals(c.getName(), "Trump21") && checkTrumpPossibility() ) {
                        choiceValid = true;
                    }
                    else {
                        choiceValid = false;
                        System.out.println("You can't choose a Trump, a King or Excuse");
                    }
                }
                else
                    System.out.println("Your choice isn't valid, try again");
            }
            while (!choiceValid);

            //Only Trumps are shown when put in Ecart
            if ( c.getSuit() != Suit.Trump) {
                flipCard(c, false);
            }
            moveCardBetweenDecks(myPlayer, chien, c);
            System.out.println("Taker : " + myPlayer.cardListToString());
            System.out.println("Talon : " + chien.cardListToString());
        }
        System.out.println("Ecart done...");
        sortDeck(myPlayer);
    }


    /**
     * Properly quit the game by removing all cards
     * and notifying observer of the removal
     * @since v1.0.0
     */
    void quitGame() {
        gameState = GameState.GAME_ENDED;
        System.out.println("\nQuitting...");

        flipDeck(myPlayer, false);
        gatherAllCards();
        while ( !wholeCardsDeck.isEmpty() ) {
            wholeCardsDeck.remove(0);
        }
        Talon.resetClass();
        Hand.resetClass();
        Card.resetClass();
    }


    //SUPPLY METHODS


    /**
     * Change current game state and notify observers
     * @since v1.0.0
     * @param gameState the new game state
     */
    private void changeGameState(GameState gameState) {
        this.gameState = gameState;
    }


    /**
     * Shuffles cards
     * @since v0.5
     * @see Collections
     * @see Random
     */
    private void shuffleCards() {
        changeGameState(GameState.CARDS_SHUFFLING);
        System.out.println("Shuffling cards...");
        long seed = System.nanoTime();
        Collections.shuffle(wholeCardsDeck, new Random(seed));
    }


    /**
     * Sorts a deck of cards
     * @since v1.0.0
     * @see Card.CardComparator
     * @param cardGroup the cardGroup that has to be sorted
     */
    private void sortDeck(CardGroup cardGroup) {
        cardGroup.sort(new Card.CardComparator());
    }


    /**
     * Cuts cards
     * @since v0.5
     */
    private void cutDeck() {
        System.out.println("Cutting cards...");
        //a cut list must contain more than 3 cards
        int splitIterator;
        boolean isValidIterator = false;

        do {
            splitIterator = wholeCardsDeck.indexOf(randomCard(wholeCardsDeck));
            if ( splitIterator < 74 && splitIterator > 3)
                isValidIterator = true;
        }
        while (!isValidIterator);

        List<Card> cut1 = new ArrayList<>();
        List<Card> cut2 = new ArrayList<>();

        for (int i=0; i <= splitIterator; i++)
            cut1.add(wholeCardsDeck.get(i));
        for (int i = splitIterator+1; i < wholeCardsDeck.size(); i++)
            cut2.add(wholeCardsDeck.get(i));

        wholeCardsDeck.clear();
        wholeCardsDeck.addAll(cut2);
        wholeCardsDeck.addAll(cut1);
    }

    /**
     * Spread the deck of cards on the the carpet
     * @since v1.0.0
     */
    private void spreadCards() {
        changeGameState(GameState.CARDS_SPREADING);
        while ( !wholeCardsDeck.isEmpty()) {
            moveCardBetweenDecks(wholeCardsDeck, toPickDeck, wholeCardsDeck.get(0));
        }
    }


    /**
     * Moves a card between two decks
     * @since v0.6
     * @param source the source deck
     * @param target the target deck
     * @param c the card to move
     */
    private void moveCardBetweenDecks(CardGroup source, CardGroup target, Card c) {
        source.remove(c);
        target.add(c);
    }

    /**
     * Checks if it is allowed to discard a Trump
     * It is only possible if there isn't other cards
     * than Trump, King or Excuse is player's deck
     * @since v0.6
     *
     * @return a boolean indicating if Trump discard is possible
     */
    private boolean checkTrumpPossibility() {
        int cpt = 0;
        for ( Card c : myPlayer) {
            if ( c.getSuit() == Suit.Trump || c.getSuit() == Suit.Excuse || c.getRank() == Rank.King)
                cpt++;
        }
        return cpt == myPlayer.size();
    }


    /**
     * Gets a random card in a card list
     * @since v0.5
     * @see Random
     * @param list the list used to return a random card
     *
     * @return a random card
     */
    private Card randomCard(List<Card> list) {
        int index = new Random().nextInt(list.size());
        return list.get(index);
    }


    /**
     * Flip a card and call function which will notify observer
     * of the flipping in model
     *
     * Final state of the card is indicated instead of just flipping
     * card (no matter the initial showing state)
     * to enhance code reading and see quickly if function deserved
     * to be called at a certain location in the code
     *
     * @since v0.8.2
     * @see CardUpdate
     * @param c the card to flip
     * @param isShown the state that is passed to the card (if different)
     */
    private void flipCard(Card c, boolean isShown) {
        c.setShown(isShown);
    }


    /**
     * Flip the whole deck of cards and call function which
     * will notify observer of the flipping in model
     *
     * Final state of the card is indicated instead of just flipping
     * card (no matter the initial showing state)
     * to enhance code reading and see quickly if function deserved
     * to be called at a certain location in the code
     *
     * @since v0.8.2
     * @see CardUpdate
     * @param cardGroup the cardGroup containing the cards to flip
     * @param state the state that is passed to the card (if different)
     */
    private void flipDeck(CardGroup cardGroup, boolean state) {
        cardGroup.forEach(c -> c.setShown(state));
    }


    /**
     * Displays card repartition after distribution
     * @since v0.5
     */
    @Override
    public String toString() {

        StringBuilder result = new StringBuilder("\n=== CARD REPARTITION ===\n\n");

        for (Map.Entry<PlayerHandler.PlayersCardinalPoint, Hand> player
                : playerHandler.getPlayersMap().entrySet()) {
            result.append("Player ").append(playerHandler.getPlayerName(player.getValue())).append(" : ");
            for ( Card c : player.getValue()) {
                result.append(c.getName()).append("; ");
            }
            result.append("\n");
        }
        result.append("Talon : ");
        for ( Card c : chien) {
            result.append(c.getName()).append("; ");
        }

        return result.toString();
    }
}
