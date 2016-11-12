package tarotCardDistribution.model;

//TODO : Documentation

import com.sun.istack.internal.NotNull;
import tarotCardDistribution.model.ActionPerformedOnCard;
import tarotCardDistribution.model.Card;
import tarotCardDistribution.model.CardGroup;

public class CardUpdate {
    private CardGroup cardGroup;
    private Card card;
    private ActionPerformedOnCard type;

    public CardUpdate(ActionPerformedOnCard type, @NotNull Card card)
    {
        if (type == ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS)
        {
            try {
                throw new Exception("Cannot move a card without specifying the destination group.");
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        this.card = card;
        this.cardGroup = null;
        this.type = type;
    }

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
