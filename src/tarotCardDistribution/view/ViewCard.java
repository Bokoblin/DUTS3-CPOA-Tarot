package tarotCardDistribution.view;

import com.sun.istack.internal.NotNull;
import javafx.geometry.Point3D;
import tarotCardDistribution.model.Card;

/**
 * The {@code ViewCard} class is a JavaFX extended node
 * with some useful methods to help animating the cards on the table.
 * @author Alexandre
 * @version v0.3
 * @since v0.3
 */
public class ViewCard extends RectangleMesh {
    private Card modelCard;
    private static final float CARD_HEIGHT = 250;
    private static final float CARD_WIDTH = CARD_HEIGHT * (float)(55.0/88.0);
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
        super(CARD_WIDTH, CARD_HEIGHT, CARD_DEPTH, "file:./res/testCarte" + ".jpg", CARD_FACE_TEXTURE_WIDTH, CARD_FACE_TEXTURE_HEIGHT);
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
                    float textureFaceWidth, float textureFaceHeight, @NotNull Card modelCard, float x, float y, float z)
    {
        super(width, height, depth, "file:./res/testCarte" + ".jpg", textureFaceWidth, textureFaceHeight);
        this.modelCard = modelCard;
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setTranslateZ(z);
    }

    //GETTERS - no documentation needed

    public void setPosition(Point3D point3D)
    {
        this.setTranslateX(point3D.getX());
        this.setTranslateY(point3D.getY());
        this.setTranslateZ(point3D.getZ());
    }

    public Point3D getPosition()
    {
        return new Point3D(getTranslateX(), getTranslateY(), getTranslateZ());
    }

    public Card getModelCard()
    {
        return modelCard;
    }

    public static float getCardWidth() {
        return CARD_WIDTH;
    }

    public static float getCardHeight() {
        return CARD_HEIGHT;
    }
}
