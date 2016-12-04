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

package app.model;

import exceptions.CardGroupNumberException;
import exceptions.CardNumberException;
import exceptions.CardUniquenessException;

import java.util.*;
import static java.lang.Thread.sleep;

/**
 * The {@code GameModel} class consists in the MVC architecture model
 * It handles Tarot dealing and bids
 * @author Arthur
 * @version v0.9
 * @since v0.2
 *
 * @see Observable
 * @see Card
 * @see Hand
 * @see PlayerHandler
 * @see Talon
 */
//TODO : Change the most part of @pauseModelFor with @waitEndUpdateAnimation
public class GameModel extends Observable {

    private CardGroup wholeCardsDeck;
    private CardGroup toPickDeck;
    private CardGroup pickedCardsDeck;
    private Map<Card, Hand> pickedCardsMap;
    private PlayerHandler playerHandler;
    private Talon talon;
    private Hand ourPlayer;
    private int userChoice;
    private int lastEndedAnimation;

    /**
     * Constructs app model by creating players, chien and cards
     * @since v0.5
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    public GameModel() throws CardGroupNumberException {
        wholeCardsDeck = new CardGroup(78);
        toPickDeck = new CardGroup(78);
        pickedCardsDeck = new CardGroup(4);
        pickedCardsMap = new HashMap<>();

        //Players creation
        playerHandler = new PlayerHandler();
        ourPlayer = playerHandler.getPlayer(PlayerHandler.PlayersCardinalPoint.South);

        //Chien creation
        try {
            talon = new Talon();
        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
        }

        userChoice = -1;
    }


    /**
     * Creates all cards and puts them in wholeCardsDeck
     * @since v0.7
     */
    public void createCards() {
        for (Suit s : Suit.values()) {
            if ( s != Suit.Trump && s != Suit.Excuse) {
                for (Rank r : Rank.values()) {
                    try {
                        Card c = new Card(s,r);
                        if(!wholeCardsDeck.add(c))
                            throw new CardNumberException("Card number limit has been reached.",
                                    wholeCardsDeck.getNbMaxCards());
                        else
                            notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.ADD_CARD, c, wholeCardsDeck));

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
                        else
                            notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.ADD_CARD, c, wholeCardsDeck));
                    } catch (CardUniquenessException | CardNumberException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            else {
                try {
                    Card c = new Card(Suit.Excuse, -1);
                    if(!wholeCardsDeck.add(c))
                        throw new CardNumberException("Card number limit has been reached.",
                                wholeCardsDeck.getNbMaxCards());
                    notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.ADD_CARD, c, wholeCardsDeck));
                } catch (CardNumberException | CardUniquenessException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }


