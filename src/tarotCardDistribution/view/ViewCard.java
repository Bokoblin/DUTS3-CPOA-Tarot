package tarotCardDistribution.view;

import tarotCardDistribution.model.Card;

/**
 * The {@code ViewCard} class is a JavaFX extended node with some useful methods to help animating the cards on the table.
 * @author Alexandre
 * @version v0.3
 * @since v0.3
 */
public class ViewCard extends RectangleMesh {
    private Card modelCard;

    public ViewCard(float width, float height, float depth, String texturePath, float textureFaceWidth, float textureFaceHeight, Card modelCard)
    {
        super(width, height, depth, texturePath, textureFaceWidth, textureFaceHeight);
        this.modelCard = modelCard;
    }

    //GETTERS - no documentation needed

    public Card getModelCard()
    {
        return modelCard;
    }
}
