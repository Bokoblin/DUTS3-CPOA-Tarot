package app.view;

import com.sun.istack.internal.NotNull;
import app.model.Card;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

/**
 * The {@code ViewCard} class is a JavaFX extended node
 * with some useful methods to help animating the cards on the table.
 * @author Alexandre
 * @version v0.7
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
     * Constructs a view card with a model card, a view and a group
     * @since v0.6.2
     *
     * @param modelCard the model card related to this view card
     * @param view the related view of the card
     * @param group the group that belongs to the card
     */
    public ViewCard(@NotNull Card modelCard, @NotNull AppView view, @NotNull Group group)
    {
        super(CARD_WIDTH, CARD_HEIGHT, CARD_DEPTH, "file:./res/testCarte" + ".jpg", CARD_FACE_TEXTURE_WIDTH, CARD_FACE_TEXTURE_HEIGHT);
        this.modelCard = modelCard;
        group.getChildren().add(this);
        view.getViewCardToGroup().put(this, group);
        setPosition(view.getCardDefaultPosition(this));
        setRotationAxis(Rotate.Z_AXIS);
        setRotate(view.getCardDefaultRotation(this));
    }

    /**
     * Constructs a view card with a model card
     * @since v0.3
     *
     * @param modelCard the model card related to this view card
     */
    public ViewCard(@NotNull Card modelCard)
    {
        super(CARD_WIDTH, CARD_HEIGHT, CARD_DEPTH, "file:./res/testCarte" + ".jpg", CARD_FACE_TEXTURE_WIDTH, CARD_FACE_TEXTURE_HEIGHT);
        this.modelCard = modelCard;
    }


    //GETTERS - no documentation needed

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
    public static float getCardDepth() {
        return CARD_DEPTH;
    }
}
