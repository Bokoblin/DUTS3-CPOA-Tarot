/*
Copyright 2016 Jacquot Alexandre, Jolivet Arthur S3A
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package app.view;

import app.model.Card;
import app.model.CardGroup;
import app.model.NotificationType;
import com.sun.istack.internal.NotNull;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

import java.util.HashMap;

/**
 * The {@code ViewCard} class is a JavaFX extended node
 * with some useful methods to help animating the cards on the table.
 * @author Alexandre
 * @author Arthur
 * @version v0.10
 * @since v0.3
 */
public class ViewCard extends RectangleMesh {
    private Card modelCard;
    private GameView gameView;
    private boolean shown;
    private static final float CARD_HEIGHT = 250;
    private static final float CARD_WIDTH = CARD_HEIGHT * (float)(55.0/88.0);
    private static final float CARD_DEPTH = 1;
    private static final int CARD_FACE_TEXTURE_WIDTH = 256;
    private static final int CARD_FACE_TEXTURE_HEIGHT = 454;

    private static final HashMap<String, String> fileNameMap;

    static {
        fileNameMap = new HashMap<>();
        fileNameMap.put("ClubAce", "Tarot_nouveau_Clubs_Ace.jpg");
        fileNameMap.put("ClubTwo", "Tarot_nouveau_Clubs_02.jpg");
        fileNameMap.put("ClubThree", "Tarot_nouveau_Clubs_03.jpg");
        fileNameMap.put("ClubFour", "Tarot_nouveau_Clubs_04.jpg");
        fileNameMap.put("ClubFive", "Tarot_nouveau_Clubs_05.jpg");
        fileNameMap.put("ClubSix", "Tarot_nouveau_Clubs_06.jpg");
        fileNameMap.put("ClubSeven", "Tarot_nouveau_Clubs_07.jpg");
        fileNameMap.put("ClubEight", "Tarot_nouveau_Clubs_08.jpg");
        fileNameMap.put("ClubNine", "Tarot_nouveau_Clubs_09.jpg");
        fileNameMap.put("ClubTen", "Tarot_nouveau_Clubs_10.jpg");
        fileNameMap.put("ClubJack", "Tarot_nouveau_Clubs_Jack.jpg");
        fileNameMap.put("ClubKnight", "Tarot_nouveau_Clubs_Knight.jpg");
        fileNameMap.put("ClubQueen", "Tarot_nouveau_Clubs_Queen.jpg");
        fileNameMap.put("ClubKing", "Tarot_nouveau_Clubs_King.jpg");
        fileNameMap.put("DiamondAce", "Tarot_nouveau_Diamonds_Ace.jpg");
        fileNameMap.put("DiamondTwo", "Tarot_nouveau_Diamonds_02.jpg");
        fileNameMap.put("DiamondThree", "Tarot_nouveau_Diamonds_03.jpg");
        fileNameMap.put("DiamondFour", "Tarot_nouveau_Diamonds_04.jpg");
        fileNameMap.put("DiamondFive", "Tarot_nouveau_Diamonds_05.jpg");
        fileNameMap.put("DiamondSix", "Tarot_nouveau_Diamonds_06.jpg");
        fileNameMap.put("DiamondSeven", "Tarot_nouveau_Diamonds_07.jpg");
        fileNameMap.put("DiamondEight", "Tarot_nouveau_Diamonds_08.jpg");
        fileNameMap.put("DiamondNine", "Tarot_nouveau_Diamonds_09.jpg");
        fileNameMap.put("DiamondTen", "Tarot_nouveau_Diamonds_10.jpg");
        fileNameMap.put("DiamondJack", "Tarot_nouveau_Diamonds_Jack.jpg");
        fileNameMap.put("DiamondKnight", "Tarot_nouveau_Diamonds_Knight.jpg");
        fileNameMap.put("DiamondQueen", "Tarot_nouveau_Diamonds_Queen.jpg");
        fileNameMap.put("DiamondKing", "Tarot_nouveau_Diamonds_King.jpg");
        fileNameMap.put("HeartAce", "Tarot_nouveau_Hearts_Ace.jpg");
        fileNameMap.put("HeartTwo", "Tarot_nouveau_Hearts_02.jpg");
        fileNameMap.put("HeartThree", "Tarot_nouveau_Hearts_03.jpg");
        fileNameMap.put("HeartFour", "Tarot_nouveau_Hearts_04.jpg");
        fileNameMap.put("HeartFive", "Tarot_nouveau_Hearts_05.jpg");
        fileNameMap.put("HeartSix", "Tarot_nouveau_Hearts_06.jpg");
        fileNameMap.put("HeartSeven", "Tarot_nouveau_Hearts_07.jpg");
        fileNameMap.put("HeartEight", "Tarot_nouveau_Hearts_08.jpg");
        fileNameMap.put("HeartNine", "Tarot_nouveau_Hearts_09.jpg");
        fileNameMap.put("HeartTen", "Tarot_nouveau_Hearts_10.jpg");
        fileNameMap.put("HeartJack", "Tarot_nouveau_Hearts_Jack.jpg");
        fileNameMap.put("HeartKnight", "Tarot_nouveau_Hearts_Knight.jpg");
        fileNameMap.put("HeartQueen", "Tarot_nouveau_Hearts_Queen.jpg");
        fileNameMap.put("HeartKing", "Tarot_nouveau_Hearts_King.jpg");
        fileNameMap.put("SpadeAce", "Tarot_nouveau_Spades_Ace.jpg");
        fileNameMap.put("SpadeTwo", "Tarot_nouveau_Spades_02.jpg");
        fileNameMap.put("SpadeThree", "Tarot_nouveau_Spades_03.jpg");
        fileNameMap.put("SpadeFour", "Tarot_nouveau_Spades_04.jpg");
        fileNameMap.put("SpadeFive", "Tarot_nouveau_Spades_05.jpg");
        fileNameMap.put("SpadeSix", "Tarot_nouveau_Spades_06.jpg");
        fileNameMap.put("SpadeSeven", "Tarot_nouveau_Spades_07.jpg");
        fileNameMap.put("SpadeEight", "Tarot_nouveau_Spades_08.jpg");
        fileNameMap.put("SpadeNine", "Tarot_nouveau_Spades_09.jpg");
        fileNameMap.put("SpadeTen", "Tarot_nouveau_Spades_10.jpg");
        fileNameMap.put("SpadeJack", "Tarot_nouveau_Spades_Jack.jpg");
        fileNameMap.put("SpadeKnight", "Tarot_nouveau_Spades_Knight.jpg");
        fileNameMap.put("SpadeQueen", "Tarot_nouveau_Spades_Queen.jpg");
        fileNameMap.put("SpadeKing", "Tarot_nouveau_Spades_King.jpg");
        fileNameMap.put("Trump1", "Tarot_nouveau_Trumps_01.jpg");
        fileNameMap.put("Trump2", "Tarot_nouveau_Trumps_02.jpg");
        fileNameMap.put("Trump3", "Tarot_nouveau_Trumps_03.jpg");
        fileNameMap.put("Trump4", "Tarot_nouveau_Trumps_04.jpg");
        fileNameMap.put("Trump5", "Tarot_nouveau_Trumps_05.jpg");
        fileNameMap.put("Trump6", "Tarot_nouveau_Trumps_06.jpg");
        fileNameMap.put("Trump7", "Tarot_nouveau_Trumps_07.jpg");
        fileNameMap.put("Trump8", "Tarot_nouveau_Trumps_08.jpg");
        fileNameMap.put("Trump9", "Tarot_nouveau_Trumps_09.jpg");
        fileNameMap.put("Trump10", "Tarot_nouveau_Trumps_10.jpg");
        fileNameMap.put("Trump11", "Tarot_nouveau_Trumps_11.jpg");
        fileNameMap.put("Trump12", "Tarot_nouveau_Trumps_12.jpg");
        fileNameMap.put("Trump13", "Tarot_nouveau_Trumps_13.jpg");
        fileNameMap.put("Trump14", "Tarot_nouveau_Trumps_14.jpg");
        fileNameMap.put("Trump15", "Tarot_nouveau_Trumps_15.jpg");
        fileNameMap.put("Trump16", "Tarot_nouveau_Trumps_16.jpg");
        fileNameMap.put("Trump17", "Tarot_nouveau_Trumps_17.jpg");
        fileNameMap.put("Trump18", "Tarot_nouveau_Trumps_18.jpg");
        fileNameMap.put("Trump19", "Tarot_nouveau_Trumps_19.jpg");
        fileNameMap.put("Trump20", "Tarot_nouveau_Trumps_20.jpg");
        fileNameMap.put("Trump21", "Tarot_nouveau_Trumps_21.jpg");
        fileNameMap.put("Excuse", "Tarot_nouveau_Excuse.jpg");
    }


