package app.view;

import app.model.Card;
import app.model.CardGroup;
import com.sun.istack.internal.NotNull;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;

/**
 * The {@code ViewCard} class is a JavaFX extended node
 * with some useful methods to help animating the cards on the table.
 * @author Alexandre
 * @version v0.8
 * @since v0.3
 */
public class ViewCard extends RectangleMesh {
    private Card modelCard;
    private AppView appView;
    private boolean shown;
    private static final float CARD_HEIGHT = 250;
    private static final float CARD_WIDTH = CARD_HEIGHT * (float)(55.0/88.0);
    private static final float CARD_DEPTH = 1;
    private static final int CARD_FACE_TEXTURE_WIDTH = 153;
    private static final int CARD_FACE_TEXTURE_HEIGHT = 266;

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
        this.appView = view;
        this.shown = true;
        group.getChildren().add(this);
        view.getViewCardToGroup().put(this, group);
        setPosition(view.getCardDefaultPosition(this));
        setRotationAxis(Rotate.Z_AXIS);
        setRotate(view.getCardDefaultRotation(this));

        //Events :
        this.setOnMouseClicked(event -> {
            if (appView.isHandleCardPicking())
            {
                CardGroup cardGroup = appView.getCardGroupFromGroup(appView.getInitialDeck());  //Technically the card is still in initialDeck in the model.
                if (cardGroup != null && cardGroup.contains(modelCard))
                {
                    try {
                        appView.getAppPresenter().transmitUserChoice(cardGroup.indexOf(modelCard));
                        appView.setHandleCardPicking(false);
                    } catch (Exception e)
                    {
                        System.err.print(e.toString());
                    }
                }
            }
        });

        this.setOnMouseEntered(event -> {
            if (appView.isHandleCardPicking() && appView.getRoot3d().getChildren().contains(this))
            {
                appView.setCursor(Cursor.HAND);
            }
        });

        this.setOnMouseExited(event -> appView.setCursor(Cursor.DEFAULT));
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
    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }
}
