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

package app.model;

import exceptions.CardGroupNumberException;
import exceptions.CardNumberException;
import exceptions.CardUniquenessException;

import java.util.*;

import static app.model.PlayerHandler.PlayersCardinalPoint.South;
import static java.lang.Thread.sleep;

/**
 * The {@code GameModel} class consists in the MVC architecture model
 * It handles Tarot dealing and bids
 * @author Arthur
 * @version v0.8.1
 * @since v0.2
 *
 * @see Observable
 * @see Card
 * @see Hand
 * @see PlayerHandler
 * @see Talon
 */
public class GameModel extends Observable {

    private CardGroup initialDeck;
    private Map<Card, Hand> pickedCardsMap;
    private PlayerHandler playerHandler;
    private Talon talon;
    private Hand ourPlayer;
    private int userChoice;

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
        initialDeck = new CardGroup(78);
        pickedCardsMap = new HashMap<>();

        //Players creation
        playerHandler = new PlayerHandler();
        ourPlayer = playerHandler.getPlayer(South);

        //Chien creation
        try {
            talon = new Talon();
        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
        }

        userChoice = -1;
    }


    /**
     * Creates all cards and puts them in initialDeck
     * @since v0.7
     */
    public void createCards() {
        for (Suit s : Suit.values()) {
            if ( s != Suit.Trump && s != Suit.Excuse) {
                for (Rank r : Rank.values()) {
                    try {
                        Card c = new Card(s,r);
                        if(!initialDeck.add(c))
                            throw new CardNumberException("Card number limit has been reached.",
                                    initialDeck.getNbMaxCards());
                        updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, c, initialDeck));

                    } catch (CardUniquenessException | CardNumberException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            else if ( s != Suit.Excuse){
                for (int i = 1; i <= Card.getNbMaxTrumps(); i++) {
                    try {
                        Card c = new Card(Suit.Trump,i);
                        if(!initialDeck.add(c))
                            throw new CardNumberException("Card number limit has been reached.",
                                    initialDeck.getNbMaxCards());
                        updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, c, initialDeck));
                    } catch (CardUniquenessException | CardNumberException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            else {
                try {
                    Card c = new Card(Suit.Excuse, -1);
                    if(!initialDeck.add(c))
                        throw new CardNumberException("Card number limit has been reached.",
                                initialDeck.getNbMaxCards());
                    updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, c, initialDeck));
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
        System.out.println("=== DEALER CHOOSING ===");

        //shuffle initial deck
        shuffleCards();

        //spread cards and handle dealer choosing
        updateCard(new CardUpdate(ActionPerformedOnCard.SPREAD_CARDS, initialDeck));

        playerHandler.getPlayersMap().forEach((cardinal,player)-> {
            Card c;
            if ( player == ourPlayer) {

                System.out.println("Choose your card by clicking on it");
                setChanged();
                notifyObservers(ViewActionExpected.PICK_CARD);

                while (userChoice == -1 ) {
                    System.out.println(new Random().nextInt(100));
                    pauseModelFor(10);
                }
                c = initialDeck.get(userChoice);
                userChoice = -1;
            }
            else { //choose a random card for other players
                do {
                    c = randomCard(initialDeck);
                }
                while (c.getSuit() == Suit.Excuse);
            }

            initialDeck.remove(c);
            pickedCardsMap.put(c, player);
            updateCard(new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, c, getPickedCardsDeck()));
        });

        //Turn the card for players to see which is the lowest
        pickedCardsMap.forEach( (card, player) -> {
            card.setShown(true);
            updateCard(new CardUpdate(ActionPerformedOnCard.TURN_CARD, card));
        });

        pauseModelFor(3000);


        //Set dealer from picking
        Card minCard = null;
        for (Map.Entry<Card, Hand> mapEntry : pickedCardsMap.entrySet())
            if (Objects.isNull(minCard))
                minCard = mapEntry.getKey();
            else if ( new Card.CardComparator().compare(mapEntry.getKey(), minCard) == -1 )
                minCard = mapEntry.getKey();

        playerHandler.setFirstDealer(pickedCardsMap.get(minCard));


        //Turn the card again and put back it to initialDeck
        pickedCardsMap.forEach( (card, player) -> {
            card.setShown(false);
            updateCard(new CardUpdate(ActionPerformedOnCard.TURN_CARD, card));
            pauseModelFor(1500);

            if(!initialDeck.add(card)) {
                try {
                    throw new CardNumberException("Card number limit has been reached.",
                            initialDeck.getNbMaxCards());
                } catch (CardNumberException e) {
                    System.err.println(e.getMessage());
                }
            }
            else
                updateCard(new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, card, initialDeck));
        });
        pickedCardsMap.clear();

        updateCard(new CardUpdate(ActionPerformedOnCard.GATHER_CARDS, initialDeck));
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
            updateCard(new CardUpdate(ActionPerformedOnCard.SHUFFLE_CARDS, initialDeck));
            System.out.println("Cutting cards...");
            cutCards();
            updateCard(new CardUpdate(ActionPerformedOnCard.CUT_DECK, initialDeck));
            System.out.println("Dealing cards...");
            dealAllCards();

            ourPlayer.forEach(c -> c.setShown(true));
            updateCard(new CardUpdate(ActionPerformedOnCard.TURN_CARD, ourPlayer));

            //"Petit Sec" checking
            System.out.println("Petit Sec checking...");
            for (Map.Entry<PlayerHandler.PlayersCardinalPoint, Hand> player
                    : playerHandler.getPlayersMap().entrySet()) {

                hasPetitSec = player.getValue().checkHasPetitSec();
                if (hasPetitSec) {
                    playerHandler.changeDealer();

                    gatherAllCards();
                    updateCard(new CardUpdate(ActionPerformedOnCard.GATHER_CARDS, initialDeck));

                    System.out.println("The player has Petit Sec, re-dealing...");
                    break;
                }
            }

            ourPlayer.forEach(c -> c.setShown(true));
            updateCard(new CardUpdate(ActionPerformedOnCard.TURN_CARD, ourPlayer));
        }
        while (hasPetitSec);
        System.out.println("No Petit Sec found.");
    }


    /**
     * Shuffles cards
     * @since v0.5
     * @see Collections
     * @see Random
     */
    public void shuffleCards() {
        long seed = System.nanoTime();
        Collections.shuffle(initialDeck, new Random(seed));
        updateCard(new CardUpdate(ActionPerformedOnCard.SHUFFLE_CARDS, initialDeck));
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
            splitIt = initialDeck.indexOf(randomCard(initialDeck));
            if ( splitIt < 74 && splitIt > 3)
                isValidIterator = true;
        }
        while (!isValidIterator);

        List<Card> cut1 = new ArrayList<>();
        List<Card> cut2 = new ArrayList<>();

        for (int i=0; i <= splitIt; i++)
            cut1.add(initialDeck.get(i));
        for (int i = splitIt+1; i < initialDeck.size(); i++)
            cut2.add(initialDeck.get(i));

        initialDeck.clear();
        initialDeck.addAll(cut2);
        initialDeck.addAll(cut1);

        updateCard(new CardUpdate(ActionPerformedOnCard.CUT_DECK, initialDeck));
    }


    /**
     * Deals card
     * @since v0.5
     */
    public void dealAllCards() {
        int cptNbCardGivenToSameHand = 0;
        while( !initialDeck.isEmpty()) {
            boolean chienReceiveCard = false;
            if( talon.size() < 6
                    && initialDeck.size() < 78) { //Don't give first card to chien
                if ( initialDeck.size() == 2 ) { //Don't give last card to chien
                    moveCardBetweenDecks(initialDeck, talon, initialDeck.get(0));
                    chienReceiveCard = true;
                }
                else {
                    chienReceiveCard = ( (new Random().nextInt(2) != 0)); //simulate a "heads or tails"
                    if (chienReceiveCard) {
                        moveCardBetweenDecks(initialDeck, talon, initialDeck.get(0));
                    }
                }
            }
            if (!chienReceiveCard) {
                moveCardBetweenDecks(initialDeck, playerHandler.getCurrentPlayer(), initialDeck.get(0));
                cptNbCardGivenToSameHand++;
            }
            if (cptNbCardGivenToSameHand == 3) {
                playerHandler.changeCurrentPlayer();
                cptNbCardGivenToSameHand = 0;
            }

        }
        playerHandler.getPlayersMap().forEach( (cardinal, hand) -> {
            hand.sort(new Card.CardComparator());
            updateCard(new CardUpdate(ActionPerformedOnCard.SORT_DECK, ourPlayer));
        });
    }


    /**
     * Retrieve all cards from players and talon
     * to the initial deck
     * @since v0.6
     */
    public void gatherAllCards() {
        playerHandler.getPlayersMap().forEach((cardinal,player)-> {
            while ( !player.isEmpty() ) {
                moveCardBetweenDecks(player, initialDeck, player.get(0));
            }
        });

        while ( !talon.isEmpty() ) {
            moveCardBetweenDecks(talon, initialDeck, talon.get(0));
        }

        initialDeck.forEach(c -> c.setShown(false));
        updateCard(new CardUpdate(ActionPerformedOnCard.TURN_CARD, initialDeck));
        updateCard(new CardUpdate(ActionPerformedOnCard.GATHER_CARDS, initialDeck));
    }


    /**
     * Handle the Bids
     * @since v0.6
     */
    public void handleBids() {
        chooseBids();
        while (ourPlayer.getBidChosen() == Bids.Pass) {
            System.out.println("You've chosen to pass. Re-dealing...");
            ourPlayer.forEach( (card -> card.setShown(false)));
            updateCard(new CardUpdate(ActionPerformedOnCard.TURN_CARD, ourPlayer));
            gatherAllCards();
            playerHandler.changeDealer();
            handleDealing();
            chooseBids();
        }
        System.out.println("You are the taker");
        if ( ourPlayer.getBidChosen()== Bids.Small || ourPlayer.getBidChosen()== Bids.Guard ) {
            System.out.println("You're allowed to constitute your ecart");
            pauseModelFor(500);
            constituteEcart();
        }
    }


    /**
     * Choose bids for each player
     * @since v0.6
     */
    private void chooseBids() {
        System.out.println("=== BIDS ===");
        playerHandler.getPlayersMap().forEach((cardinal,player)-> {
            if ( player == ourPlayer) {
                System.out.println("Here are your cards :");
                ourPlayer.forEach(c -> c.setShown(true));
                updateCard(new CardUpdate(ActionPerformedOnCard.TURN_CARD, ourPlayer));

                System.out.println(ourPlayer.cardListToString());
                System.out.println("Choose your Bids among those one :");
                System.out.println("1. Small");
                System.out.println("2. Guard");
                System.out.println("3. GuardWithoutTheKitty");
                System.out.println("4. GuardAgainstTheKitty");
                System.out.println("5. Pass");

                setChanged();
                notifyObservers(ViewActionExpected.CHOOSE_BID);
                while ( userChoice == -1 ) {
                    pauseModelFor(10);
                }

                switch (userChoice) {
                    case 1:
                        ourPlayer.setBidChosen(Bids.Small);
                        break;
                    case 2:
                        ourPlayer.setBidChosen(Bids.Guard);
                        break;
                    case 3:
                        ourPlayer.setBidChosen(Bids.GuardWithoutTheKitty);
                        break;
                    case 4:
                        ourPlayer.setBidChosen(Bids.GuardAgainstTheKitty);
                        break;
                    case 5:
                        ourPlayer.setBidChosen(Bids.Pass);
                        break;
                    default:
                        break;
                }
                userChoice = -1;
                System.out.println("You have chosen the bid : " + String.valueOf(ourPlayer.getBidChosen()));
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
        talon.forEach(c -> c.setShown(true));
        updateCard(new CardUpdate(ActionPerformedOnCard.TURN_CARD, talon));

        System.out.println(talon.cardListToString());
        System.out.println("Placing talon's cards into taker's deck...");

        while ( !talon.isEmpty() ) {
            moveCardBetweenDecks(talon, ourPlayer, talon.get(0));
        }

        System.out.println("Now, constitute your ecart by putting 6 of your deck's cards in the talon :");
        System.out.println(ourPlayer.cardListToString());

        for (int i=0; i < 6; i++) {
            boolean choiceValid;
            do {
                setChanged();
                notifyObservers(ViewActionExpected.CHOOSE_ECART_CARD);
                while (userChoice == -1 ) {
                    pauseModelFor(10);
                }
                Card c = ourPlayer.get(userChoice);

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
            if ( ourPlayer.get(userChoice).getSuit() != Suit.Trump) {
                ourPlayer.get(userChoice).setShown(false);
                updateCard(new CardUpdate(ActionPerformedOnCard.TURN_CARD, ourPlayer.get(userChoice) ));
            }
            moveCardBetweenDecks(ourPlayer, talon, ourPlayer.get(userChoice));
            System.out.println("Taker : " + ourPlayer.cardListToString());
            System.out.println("Talon : " + talon.cardListToString());
        }
        System.out.println("Ecart done...");
        ourPlayer.sort(new Card.CardComparator());

        updateCard(new CardUpdate(ActionPerformedOnCard.SORT_DECK, ourPlayer));
    }


    /**
     * Check if it is allowed to discard a Trump
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
     * Move a card between two decks
     * @since v0.6
     *@param source the source deck
     * @param target the target deck
     * @param c the card to move
     */
    public void moveCardBetweenDecks(CardGroup source, CardGroup target, Card c) {
        source.remove(c);
        target.add(c);
        updateCard(new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, c, target));
    }


    /**
     * Get a random card in a card list
     * @since v0.5
     * @see Random
     *
     * @return a random card
     */
    public Card randomCard(List<Card> list) {
        int index = new Random().nextInt(list.size());
        return list.get(index);
    }

    public void pauseModelFor(long millis) {
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Notify observers with the type of card update
     * @since v0.6
     */
    public void updateCard(CardUpdate cardUpdate)
    {
        setChanged();
        notifyObservers(cardUpdate);
        if ( cardUpdate.getType() != ActionPerformedOnCard.ADD_CARD) {
            pauseModelFor(300);
        }
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
            result += "Player " + playerHandler.getPlayerName(player.getValue()) + " : ";
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


    //GETTERS & SETTERS - no documentation needed

    public CardGroup getInitialDeck() {
        return initialDeck;
    }
    public CardGroup getPickedCardsDeck() {
        try {
            CardGroup pickedCardDeck = new CardGroup(4);
            pickedCardsMap.forEach( ((card, player) -> pickedCardDeck.add(card)));
            return pickedCardDeck;
        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
    public Talon getTalon() {
        return talon;
    }

    public void setUserChoice(int userChoice) {
        this.userChoice = userChoice;
    }
}
