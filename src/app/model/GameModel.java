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
 * The {@code GameModel} class consists in the MVP architecture model
 * It handles Tarot dealer choosing,
 * dealing, bids and ecart constitution
 * @author Arthur
 * @version v0.11
 * @since v0.2
 *
 * @see Observable
 * @see Card
 * @see Hand
 * @see PlayerHandler
 * @see Talon
 */

public class GameModel extends Observable {

    private CardGroup wholeCardsDeck;
    private CardGroup toPickDeck;
    private CardGroup pickedCardsDeck;
    private Map<Card, Hand> pickedCardsMap;
    private PlayerHandler playerHandler;
    private Talon talon;
    private Hand ourPlayer;
    private NotificationType awaitsUserEvent;
    private Thread gameThread;
    private GameState gameState;
    private int userChoice;
    private int lastEndedAnimation;

    /**
     * Constructs app model by creating players, chien and cards
     * @since v0.5
     *
     * @throws CardGroupNumberException if user tries to create too much hands
     * @param dealerChoosingEnabled the mode chosen to run the game
     */
    public GameModel(boolean dealerChoosingEnabled) throws CardGroupNumberException {

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

        gameThread = new Thread( () -> {
            if (gameState != GameState.GAME_ENDED && dealerChoosingEnabled)
                chooseInitialDealer();
            if (gameState != GameState.GAME_ENDED)
                handleDealing();
            if (gameState != GameState.GAME_ENDED)
                handleBids();
            gameState = GameState.GAME_ENDED;
        });
        gameThread.setDaemon(true);

        userChoice = -1;
        awaitsUserEvent = null;
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
                            notifyObserversOfCardUpdate(new CardUpdate(CardUpdateType.ADD_CARD, c, wholeCardsDeck));

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
                            notifyObserversOfCardUpdate(new CardUpdate(CardUpdateType.ADD_CARD, c, wholeCardsDeck));
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
                    notifyObserversOfCardUpdate(new CardUpdate(CardUpdateType.ADD_CARD, c, wholeCardsDeck));
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
    private void chooseInitialDealer() {
        changeGameState(GameState.DEALER_CHOOSING);

        changeGameState(GameState.CARDS_SHUFFLING);
        shuffleCards();

        temporize(1500);

        changeGameState(GameState.CARDS_SPREADING);
        spreadCards();

        //Handle dealer choosing
        changeGameState(GameState.DEALER_CHOOSING);
        playerHandler.getPlayersMap().forEach((cardinalPoint,player)-> {
            Card c;
            if ( player == ourPlayer) {
                int choice = waitObserverUserEvent(NotificationType.PICK_CARD);
                c = toPickDeck.get(choice);
            }
            else { //choose a random card for other players
                do {
                    c = randomCard(toPickDeck);
                }
                while (c.getSuit() == Suit.Excuse);
            }

            moveCardBetweenDecks(toPickDeck, pickedCardsDeck, c, true);
            pickedCardsMap.put(c, player);
        });

        //Flip cards for players to see which is the lowest
        flipDeck(pickedCardsDeck, true);

        //Set dealer from picking
        Card minCard = null;
        for (Map.Entry<Card, Hand> mapEntry : pickedCardsMap.entrySet())
            if (Objects.isNull(minCard))
                minCard = mapEntry.getKey();
            else if ( new Card.CardComparator().compare(mapEntry.getKey(), minCard) == -1 )
                minCard = mapEntry.getKey();

        playerHandler.setFirstDealer(pickedCardsMap.get(minCard));

        changeGameState(GameState.DEALER_CHOSEN);
        temporize(1000);

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
    private void handleDealing() {
        boolean hasPetitSec = false;
        do {
            changeGameState(GameState.CARDS_SHUFFLING);
            shuffleCards();
            changeGameState(GameState.CARDS_CUTTING);
            cutDeck();
            changeGameState(GameState.CARDS_DEALING);
            dealAllCards();

            flipDeck(ourPlayer, true);

            playerHandler.getPlayersMap().forEach( (cardinalPoint, playerHand) -> sortDeck(playerHand));

            temporize(3000);

            //"Petit Sec" checking
            for (Map.Entry<PlayerHandler.PlayersCardinalPoint, Hand> player
                    : playerHandler.getPlayersMap().entrySet()) {

                hasPetitSec = player.getValue().checkHasPetitSec();
                if (hasPetitSec) {
                    changeGameState(GameState.PETIT_SEC_DETECTED);
                    flipDeck(ourPlayer, false);
                    playerHandler.changeDealer();
                    gatherAllCards();
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
    public void dealAllCards() {
        int cptNbCardGivenToSameHand = 0;
        while( !wholeCardsDeck.isEmpty()) {
            boolean chienReceiveCard = false;
            if( talon.size() < 6
                    && wholeCardsDeck.size() < 78) { //Don't give first card to chien
                if ( wholeCardsDeck.size() == 2 ) { //Don't give last card to chien
                    moveCardBetweenDecks(wholeCardsDeck, talon, wholeCardsDeck.get(0), true);
                    chienReceiveCard = true;
                }
                else {
                    chienReceiveCard = ( (new Random().nextInt(4) == 0)); //25% it chooses to put it in Talon
                    if (chienReceiveCard) {
                        moveCardBetweenDecks(wholeCardsDeck, talon, wholeCardsDeck.get(0), true);
                    }
                }
            }
            if (!chienReceiveCard) {
                moveCardBetweenDecks(wholeCardsDeck, playerHandler.getCurrentPlayer(), wholeCardsDeck.get(0), true);
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
                moveCardBetweenDecks(player, wholeCardsDeck, player.get(0), false);
            }
        });
        while ( !talon.isEmpty() ) {
            moveCardBetweenDecks(talon, wholeCardsDeck, talon.get(0), false);
        }
        while ( !pickedCardsDeck.isEmpty() ) {
            moveCardBetweenDecks(pickedCardsDeck, wholeCardsDeck, pickedCardsDeck.get(0), false);
        }
        while ( !toPickDeck.isEmpty() ) {
            moveCardBetweenDecks(toPickDeck, wholeCardsDeck, toPickDeck.get(0), false);
        }
        CardUpdate cardUpdate = new CardUpdate(CardUpdateType.GATHER_CARDS, wholeCardsDeck);
        notifyObserversOfCardUpdate(cardUpdate);
        waitEndUpdateAnimation(cardUpdate);
    }


    /**
     * Handle the Bids
     * @since v0.6
     */
    private void handleBids() {
        chooseBids();
        while (ourPlayer.getBidChosen() == Bids.Pass) {
            flipDeck(ourPlayer, false);
            gatherAllCards();
            playerHandler.changeDealer();
            handleDealing();
            chooseBids();
        }
        if ( ourPlayer.getBidChosen()== Bids.Small || ourPlayer.getBidChosen()== Bids.Guard ) {
            temporize(800);
            constituteEcart();
            changeGameState(GameState.ECART_CONSTITUTED);
        }
    }


    /**
     * Choose a bid for each player
     * @since v0.6
     */
    private void chooseBids() {

        changeGameState(GameState.BID_CHOOSING);

        playerHandler.getPlayersMap().forEach((cardinalPoint,player) -> {
            if ( player == ourPlayer) {
                flipDeck(ourPlayer, true);
                int choice = waitObserverUserEvent(NotificationType.CHOOSE_BID);

                try {
                    ourPlayer.setBidChosen(Bids.valueOf(choice));
                } catch (Exception e) {
                    e.getMessage();
                }
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
        flipDeck(talon, true);

        while ( !talon.isEmpty() ) {
            moveCardBetweenDecks(talon, ourPlayer, talon.get(0), true);
        }

        sortDeck(ourPlayer);

        //TODO : DRAG & DROP VERSION
        for (int i=0; i < 6; i++) {
            boolean choiceValid;
            Card c;
            do {
                int choice = waitObserverUserEvent(NotificationType.CHOOSE_ECART_CARD);

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
                    setChanged();
                    notifyObservers(NotificationType.UNAUTHORIZED_CARD_CHOICE);
                }
            }
            while (!choiceValid);

            //Only Trumps are shown when put in Ecart
            if ( c.getSuit() != Suit.Trump) {
                flipCard(c, false);
            }
            moveCardBetweenDecks(ourPlayer, talon, c, true);
            CardUpdate cardUpdate = new CardUpdate(CardUpdateType.SORT_DECK, ourPlayer);
            notifyObserversOfCardUpdate(cardUpdate);
            waitEndUpdateAnimation(cardUpdate);
        }
    }


    /**
     * Properly quit the game by removing all cards
     * and notifying observer of the removal
     * @since v0.10
     */
    public void quitGame() {
        gameState = GameState.GAME_ENDED;

        flipDeck(ourPlayer, false);
        gatherAllCards();
        while ( !wholeCardsDeck.isEmpty() ) {
            CardUpdate deleteUpdate = new CardUpdate(CardUpdateType.DELETE_CARD, wholeCardsDeck.get(0));
            notifyObserversOfCardUpdate(deleteUpdate);
            waitEndUpdateAnimation(deleteUpdate);
            wholeCardsDeck.remove(0);
        }
        Talon.resetClassForTesting();
        Hand.resetClassForTesting();
        Card.resetClassForTesting();
    }


    //SUPPLY METHODS


    /**
     * Change current game state and notify observers
     * @since v0.10
     * @param gameState the new game state
     */
    private void changeGameState(GameState gameState) {
        this.gameState = gameState;
        if ( countObservers() != 0 ) {
            setChanged();
            notifyObservers(gameState);
        }
    }


    /**
     * Shuffles cards
     * @since v0.5
     * @see Collections
     * @see Random
     */
    public void shuffleCards() {
        long seed = System.nanoTime();
        Collections.shuffle(wholeCardsDeck, new Random(seed));
        CardUpdate cardUpdate = new CardUpdate(CardUpdateType.SHUFFLE_CARDS, wholeCardsDeck);
        notifyObserversOfCardUpdate(cardUpdate);
        waitEndUpdateAnimation(cardUpdate);
    }


    /**
     * Sorts a deck of cards
     * @since v0.8.2
     * @see Card.CardComparator
     * @param cardGroup the cardGroup that has to be sorted
     */
    private void sortDeck(CardGroup cardGroup) {
        cardGroup.sort(new Card.CardComparator());
        notifyObserversOfCardUpdate(new CardUpdate(CardUpdateType.SORT_DECK, cardGroup));
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

        temporize(1000);
        CardUpdate cutUpdate = new CardUpdate(CardUpdateType.CUT_DECK, wholeCardsDeck.get(splitIt), wholeCardsDeck);
        notifyObserversOfCardUpdate(cutUpdate);
        waitEndUpdateAnimation(cutUpdate);
    }


    /**
     * Spread the deck of cards on the the carpet
     * @since v0.5
     */
    private void spreadCards() {
        while ( !wholeCardsDeck.isEmpty()) {
            moveCardBetweenDecks(wholeCardsDeck, toPickDeck, wholeCardsDeck.get(0), false);
        }

        CardUpdate cardUpdate = new CardUpdate(CardUpdateType.SPREAD_CARDS, toPickDeck);
        notifyObserversOfCardUpdate(cardUpdate);
        waitEndUpdateAnimation(cardUpdate);
    }


    /**
     * Moves a card between two decks
     * @since v0.6
     * @param source the source deck
     * @param target the target deck
     * @param c the card to move
     * @param doesNotifyObserver allows to enable/disable move animation
     */
    public void moveCardBetweenDecks(CardGroup source, CardGroup target, Card c, boolean doesNotifyObserver) {
        source.remove(c);
        target.add(c);
        if (doesNotifyObserver)
        {
            notifyObserversOfCardUpdate(new CardUpdate(CardUpdateType.MOVE_CARD_BETWEEN_GROUPS, c, target));
        }
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
     * This allows game to run cooler
     * @since v0.8.1
     * @see Random
     * @param millis the amount of milliseconds to sleep
     */
    private void temporize(long millis) {
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
        CardUpdate flipCardUpdate = new CardUpdate(CardUpdateType.FLIP_CARD, c);
        notifyObserversOfCardUpdate(flipCardUpdate);
        if ( countObservers() != 0 )
            waitEndUpdateAnimation(flipCardUpdate);
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
        CardUpdate flipDeckUpdate = new CardUpdate(CardUpdateType.FLIP_CARD, cardGroup);
        notifyObserversOfCardUpdate(flipDeckUpdate);
        waitEndUpdateAnimation(flipDeckUpdate);
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
            if (cardUpdate.getType() != CardUpdateType.ADD_CARD) {
                temporize(200);
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
     * @see NotificationType
     * @param action the expected action from view
     */
    private synchronized int waitObserverUserEvent(NotificationType action) {
        awaitsUserEvent = action;
        int choice = -1;
        if ( countObservers() != 0 ) {
            setChanged();
            notifyObservers(action);
            while (userChoice == -1)
            {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }
            choice = userChoice;
            userChoice = -1;
        }
        else { //if no observers, set default values
            if ( action == NotificationType.PICK_CARD) {
                choice = new Random().nextInt(78);
            }
            else if ( action == NotificationType.CHOOSE_BID) {
                choice = 1 + (new Random().nextInt(5));
            }
            else if ( action == NotificationType.CHOOSE_ECART_CARD) {
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
    private synchronized void waitEndUpdateAnimation(CardUpdate cardUpdate) {
        if ( countObservers() != 0 ) {
            while (lastEndedAnimation != cardUpdate.hashCode()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }
            lastEndedAnimation = -1;
        }
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
    public Hand getOurPlayer() {
        return ourPlayer;
    }
    public NotificationType getAwaitsUserEvent() {
        return awaitsUserEvent;
    }
    public Thread getGameThread() {
        return gameThread;
    }
    public GameState getGameState() {
        return gameState;
    }

    public synchronized void setUserChoice(int userChoice) {
        this.userChoice = userChoice;
        notify();
    }
    public void setAwaitsUserEvent(NotificationType awaitsUserEvent) {
        this.awaitsUserEvent = awaitsUserEvent;
    }
    public synchronized void setLastEndedAnimation(int lastEndedAnimation) {
        this.lastEndedAnimation = lastEndedAnimation;
        notify();
    }
}