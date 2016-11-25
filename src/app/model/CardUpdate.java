package app.model;

import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * This class is a container which is passed when calling notifyObservers() method.
 * It indicate to view what action to perform on a specific card with sometimes a specific cardGroup.
 * @author Alexandre
 * @version v0.7.1
 * @since v0.6
 */
public class CardUpdate {
    private List<Card> cardGroup;
    private Card card;
    private ActionPerformedOnCard type;

    /**
     * Constructs CardUpdate with a card and a type
     * @since v0.6
     *
     * @param card the model card
     * @param type the type
     */
    public CardUpdate(ActionPerformedOnCard type, @NotNull Card card) {
        if (type == ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS || type == ActionPerformedOnCard.SHUFFLE_CARDS
                || type == ActionPerformedOnCard.SPREAD_CARDS || type == ActionPerformedOnCard.GATHER_CARDS
                || type == ActionPerformedOnCard.CUT_DECK)
        {
            System.err.println("Cannot do the specific action : " + type.toString() + " without specifying the group. The update will be canceled.");
            this.card = null;
            this.cardGroup = null;
            this.type = null;
        }
        this.card = card;
        this.cardGroup = null;
        this.type = type;
    }

    /**
     * Constructs CardUpdate with a card, a group and a type
     * @since v0.6
     *@param type the type
     * @param card the model card
     * @param list the cardGroup
     */
    public CardUpdate(ActionPerformedOnCard type, @NotNull Card card, @NotNull List<Card> list)
    {
        this.cardGroup = list;
        this.card = card;
        this.type = type;
    }

    /**
     * Constructs CardUpdate with a group and a type
     * @since v0.7
     *
     * @param type the type
     * @param cardGroup the cardGroup
     */
    public CardUpdate(ActionPerformedOnCard type, @NotNull CardGroup cardGroup)  {
        if (type == ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS || type == type.ADD_CARD ||
                type == ActionPerformedOnCard.REMOVE_CARD_FROM_GROUP || type == ActionPerformedOnCard.DELETE_CARD)
        {
            System.err.println("Cannot do the specific action : " + type.toString() + " without specifying the card. The update will be canceled.");
            this.card = null;
            this.cardGroup = null;
            this.type = null;
        }
        this.cardGroup = cardGroup;
        this.card = null;
        this.type = type;
    }


    //GETTERS - no documentation needed

    public List<Card> getCardGroup() {
        return cardGroup;
    }
    public Card getCard() {
        return card;
    }
    public ActionPerformedOnCard getType() {
        return type;
    }
}
