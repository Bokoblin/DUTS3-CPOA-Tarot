package app.model;

import com.sun.istack.internal.NotNull;

/**
 * This class is a container which is passed when calling notifyObservers() method.
 * It indicate to view what action to perform on a specific card with sometimes a specific cardGroup.
 * @author Alexandre
 * @version v0.8
 * @since v0.6
 */
public class CardUpdate {

    private CardGroup cardGroup;
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
        try {
            if (type == ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS) {
                throw new Exception("Cannot move a card without specifying the destination group.");
            }
            this.card = card;
            this.cardGroup = null;
            this.type = type;
        }
        catch ( Exception e) {
            System.err.println(e.getMessage());
            this.card = null;
            this.cardGroup = null;
            this.type = null;
        }
    }


    /**
     * Constructs CardUpdate with a card, a group and a type
     * @since v0.6
     *@param type the type
     * @param card the model card
     * @param group the cardGroup
     */
    public CardUpdate(ActionPerformedOnCard type, @NotNull Card card, @NotNull CardGroup group)
    {
        this.cardGroup = group;
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
    public CardUpdate(ActionPerformedOnCard type, @NotNull CardGroup cardGroup) {
        try {
            if (type == ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS)
                throw new Exception("Cannot move a card with is not specified.");

            this.cardGroup = cardGroup;
            this.card = null;
            this.type = type;
        }
        catch ( Exception e) {
            System.err.println(e.getMessage());
            this.card = null;
            this.cardGroup = null;
            this.type = null;
        }
    }


    //GETTERS - no documentation needed

    public CardGroup getCardGroup() {
        return cardGroup;
    }
    public Card getCard() {
        return card;
    }
    public ActionPerformedOnCard getType() {
        return type;
    }
}
