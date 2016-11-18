package app.view;

import com.sun.istack.internal.NotNull;
import app.model.Card;

/**
 * The {@code ViewCard} class is a JavaFX extended node
 * with some useful methods to help animating the cards on the table.
 * @author Alexandre
 * @version v0.3
 * @since v0.3
 */
public class ViewCard extends RectangleMesh {
    private Card modelCard;
    private static final float CARD_HEIGHT = 200;
    private static final float CARD_WIDTH = CARD_HEIGHT * (55/88);
    private static final float CARD_DEPTH = 2;
    private static final int CARD_FACE_TEXTURE_WIDTH = 1536;
    private static final int CARD_FACE_TEXTURE_HEIGHT = 2663;

    /**
     * Constructs a view card with default values
     * @since v0.6.2
     *
     * @param modelCard the model card related to this view card
     */
    public ViewCard(@NotNull Card modelCard)
    {
        super(CARD_WIDTH, CARD_HEIGHT, CARD_DEPTH,
                "file:./res/" + modelCard.getName().toLowerCase() + ".jpg",
                CARD_FACE_TEXTURE_WIDTH, CARD_FACE_TEXTURE_HEIGHT);
        this.modelCard = modelCard;
    }

    /**
     * Constructs a view card
     * @since v0.3.0
     *
     * @param width the card width
     * @param height the card height
     * @param depth the card depth
     * @param textureFaceWidth the width of card face on texture
     * @param textureFaceHeight the height of card face on texture
     * @param modelCard the model card related to this view card
     */
    public ViewCard(float width, float height, float depth,
                    float textureFaceWidth, float textureFaceHeight, @NotNull Card modelCard)
    {
        super(width, height, depth, "file:./res/testCarte"
                + ".jpg", textureFaceWidth, textureFaceHeight);
        this.modelCard = modelCard;
    }


    //GETTERS - no documentation needed

    public Card getModelCard()
    {
        return modelCard;
    }
}
