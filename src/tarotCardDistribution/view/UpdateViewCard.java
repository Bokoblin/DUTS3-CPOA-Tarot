package tarotCardDistribution.view;

import com.sun.istack.internal.NotNull;
import tarotCardDistribution.model.Card;
import tarotCardDistribution.model.CardGroup;

public class UpdateViewCard {
    private CardGroup cardGroup;
    private Card card;
    private UpdateViewCardType type;

    public UpdateViewCard(UpdateViewCardType type, @NotNull Card card)
    {
        if (type == UpdateViewCardType.CHANGECARDGROUP)
        {
            //Throw an exception
        }
        this.card = card;
        this.cardGroup = null;
        this.type = type;
    }

    public UpdateViewCard(UpdateViewCardType type, @NotNull Card card, @NotNull CardGroup cardGroup)
    {
        if (!cardGroup.getCardList().contains(card))
        {
            //Throw an exception
        }
        this.cardGroup = cardGroup;
        this.card = card;
        this.type = type;
    }

    public CardGroup getCardGroup() {
        return cardGroup;
    }

    public Card getCard() {
        return card;
    }

    public UpdateViewCardType getType() {
        return type;
    }
}
