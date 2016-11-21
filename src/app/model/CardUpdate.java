package app.model;

import com.sun.istack.internal.NotNull;

/**
 * This class is a container which is passed when calling notifyObservers() method.
 * It indicate to view what action to perform on a specific card with sometimes a specific cardGroup.
 * @author Alexandre
 * @version v0.6.3
 * @since v0.6
 */
public class CardUpdate {
    private CardGroup cardGroup;
    private Card card;
    private ActionPerformedOnCard type;

    //TODO : Documentation
    public CardUpdate(ActionPerformedOnCard type, @NotNull Card card) throws Exception {
        if (type == ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS)
        {
            throw new Exception("Cannot move a card without specifying the destination group.");
        }
        this.card = card;
        this.cardGroup = null;
        this.type = type;
    }

    //TODO : Documentation
    public CardUpdate(ActionPerformedOnCard type, @NotNull Card card, @NotNull CardGroup cardGroup)
    {
        this.cardGroup = cardGroup;
        this.card = card;
        this.type = type;
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
