package tarotCardDistribution.view;

import com.sun.istack.internal.NotNull;
import tarotCardDistribution.model.Card;

/**
 * The {@code ViewCard} class is a JavaFX extended node with some useful methods to help animating the cards on the table.
 * @author Alexandre
 * @version v0.3
 * @since v0.3
 */
public class ViewCard extends RectangleMesh {
    private Card modelCard;
    public static final float CARDHEIGHT = 200;
    public static final float CARDWIDTH = CARDHEIGHT * (55/88);
    public static final float CARDDEPTH = 2;

    //TODO : Documentation
    public ViewCard(float width, float height, float depth, float textureFaceWidth, float textureFaceHeight, @NotNull Card modelCard)
    {
        super(width, height, depth, "file:./res/" + modelCard.getName().toLowerCase() + ".jpg", textureFaceWidth, textureFaceHeight);
        this.modelCard = modelCard;
    }

    //GETTERS - no documentation needed

    public Card getModelCard()
    {
        return modelCard;
    }
}
