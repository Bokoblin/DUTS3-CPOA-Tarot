package tarotCardDistribution.model;

import exceptions.CardGroupNumberException;

/**
 * The {@code PlayerHandler} class handles players creation
 * and their temporary status (dealer, shuffler, cutter, currentPlayer)
 * @author Arthur
 * @version v0.6
 * @since v0.6
 *
 * @see Hand
 */
public class PlayerHandler {
    private Hand north;
    private Hand west;
    private Hand south; //our Player
    private Hand east;

    private Hand dealer;
    private Hand shuffler; //dealer's opposite player
    private Hand cutter; //dealer's left player
    private Hand currentPlayer; //dealer's right player

    /**
     * Constructs a player handler and the 4 players
     * @since v0.6
     * @throws CardGroupNumberException if user tries to create too much hands
     */
    public PlayerHandler() throws CardGroupNumberException {
        try {
            north = new Hand();
            west = new Hand();
            south = new Hand();
            east = new Hand();
        } catch (CardGroupNumberException e) {
            System.err.println(e.getMessage());
        }

        dealer = null;
        shuffler = null;
        cutter = null;
        currentPlayer = north;
    }

    /**
     * Sets first dealer and the shuffler and cutter
     * @since v0.6
     * @param firstDealer the first dealer object
     */
    public void setFirstDealer(Hand firstDealer) {
        dealer = firstDealer;
        if ( dealer == north) {
            shuffler = south;
            cutter = east;
        }
        else if ( currentPlayer == west) {
            shuffler = east;
            cutter = north;
        }
        else if ( currentPlayer == south) {
            shuffler = north;
            cutter = west;
        }
        else {
            shuffler = west;
            cutter = south;
        }
        setFirstCurrentPlayer();
    }

    /**
     * Sets first currentPlayer
     * @since v0.6
     */
    private void setFirstCurrentPlayer() {
        if ( dealer == north)
            currentPlayer = west;
        else if ( currentPlayer == west)
            currentPlayer = south;
        else if ( currentPlayer == south)
            currentPlayer = east;
        else
            currentPlayer = north;
    }

    /**
     * Change dealer counter-clockwise / rightward
     * @since v0.6
     */
    public void changeDealer() {
        if ( dealer == north) {
            dealer = west;
            shuffler = east;
            cutter = north;
        }
        else if ( currentPlayer == west) {
            dealer = south;
            shuffler = north;
            cutter = west;
        }
        else if ( currentPlayer == south) {
            dealer = east;
            shuffler = west;
            cutter = south;
        }
        else {
            dealer = north;
            shuffler = south;
            cutter = east;
        }
        setFirstCurrentPlayer();
    }

    /**
     * Change current player counter-clockwise / rightward
     * @since v0.6
     */
    public void changeCurrentPlayer() {
        if ( currentPlayer == north)
            currentPlayer = west;
        else if ( currentPlayer == west)
            currentPlayer = south;
        else if ( currentPlayer == south)
            currentPlayer = east;
        else
            currentPlayer = north;
    }

    /**
     * Get player name
     * @since v0.6
     * @param player the player whose name is requested
     */
    public String getPlayerName(Hand player) {
        if ( player == north)
            return "North";
        else if ( currentPlayer == west)
            return "West";
        else if ( currentPlayer == south)
            return "South";
        else
            return "East";
    }


    //GETTERS - no documentation needed

    public Hand getCurrentPlayer() {
        return currentPlayer;
    }
    public Hand getSouth() {
        return south;
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

}