    /**
     * Choose this game first dealer
     * by picking a card among the 78 ones
     * The hand that has picked the littlest/weakest card
     * becomes the dealer
     * @since v0.5
     */
    public void chooseInitialDealer() {
        System.out.println("\n=== DEALER CHOOSING ===\n");

        //shuffle wholeCardsDeck and spread the cards on the table
        shuffleCards();

        while ( !wholeCardsDeck.isEmpty() ) {
            toPickDeck.add(wholeCardsDeck.get(0));
            wholeCardsDeck.remove(wholeCardsDeck.get(0));
        }
        pauseModelFor(1500);
        notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.SPREAD_CARDS, toPickDeck));
        pauseModelFor(500);

        //Handle dealer choosing
        playerHandler.getPlayersMap().forEach((cardinalPoint,player)-> {
            Card c;
            if ( player == ourPlayer) {

                System.out.println("Choose your card by clicking on it");
                int choice = waitObserverUserEvent(ViewActionExpected.PICK_CARD);
                c = toPickDeck.get(choice);
            }
            else { //choose a random card for other players
                do {
                    c = randomCard(toPickDeck);
                }
                while (c.getSuit() == Suit.Excuse);
            }

            moveCardBetweenDecks(toPickDeck, pickedCardsDeck, c);
            pickedCardsMap.put(c, player);
        });

        //Flip cards for players to see which is the lowest
        pickedCardsDeck.forEach( (card) -> flipCard(card, true));
        pauseModelFor(3000);

        //Set dealer from picking
        Card minCard = null;
        for (Map.Entry<Card, Hand> mapEntry : pickedCardsMap.entrySet())
            if (Objects.isNull(minCard))
                minCard = mapEntry.getKey();
            else if ( new Card.CardComparator().compare(mapEntry.getKey(), minCard) == -1 )
                minCard = mapEntry.getKey();

        playerHandler.setFirstDealer(pickedCardsMap.get(minCard));
        System.out.println("Dealer is " + playerHandler.getPlayerName(getPlayerHandler().getDealer()));

        //Flip cards again and put them back to wholeCardsDeck
        while ( !pickedCardsDeck.isEmpty()) {
            flipCard(pickedCardsDeck.get(0), false);
            pauseModelFor(1500);
            moveCardBetweenDecks(pickedCardsDeck, wholeCardsDeck, pickedCardsDeck.get(0));
        }
        while ( !toPickDeck.isEmpty() )
            moveCardBetweenDecks(toPickDeck, wholeCardsDeck, toPickDeck.get(0));
    }


    /**
     * Do Tarot Dealing by shuffling, cutting and dealing cards
     * If a player has Petit Sec after dealing,
     * new dealer, shuffler and cutter are designated
     * and card deck is again shuffled, cut and dealt
     * @since v0.5
     */
    public void handleDealing() {
        boolean hasPetitSec = false;
        do {
            System.out.println("Shuffling cards...");
            shuffleCards();
            System.out.println("Cutting cards...");
            cutDeck();
            System.out.println("Dealing cards...");
            dealAllCards();

            flipDeck(ourPlayer, true);
            pauseModelFor(3000);

            System.out.println("Sorting cards...");
            playerHandler.getPlayersMap().forEach( (cardinalPoint, playerHand) -> sortDeck(playerHand));

            pauseModelFor(3000);

            //"Petit Sec" checking
            System.out.println("Petit Sec checking...");
            for (Map.Entry<PlayerHandler.PlayersCardinalPoint, Hand> player
                    : playerHandler.getPlayersMap().entrySet()) {

                hasPetitSec = player.getValue().checkHasPetitSec();
                if (hasPetitSec) {
                    System.out.println("The player has Petit Sec, re-dealing...");
                    flipDeck(ourPlayer, false);
                    pauseModelFor(1500);
                    playerHandler.changeDealer();
                    gatherAllCards();
                    break;
                }
            }
        }
        while (hasPetitSec);
        System.out.println("No Petit Sec found.");
    }

    /**
     * Deals card
     * @since v0.5
     */
    public void dealAllCards() {
        int cptNbCardGivenToSameHand = 0;
        while( !wholeCardsDeck.isEmpty()) {
            boolean chienReceiveCard = false;
            if( talon.size() < 6
                    && wholeCardsDeck.size() < 78) { //Don't give first card to chien
                if ( wholeCardsDeck.size() == 2 ) { //Don't give last card to chien
                    moveCardBetweenDecks(wholeCardsDeck, talon, wholeCardsDeck.get(0));
                    chienReceiveCard = true;
                }
                else {
                    chienReceiveCard = ( (new Random().nextInt(4) == 0)); //25% it chooses to put it in Talon
                    if (chienReceiveCard) {
                        moveCardBetweenDecks(wholeCardsDeck, talon, wholeCardsDeck.get(0));
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
     * Retrieve all cards from players and talon
     * to the initial deck
     * @since v0.6
     */
    public void gatherAllCards() {
        playerHandler.getPlayersMap().forEach((cardinalPoint,player)-> {
            while ( !player.isEmpty() ) {
                moveCardBetweenDecks(player, wholeCardsDeck, player.get(0));
            }
        });

        while ( !talon.isEmpty() ) {
            moveCardBetweenDecks(talon, wholeCardsDeck, talon.get(0));
        }
    }


    /**
     * Handle the Bids
     * @since v0.6
     */
    public void handleBids() {
        chooseBids();
        while (ourPlayer.getBidChosen() == Bids.Pass) {
            System.out.println("You've chosen to pass. Re-dealing...");
            flipDeck(ourPlayer, false);
            pauseModelFor(1500);
            gatherAllCards();
            pauseModelFor(500);
            playerHandler.changeDealer();
            handleDealing();
            chooseBids();
        }
        System.out.println("You are the taker");
        if ( ourPlayer.getBidChosen()== Bids.Small || ourPlayer.getBidChosen()== Bids.Guard ) {
            System.out.println("You're allowed to constitute your ecart");
            pauseModelFor(1500);
            constituteEcart();
        }
    }


    /**
     * Choose a bid for each player
     * @since v0.6
     */
    private void chooseBids() {

        System.out.println("\n=== BIDS ===\n");

        playerHandler.getPlayersMap().forEach((cardinalPoint,player) -> {
            if ( player == ourPlayer) {
                System.out.println("Here are your cards :");
                flipDeck(ourPlayer, true);
                System.out.println(ourPlayer.cardListToString());

                System.out.println("Choose your bid among those one :");
                System.out.println("1. Small");
                System.out.println("2. Guard");
                System.out.println("3. GuardWithoutTheKitty");
                System.out.println("4. GuardAgainstTheKitty");
                System.out.println("5. Pass");

                int choice = waitObserverUserEvent(ViewActionExpected.CHOOSE_BID);
                try {
                    ourPlayer.setBidChosen(Bids.valueOf(choice));
                    System.out.println("You have chosen the bid : " +
                            String.valueOf(ourPlayer.getBidChosen()));
                } catch (Exception e) {
                    //TODO: HANDLE IT WITH ERROR CODE TO OBSERVERS FOR THEM TO DISPLAY IT
                    System.err.println(e.getMessage());
                    ourPlayer.setBidChosen(Bids.Pass);
                }
            }
            else {
                player.setBidChosen(Bids.Pass); //Other players passes
            }
        });
    }


    /**
     * Constitute player Ecart
     * @since v0.6
     */
    private void constituteEcart() {
        System.out.println("Showing the talon to all...");
        flipDeck(talon, true);
        pauseModelFor(2000);
        System.out.println(talon.cardListToString());

        System.out.println("Placing talon's cards into taker's deck...");
        while ( !talon.isEmpty() ) {
            moveCardBetweenDecks(talon, ourPlayer, talon.get(0));
        }

        System.out.println("Now, constitute your ecart by putting 6 of your deck's cards in the talon :");
        System.out.println(ourPlayer.cardListToString());

        for (int i=0; i < 6; i++) {
            boolean choiceValid;
            Card c;
            do {
                int choice = waitObserverUserEvent(ViewActionExpected.CHOOSE_ECART_CARD);

                c = ourPlayer.get(choice);

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
            while (!choiceValid);

            //Only Trumps are shown when put in Ecart
            if ( c.getSuit() != Suit.Trump) {
                flipCard(c, false);
                pauseModelFor(1500);
            }
            moveCardBetweenDecks(ourPlayer, talon, c);
            System.out.println("Taker : " + ourPlayer.cardListToString());
            System.out.println("Talon : " + talon.cardListToString());
        }
        System.out.println("Ecart done...");
        System.out.println("Sorting player South...");
        sortDeck(ourPlayer);
    }


    /**
     * Displays card repartition after distribution
     * @since v0.5
     */
    @Override
    public String toString() {

        String result = "\n=== CARD REPARTITION ===\n";

        for (Map.Entry<PlayerHandler.PlayersCardinalPoint, Hand> player
                : playerHandler.getPlayersMap().entrySet()) {
            result += playerHandler.getPlayerName(player.getValue()) + " : ";
            for ( Card c : player.getValue()) {
                result += c.getName() + "; ";
            }
            result +=  "\n";
        }
        result += "Talon : ";
        for ( Card c : talon) {
            result += c.getName() + "; ";
        }
        return result;
    }


    //SUPPLY METHODS


    /**
     * Shuffles cards
     * @since v0.5
     * @see Collections
     * @see Random
     */
    public void shuffleCards() {
        long seed = System.nanoTime();
        Collections.shuffle(wholeCardsDeck, new Random(seed));
        notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.SHUFFLE_CARDS, wholeCardsDeck));
    }


    /**
     * Sorts a deck of cards
     * @since v0.8.2
     * @see Card.CardComparator
     * @param cardGroup the cardGroup that has to be sorted
     */
    private void sortDeck(CardGroup cardGroup) {
        cardGroup.sort(new Card.CardComparator());
        notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.SORT_DECK, cardGroup));
    }


    /**
     * Cuts a deck of cards
     * @since v0.5
     */
    public void cutDeck() {
        //a cut list must contain more than 3 cards
        int splitIt;
        boolean isValidIterator = false;

        do {
            splitIt = wholeCardsDeck.indexOf(randomCard(wholeCardsDeck));
            if ( splitIt < 74 && splitIt > 3)
                isValidIterator = true;
        }
        while (!isValidIterator);

        List<Card> cut1 = new ArrayList<>();
        List<Card> cut2 = new ArrayList<>();

        for (int i=0; i <= splitIt; i++)
            cut1.add(wholeCardsDeck.get(i));
        for (int i = splitIt+1; i < wholeCardsDeck.size(); i++)
            cut2.add(wholeCardsDeck.get(i));

        wholeCardsDeck.clear();
        wholeCardsDeck.addAll(cut2);
        wholeCardsDeck.addAll(cut1);

        pauseModelFor(2000);
        CardUpdate cardUpdate = new CardUpdate(ActionPerformedOnCard.CUT_DECK, wholeCardsDeck.get(splitIt), wholeCardsDeck);
        notifyObserversOfCardUpdate(cardUpdate);
        waitEndUpdateAnimation(cardUpdate);
    }


    /**
     * Moves a card between two decks
     * @since v0.6
     * @param source the source deck
     * @param target the target deck
     * @param c the card to move
     */
    public void moveCardBetweenDecks(CardGroup source, CardGroup target, Card c) {
        source.remove(c);
        target.add(c);
        notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, c, target));
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
        for ( Card c : ourPlayer ) {
            if ( c.getSuit() == Suit.Trump || c.getSuit() == Suit.Excuse || c.getRank() == Rank.King)
                cpt++;
        }
        return cpt == ourPlayer.size();
    }


    /**
     * Gets a random card in a card list
     * @since v0.5
     * @see Random
     * @param list the list used to return a random card
     *
     * @return a random card
     */
    public Card randomCard(List<Card> list) {
        int index = new Random().nextInt(list.size());
        return list.get(index);
    }


    /**
     * Interrupts model logic for a certain amount of milliseconds
     * @since v0.8.1
     * @see Random
     * @param millis the amount of milliseconds to sleep
     */
    private void pauseModelFor(long millis) {
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
     * @param state the state that is passed to the card (if different)
     */
    private void flipCard(Card c, boolean state) {
        c.setShown(state);
        notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.FLIP_CARD, c));
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
        notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.FLIP_CARD, cardGroup));
    }


    /**
     * Notifies model observers with the type of card update
     * it shall operate to update itself
     * @since v0.6
     * @see Observable
     */
    public void notifyObserversOfCardUpdate(CardUpdate cardUpdate) {
        if ( countObservers() != 0) {
            setChanged();
            notifyObservers(cardUpdate);
            if (cardUpdate.getType() != ActionPerformedOnCard.ADD_CARD) {
                pauseModelFor(200);
            }
        }
    }


    /**
     * Notifies observer that model needs an action from user
     * to resume its logic.
     * Then, loop until user event.
     * The expected action can be a card or a bid selection.
     *
     * If gameModel hasn't any observer, default values will be set
     * following action type
     *
     * @since v0.8.2
     * @see ViewActionExpected
     * @param action the expected action from view
     */
    private synchronized int waitObserverUserEvent(ViewActionExpected action) {
        int choice = -1;
        if ( countObservers() != 0 ) {
            setChanged();
            notifyObservers(action);
            while (userChoice == -1)
            {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println(e.toString());
                }
            }
            choice = userChoice;
            userChoice = -1;
        }
        else { //if no observers, set default values
            if ( action == ViewActionExpected.PICK_CARD) {
                choice = new Random().nextInt(78);
            }
            else if ( action == ViewActionExpected.CHOOSE_BID) {
                choice = 1 + (new Random().nextInt(5));
            }
            else if ( action == ViewActionExpected.CHOOSE_ECART_CARD) {
                choice = new Random().nextInt(playerHandler.
                        getPlayer(PlayerHandler.PlayersCardinalPoint.South).size() );
            }
        }
        return choice;
    }

    /**
     * Wait until the animation of a specified updateCard object
     * has been finished.
     * @since v0.9.1
     * @param cardUpdate the specified cardUpdate object
     */
    private synchronized void waitEndUpdateAnimation(CardUpdate cardUpdate)
    {
        while (lastEndedAnimation != cardUpdate.hashCode())
        {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e.toString());
            }
        }
        lastEndedAnimation = -1;
    }

    //GETTERS & SETTERS - no documentation needed

    public CardGroup getWholeCardsDeck() {
        return wholeCardsDeck;
    }
    public CardGroup getToPickDeck() {
        return toPickDeck;
    }
    public CardGroup getPickedCardsDeck() {
        return pickedCardsDeck;
    }
    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
    public Talon getTalon() {
        return talon;
    }

    public synchronized void setUserChoice(int userChoice) {
        this.userChoice = userChoice;
        notifyAll();
    }

    public synchronized void setLastEndedAnimation(int lastEndedAnimation)
    {
        this.lastEndedAnimation = lastEndedAnimation;
        notifyAll();
    }
}