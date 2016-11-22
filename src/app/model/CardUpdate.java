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
    public CardUpdate(ActionPerformedOnCard type, @NotNull Card card) throws Exception {
        if (type == ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS)
        {
            throw new Exception("Cannot move a card without specifying the destination group.");
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
    public CardUpdate(ActionPerformedOnCard type, @NotNull CardGroup cardGroup) {
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