    /**
     * Constructs a view card with a model card, a view and a group
     * @since v0.6.2
     *
     * @param modelCard the model card related to this view card
     * @param view the related view of the card
     * @param group the group that belongs to the card
     */
    public ViewCard(@NotNull Card modelCard, @NotNull GameView view, @NotNull Group group)
    {
        super(CARD_WIDTH, CARD_HEIGHT, CARD_DEPTH, "file:./res/" + getFileName(modelCard),
                CARD_FACE_TEXTURE_WIDTH, CARD_FACE_TEXTURE_HEIGHT);
        this.modelCard = modelCard;
        this.gameView = view;
        this.shown = true;
        group.getChildren().add(this);
        view.getViewCardToGroup().put(this, group);
        setPosition(view.getCardDefaultPosition(this));
        setRotationAxis(Rotate.Z_AXIS);
        setRotate(view.getCardDefaultRotation(this));

        //=== EVENTS

        this.setOnMouseEntered(event -> {
            if ( gameView.isHandlingCardPicking() )
                if ( (gameView.getGameModel().getAwaitsUserEvent() == NotificationType.PICK_CARD  )
                        ||
                        (gameView.getGameModel().getAwaitsUserEvent() == NotificationType.CHOOSE_ECART_CARD
                        && gameView.getSouth().getChildren().contains(this) )
                ) {
                    gameView.setCursor(Cursor.HAND);
            }
        });

        this.setOnMouseClicked(event -> {
            if (gameView.isHandlingCardPicking())
            {
                CardGroup cardGroup;
                if( gameView.getGameModel().getAwaitsUserEvent() == NotificationType.PICK_CARD)
                    cardGroup = gameView.getCardGroupFromGroup(gameView.getWholeCardsDeck());
                else
                    cardGroup = gameView.getCardGroupFromGroup(gameView.getSouth());

                if (cardGroup != null && cardGroup.contains(modelCard))
                {
                    gameView.getAppPresenter().transmitUserChoice(cardGroup.indexOf(modelCard));
                    gameView.setHandleCardPicking(false);
                    gameView.getToolTip().setText("Please wait...");
                }
            }
        });

        this.setOnMouseExited(event -> gameView.setCursor(Cursor.DEFAULT));
    }


    /**
     * Constructs a view card with a model card
     * @since v0.3
     *
     * @param modelCard the model card related to this view card
     */
    public ViewCard(@NotNull Card modelCard)
    {
        super(CARD_WIDTH, CARD_HEIGHT, CARD_DEPTH, "file:./res/" + getFileName(modelCard),
                CARD_FACE_TEXTURE_WIDTH, CARD_FACE_TEXTURE_HEIGHT);
        this.modelCard = modelCard;
    }


    /**
     * This method gets filename of the card image thanks to the card name
     * and a map associating card name and filename
     * @since v0.8.1
     *
     * @param modelCard the model card related to this view card
     * @return a string containing filename
     */
    private static String getFileName(Card modelCard) {
        return fileNameMap.get(modelCard.getName() );
    }


    //GETTERS & SETTERS - no documentation needed

    public Card getModelCard()
    {
        return modelCard;
    }
    public static float getWidth() {
        return CARD_WIDTH;
    }
    public static float getHeight() {
        return CARD_HEIGHT;
    }
    public static float getDepth() {
        return CARD_DEPTH;
    }
    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }
}
