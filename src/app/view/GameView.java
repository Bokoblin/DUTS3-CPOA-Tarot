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

import app.model.*;
import app.presenter.AppPresenter;
import com.sun.istack.internal.NotNull;
import exceptions.NullViewCardException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


/**
 * The {@code GameView} class consists in the MVC architecture view
 * @author Alexandre
 * @author Arthur
 * @version v0.10
 * @since v0.2
 *
 * @see Observer
 * @see Scene
 */
public class GameView extends Scene implements Observer {

    private static final float TABLE_SIZE = 2500;
    private static final float MARGIN_TABLE = 130;
    private static final float MARGIN_CARDS = 30;
    private static final float TABLE_DEPTH = 182;
    private static final float HAND_MARGIN_UP = 100;
    private static final float HAND_MARGIN_LEFT = (float)(0.2 * TABLE_SIZE);
    private static final float MARGIN_BETWEEN_HAND_CARDS = (TABLE_SIZE-(2*HAND_MARGIN_LEFT))/18;
    private static final Point3D TALON_POSITION = new Point3D((TABLE_SIZE/2) - (ViewCard.getWidth()/2),
            (TABLE_SIZE/2)-(ViewCard.getHeight()/2), 0);
    private static final Point3D INITIAL_DECK_POSITION = new Point3D(-300, TABLE_SIZE/2, -200);
    private static final Point3D PICKED_CARD_DECK_POSITION = new Point3D(MARGIN_TABLE,
            TABLE_SIZE-MARGIN_TABLE-ViewCard.getHeight(), 0);


    private GameModel gameModel;
    private AppPresenter appPresenter;

    //Groups
    private Group root3D;
    private Group rootGUI;
    private SubScene subSceneGUI;
    private Group background;
    private Group wholeCardsDeck;
    private Group pickedCardDeck;
    private Group talon;
    private Group[] hands = new Group[4];
    private HashMap<CardGroup, Group> cardGroupToGroup;
    private HashMap<ViewCard, Group> viewCardToGroup;

    //GUI elements
    private Label stateTitle;
    private Label toolTip;
    private Label errorSnack;
    private VBox bidBox;

    private boolean handleCardPicking;


    /**
     * Constructs a view for a specific root node and with a model and a presenter
     * @since   v0.1
     * @param   model       the model it reads
     * @param   controller  the presenter it sends event information
     */
    public GameView(Group root, GameModel model, AppPresenter controller) {
        super(root, 800, 600, true, SceneAntialiasing.DISABLED);

        this.gameModel = model;
        this.appPresenter = controller;
        this.handleCardPicking = false;
        this.setFill(Color.BLACK);
        model.addObserver(this);

        //=== Create the groups
        root3D = new Group();
        rootGUI = new Group();
        subSceneGUI = new SubScene(root3D, 800, 600, false, SceneAntialiasing.BALANCED);
        background = new Group();
        wholeCardsDeck = new Group();
        pickedCardDeck = new Group();
        talon = new Group();
        viewCardToGroup = new HashMap<>();
        cardGroupToGroup = new HashMap<>();

        for (PlayerHandler.PlayersCardinalPoint cardinalPoint :
                PlayerHandler.PlayersCardinalPoint.values()) { hands[cardinalPoint.ordinal()] = new Group();}
        updateCardGroupToGroup();

        RectangleMesh table = new RectangleMesh(TABLE_SIZE, TABLE_SIZE, TABLE_DEPTH,
                "file:./res/table.jpg", 1100, 1100);

        //=== Define the camera

        ViewCamera camera3D = new ViewCamera(true);
        camera3D.setTranslateX(TABLE_SIZE/2);
        camera3D.setTranslateY(4200);
        camera3D.setTranslateZ(-3800);
        camera3D.getTransformations().getRotateX().setAngle(35);

        //camera for GUI
        ViewCamera camera2D = new ViewCamera(true);
        camera2D.setTranslateX(TABLE_SIZE/2);
        camera2D.setTranslateY(1200);
        camera2D.setTranslateZ(-5500);
        camera2D.getTransformations().getRotateX().setAngle(0);

        this.setCamera(camera3D);
        subSceneGUI.setCamera(camera2D);


        //=== Define the light

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(getWidth()/2);
        pointLight.setTranslateY(getHeight()/2);
        pointLight.setTranslateZ(-20000);

        //=== Create GUI elements

        rootGUI.setStyle("-fx-focus-color: transparent;");

        stateTitle = new Label();
        stateTitle.setTranslateX(1000);
        stateTitle.setTranslateY(-200);
        stateTitle.setScaleX(2);
        stateTitle.setScaleY(2);
        stateTitle.setTextFill(Color.WHITE);
        stateTitle.setFont(new Font(40));

        toolTip = new Label();
        toolTip.setTranslateX(3000);
        toolTip.setTranslateY(0);
        toolTip.setScaleX(2);
        toolTip.setScaleY(2);
        toolTip.setTextFill(Color.WHITE);
        toolTip.setFont(new Font(35));

        errorSnack = new Label();
        errorSnack.setTranslateX(1000);
        errorSnack.setTranslateY(2700);
        errorSnack.setScaleX(2);
        errorSnack.setScaleY(2);
        errorSnack.setTextFill(Color.RED);
        errorSnack.setFont(new Font(30));

        Button bidSmall = new Button("Small");
        bidSmall.setTextFill(Color.BLACK);
        bidSmall.setMinSize(160, 30);
        bidSmall.setOnAction(event -> {
            appPresenter.transmitUserChoice(1);
            bidBox.setVisible(false);
        });
        bidSmall.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        bidSmall.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        Button bidGuard = new Button("Guard");
        bidGuard.setTextFill(Color.BLACK);
        bidGuard.setMinSize(160, 30);
        bidGuard.setOnAction(event -> {
            appPresenter.transmitUserChoice(2);
            bidBox.setVisible(false);
        });
        bidGuard.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        bidGuard.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        Button bidGuardWithout = new Button("Guard Without The Kitty");
        bidGuardWithout.setTextFill(Color.BLACK);
        bidGuardWithout.setMinSize(160, 30);
        bidGuardWithout.setOnAction(event  -> {
            appPresenter.transmitUserChoice(3);
            bidBox.setVisible(false);
        });
        bidGuardWithout.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        bidGuardWithout.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        Button bidGuardAgainst = new Button("Guard Against The Kitty");
        bidGuardAgainst.setTextFill(Color.BLACK);
        bidGuardAgainst.setMinSize(160, 30);
        bidGuardAgainst.setOnAction(event -> {
            appPresenter.transmitUserChoice(4);
            bidBox.setVisible(false);
        });
        bidGuardAgainst.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        bidGuardAgainst.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        Button bidPass = new Button("Pass");
        bidPass.setTextFill(Color.BLACK);
        bidPass.setMinSize(160, 30);
        bidPass.setOnAction(event -> {
            appPresenter.transmitUserChoice(5);
            bidBox.setVisible(false);
        });
        bidPass.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        bidPass.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );


        bidBox = new VBox(10);
        bidBox.setPadding(new Insets(10, 50, 50, 50));
        bidBox.setTranslateZ(-500);
        bidBox.setScaleX(4.5);
        bidBox.setScaleY(4.5);
        bidBox.setTranslateX(2800);
        bidBox.setTranslateY(1200);
        bidBox.getChildren().addAll(bidSmall, bidGuard, bidGuardWithout, bidGuardAgainst, bidPass);
        bidBox.setVisible(false);

        //=== Add elements to groups

        background.getChildren().add(table);
        root.getChildren().addAll(root3D, rootGUI);
        rootGUI.getChildren().addAll(stateTitle, toolTip, errorSnack, bidBox);
        root3D.getChildren().addAll(background, talon, wholeCardsDeck, pickedCardDeck, pointLight);
        for ( Group hand : hands)
            root3D.getChildren().add(hand);

        this.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode())
            {
                case M:
                    root3D.setVisible(false);
                    rootGUI.setVisible(false);
                    gameModel.setGameState(GameState.ENDED);
                    Platform.runLater( () -> gameModel.quitGame());
                default:
                    break;
            }
        });
    }


    /**
     * Because of the keys of the @cardGroupToGroup change,
     * whe have to update this HashMap before search keys on it.
     * @since v0.7
     *
     */
    private void updateCardGroupToGroup() {
        cardGroupToGroup = new HashMap<>();
        for (PlayerHandler.PlayersCardinalPoint playersCardinalPoint :
                PlayerHandler.PlayersCardinalPoint.values())
        {
            CardGroup cardGroup = gameModel.getPlayerHandler().getPlayer(playersCardinalPoint);
            cardGroupToGroup.put(cardGroup, hands[playersCardinalPoint.ordinal()]);
        }
        cardGroupToGroup.put(gameModel.getTalon(), talon);
        cardGroupToGroup.put(gameModel.getWholeCardsDeck(), wholeCardsDeck);
        cardGroupToGroup.put(gameModel.getToPickDeck(), wholeCardsDeck);
        cardGroupToGroup.put(gameModel.getPickedCardsDeck(), pickedCardDeck);
    }


    /**
     * This method is called whenever the observed object is changed.
     * It determines which object of the gameModel has been changed with arg
     * parameter and updates view in consequence
     * @since   v0.2
     *
     * @param   o     the observable object.
     * @param   arg   an argument passed to the <code>notifyObservers</code> method.
     */
    @Override
    public void update(Observable o, Object arg) {
        updateCardGroupToGroup();
        if (arg instanceof CardUpdate)
        {
            CardUpdate cardUpdate = (CardUpdate)arg;
            if (cardUpdate.getType() != null) {
                Platform.runLater(() -> {
                    try {
                        switch (cardUpdate.getType()) {
                            case ADD_CARD:
                                addNewCard(cardUpdate);
                                break;
                            case FLIP_CARD:
                                if (cardUpdate.getCard() == null) {
                                    for (Card c : cardUpdate.getCardGroup())
                                        flipBackCard(c, 2500);
                                } else
                                    flipBackCard(cardUpdate.getCard(), 2000);
                                break;
                            case MOVE_CARD_BETWEEN_GROUPS:
                                changeCardGroup(cardUpdate, 1000);
                                break;
                            case REMOVE_CARD_FROM_GROUP:
                                removeCardFromGroup(cardUpdate);
                                break;
                            case DELETE_CARD:
                                removeCard(cardUpdate.getCard());
                                break;
                            case SHUFFLE_CARDS:
                                shuffleDeck(cardUpdate.getCardGroup());
                                break;
                            case SORT_DECK:
                                sortDeck(cardUpdate.getCardGroup());
                                break;
                            case CUT_DECK:
                                cutDeck(cardUpdate.getCardGroup());
                                break;
                            case SPREAD_CARDS:
                                spreadAllCards(cardUpdate.getCardGroup());
                                break;
                            case GATHER_CARDS:
                                if ( gameModel.getGameState() == GameState.ENDED) {
                                    stateTitle.setText("GAME ENDING");
                                    toolTip.setText("");
                                }
                                gatherAllCards(cardUpdate.getCardGroup());
                                break;
                            default:
                                break;
                        }
                    } catch (NullViewCardException e) {
                        System.err.println(e.getMessage());
                    }
                });
            }
        }
        else if ( arg instanceof NotificationType) {
            Platform.runLater(() -> {
                switch ( (NotificationType)arg ) {
                    case PICK_CARD:
                        handleCardPicking = true;
                        toolTip.setText("Please select a card by clicking on it");
                        break;
                    case CHOOSE_ECART_CARD:
                        handleCardPicking = true;
                        toolTip.setText("You are the taker\nPlease select a card");
                        break;
                    case CHOOSE_BID:
                        bidBox.setVisible(true);
                        toolTip.setText("Select a bid with the buttons below");
                        break;
                    case UNAUTHORIZED_CARD_CHOICE:
                        errorSnack.setText("You can't choose a Trump, a King or Excuse");
                        new Timeline(new KeyFrame( Duration.millis(2500), t -> errorSnack.setText(""))).play();
                        break;
                    default:
                        break;
                }
            });
        }
        else if ( arg instanceof GameState) {
            Platform.runLater(() -> {
                switch ( (GameState)arg ) {
                    case CARDS_SPREADING:
                        stateTitle.setText("CARDS SPREADING");
                        toolTip.setText("Please wait...");
                        break;
                    case DEALER_CHOOSING:
                        stateTitle.setText("DEALER CHOOSING");
                        toolTip.setText("Please wait...");
                        break;
                    case DEALER_CHOSEN:
                        stateTitle.setText("DEALER CHOSEN");
                        toolTip.setText("Dealer is " + gameModel.getPlayerHandler().
                                getPlayerName(gameModel.getPlayerHandler().getDealer()));
                        break;
                    case CARDS_DEALING:
                        stateTitle.setText("CARDS DEALING");
                        toolTip.setText("Please wait...");
                        break;
                    case PETIT_SEC_DETECTED:
                        stateTitle.setText("PETIT SEC DETECTED");
                        toolTip.setText("Please click on \"RE-DEAL\" button");
                        //TODO : NAVIGATION SYSTEM
                        break;
                    case BID_CHOOSING:
                        stateTitle.setText("BID CHOOSING");
                        toolTip.setText("Please wait...");
                        break;
                    case BID_CHOSEN:
                        handleBidChosen();
                        break;
                    case ECART_CONSTITUTING:
                        stateTitle.setText("ECART CONSTITUTING");
                        toolTip.setText("You are the taker");
                        break;
                    case ECART_CONSTITUTED:
                        stateTitle.setText("ECART CONSTITUTED");
                        toolTip.setText("Game is finished\nYou can quit game");
                        //TODO : NAVIGATION SYSTEM
                        break;
                    default:
                        break;
                }
            });
        }
    }


    /**
     * This method handles actions of "bid chosen" screen
     * @since   v0.10
     */
    private void handleBidChosen() {
        stateTitle.setText("BID HAS BEEN CHOSEN");
        switch (gameModel.getOurPlayer().getBidChosen()) {
            case Small:
                toolTip.setText("Small\nYou can constitute your ecart");
                break;
            case Guard:
                toolTip.setText("Guard\nYou can constitute your ecart");
                break;
            case GuardWithoutTheKitty:
                toolTip.setText("Guard Without The Kitty\nGame is finished\nYou can quit game");
                break;
            case GuardAgainstTheKitty:
                toolTip.setText("Guard Against The Kitty\nGame is finished\nYou can quit game");
                break;
            case Pass:
                toolTip.setText("You have chosen to Pass\nPlease click on \"RE-DEAL\" button");
                break;
            //TODO : NAVIGATION SYSTEM
        }
    }


    /**
     * This method is called by @update if the update type is @ADD_CARD
     * It create a new ViewCard and add it to the corresponding javaFX group
     * @since   v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     */
    private void addNewCard(CardUpdate cardUpdate) throws NullViewCardException {
        if (getViewCardFromCard(cardUpdate.getCard()) != null)
        {
            throw new NullViewCardException(cardUpdate, false);
        }
        new ViewCard(cardUpdate.getCard(), this,
                getGroupFromCardGroup(cardUpdate.getCardGroup()));
        flipBackCard(cardUpdate.getCard(), 0);
    }

    /**
     * This method is called by @update if the update type is @FLIP_CARD
     * It apply a 180° on the 3D Card with a transition to show its other face
     * @since   v0.6
     * @param   card     the cardUpdate object.
     */
    private void flipBackCard(@NotNull Card card, int animationTime) {

        if (animationTime<5)
            animationTime = 5; //To prevent keyFrames from mixing

        ViewCard viewCard = getViewCardFromCard(card);

        if (viewCard != null && (viewCard.isShown() != viewCard.getModelCard().isShown()) ) {

            viewCard.setShown(!viewCard.isShown());
            Timeline timeline = new Timeline();
            KeyValue initialTranslate;
            KeyValue finalTranslate;
            CardGroup cardGroup = getCardGroupFromGroup(viewCardToGroup.get(viewCard));

            DoubleProperty cardY = viewCard.getTransformations().getTranslate().yProperty();
            DoubleProperty cardZ = viewCard.getTransformations().getTranslate().zProperty();
            DoubleProperty cardAngle = viewCard.getTransformations().getRotateY().angleProperty();

            if (cardGroup instanceof Hand) {
                initialTranslate = new KeyValue(cardY, 0);
                finalTranslate = new KeyValue(cardY, 200);
            } else {
                initialTranslate = new KeyValue(cardY, 0);
                finalTranslate = new KeyValue(cardY, 0);
            }

            double initialRotateY, finalRotateY;
            initialRotateY = viewCard.getTransformations().getRotateY().getAngle();

            if (viewCard.getTransformations().getRotateY().getAngle() >= 180)
                finalRotateY = 0;
            else
                finalRotateY = 180;

            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, initialTranslate),
                    new KeyFrame(new Duration(animationTime*0.2) , finalTranslate),
                    new KeyFrame(new Duration(animationTime*0.2) , new KeyValue( cardZ, 0) ),
                    new KeyFrame(new Duration(animationTime*0.4), new KeyValue( cardZ, -100) ),
                    new KeyFrame(new Duration(animationTime*0.4), new KeyValue( cardAngle, initialRotateY) ),
                    new KeyFrame(new Duration(animationTime*0.6), new KeyValue( cardAngle, finalRotateY) ),
                    new KeyFrame(new Duration(animationTime*0.6), new KeyValue( cardZ, -100) ),
                    new KeyFrame(new Duration(animationTime*0.8), new KeyValue( cardZ, 0) ),
                    new KeyFrame(new Duration(animationTime*0.8), finalTranslate),
                    new KeyFrame(new Duration(animationTime), initialTranslate)
            );
            timeline.play();
        }
    }


    /**
     * This method is called by @update if the update type is @MOVE_CARD_BETWEEN_GROUPS
     * It move a ViewCard associated with a model Card to another JavaFX Group
     * @since   v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     */
    private void changeCardGroup(CardUpdate cardUpdate, int animationTime) throws NullViewCardException {

        if (animationTime<5)
            animationTime = 5; //To prevent keyFrames from mixing

        ViewCard viewCard = getViewCardFromCard(cardUpdate.getCard());

        if (viewCard == null)
            throw new NullViewCardException(cardUpdate, true);
        else {
            Group oldGroup = viewCardToGroup.get(viewCard);
            Group newGroup = getGroupFromCardGroup(cardUpdate.getCardGroup());
            oldGroup.getChildren().remove(viewCard);
            viewCardToGroup.replace(viewCard, newGroup);
            newGroup.getChildren().add(viewCard);

            Point3D viewCardPosition = getCardDefaultPosition(viewCard);

            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(new Duration(animationTime), new KeyValue(viewCard.translateXProperty(),
                            viewCardPosition.getX())),
                    new KeyFrame(new Duration(animationTime), new KeyValue(viewCard.translateYProperty(),
                            viewCardPosition.getY())),
                    new KeyFrame(new Duration(animationTime), new KeyValue(viewCard.translateZProperty(),
                            viewCardPosition.getZ())),
                    new KeyFrame(new Duration(animationTime), new KeyValue(viewCard.rotateProperty(),
                            getCardDefaultRotation(viewCard)))
            );
            timeline.play();
        }
    }


    /**
     * This method is called by @update if the update type is @MOVE_CARD_BETWEEN_GROUPS
     * It move a ViewCard associated with a model Card to another JavaFX Group
     * @since   v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     * @param   position       the destination position
     */
    private void changeCardGroup(CardUpdate cardUpdate, Point3D position, int animationTime)
            throws NullViewCardException {

        if (animationTime<5)
            animationTime = 5; //To prevent keyFrames from mixing

        ViewCard viewCard = getViewCardFromCard(cardUpdate.getCard());
        if (viewCard == null)
        {
            throw new NullViewCardException(cardUpdate, true);
        }

        Group oldGroup = viewCardToGroup.get(viewCard);
        Group newGroup = getGroupFromCardGroup(cardUpdate.getCardGroup());
        oldGroup.getChildren().remove(viewCard);
        viewCardToGroup.replace(viewCard, newGroup);
        newGroup.getChildren().add(viewCard);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(new Duration(animationTime), new KeyValue(viewCard.translateXProperty(), position.getX())),
                new KeyFrame(new Duration(animationTime), new KeyValue(viewCard.translateYProperty(), position.getY())),
                new KeyFrame(new Duration(animationTime), new KeyValue(viewCard.translateZProperty(), position.getZ())),
                new KeyFrame(new Duration(animationTime), new KeyValue(viewCard.rotateProperty(), getCardDefaultRotation(viewCard)))
        );
        timeline.play();
    }


    /**
     * This method is called by @update if the update type is @REMOVE_CARD_FROM_GROUP
     * It remove a ViewCard from its actual JavaFX group
     * and place it to the default group that is @root3D
     * @since   v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     */
    private void removeCardFromGroup(CardUpdate cardUpdate) throws NullViewCardException {
        changeCardGroup(cardUpdate, 800);
    }


    /**
     * This method is called by @update if the update type is @DELETE_CARD
     * It delete the ViewCard associated to a model Card from the View
     * @since   v0.6
     *@param   card     the viewCard related modelCard
     */
    private void removeCard(Card card) throws NullViewCardException {
        ViewCard viewCard = getViewCardFromCard(card);
        viewCardToGroup.get(viewCard).getChildren().remove(viewCard);
        viewCardToGroup.remove(viewCard);
    }


    /**
     * This method is called by @update if the update type is @SHUFFLE_CARDS
     * It shuffles all the cards of a given group
     * @since v0.7.1
     * @param   cardGroup     the cardUpdate object.
     */
    private void shuffleDeck(CardGroup cardGroup) throws NullViewCardException {
        //TODO : ADD SHUFFLING CARDS ANIMATION
    }


    /**
     * This method is called by @update if the update type is @CUT_DECK
     * It cut the deck of a given group in two
     * @since v0.7.1
     * @param   cardGroup     the cardUpdate object.
     */
    private void cutDeck(CardGroup cardGroup) throws NullViewCardException {
        //TODO : ADD CUTTING DECK ANIMATION
    }


    /**
     * This method is called by @update if the update type is @SORT_DECK
     * It sorts a deck of card following cards priority from Tarot's rules
     * @since   v0.8.1
     * @param   cardGroup     the cardUpdate object.
     */
    private void sortDeck(CardGroup cardGroup) throws NullViewCardException {
        //TODO : FIX SORTING DECK ANIMATION - CONFLICT WITH MOUSE SELECTION
        cardGroup.forEach( (card) -> {
            ViewCard viewCard = getViewCardFromCard(card);

            if (viewCard != null) {

                Point3D newPosition = new Point3D(0, 0, 0);
                Group group = viewCardToGroup.get(viewCard);
                final int CARD_INDEX = cardGroup.indexOf(viewCard.getModelCard());

                switch (gameModel.getPlayerHandler().getPlayerCardinalPoint( (Hand)getCardGroupFromGroup(group) ) )
                {
                    case North:
                        newPosition = new Point3D( TABLE_SIZE - HAND_MARGIN_LEFT - ViewCard.getWidth()
                                - CARD_INDEX*MARGIN_BETWEEN_HAND_CARDS,
                                HAND_MARGIN_UP, CARD_INDEX+ViewCard.getDepth());
                        break;
                    case West:
                        newPosition = new Point3D((ViewCard.getHeight() - ViewCard.getWidth())/2
                                + HAND_MARGIN_UP, (-1)*((ViewCard.getHeight() - ViewCard.getWidth())/2)
                                + HAND_MARGIN_LEFT + CARD_INDEX*MARGIN_BETWEEN_HAND_CARDS, CARD_INDEX+ViewCard.getDepth());
                        break;
                    case South:
                        newPosition = new Point3D( HAND_MARGIN_LEFT + CARD_INDEX*MARGIN_BETWEEN_HAND_CARDS,
                                TABLE_SIZE-HAND_MARGIN_UP-ViewCard.getHeight(),CARD_INDEX+ViewCard.getDepth());
                        break;
                    case East:
                        newPosition = new Point3D(TABLE_SIZE - ViewCard.getWidth() -((ViewCard.getHeight()
                                - ViewCard.getWidth())/2) - HAND_MARGIN_UP, TABLE_SIZE - HAND_MARGIN_LEFT
                                - ViewCard.getWidth() - ((ViewCard.getHeight() - ViewCard.getWidth())/2)
                                - CARD_INDEX*MARGIN_BETWEEN_HAND_CARDS, CARD_INDEX+ViewCard.getDepth());
                        break;
                }

                Timeline timeline = new Timeline();
                timeline.getKeyFrames().addAll(
                        new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateXProperty(), newPosition.getX())),
                        new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateYProperty(), newPosition.getY())),
                        new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateZProperty(), newPosition.getZ())),
                        new KeyFrame(new Duration(1000), new KeyValue(viewCard.rotateProperty(), getCardDefaultRotation(viewCard)))
                );
                timeline.play();
                viewCard.toFront();
            }
        });

    }


    /**
     * This method is called by @update if the update type is @SPREAD_CARDS
     * It spreads the card of a group on the table
     * @since v0.7.1
     * @param   cardGroup     the cardUpdate object.
     */
    private void spreadAllCards(CardGroup cardGroup) throws NullViewCardException {
        int nbCardInRow = (int)((TABLE_SIZE - MARGIN_TABLE*2)/(ViewCard.getWidth()+MARGIN_CARDS));
        int i = 0;
        int j = 0;
        for(Card card : cardGroup)
        {
            Point3D position = new Point3D(MARGIN_TABLE + i*(MARGIN_CARDS+ViewCard.getWidth()), MARGIN_TABLE
                    + j*(MARGIN_CARDS+ViewCard.getHeight()), -ViewCard.getDepth());
            changeCardGroup(new CardUpdate(CardUpdateType.MOVE_CARD_BETWEEN_GROUPS, card, null), position, 800);
            i++;
            if (i>nbCardInRow-1)
            {
                i=0;
                j++;
            }
        }
    }

    /**
     * This method is called by @update if the update type is @GATHER_CARDS
     * It gather all cards of all groups to a target cardGroup
     * @since   v0.10
     * @param   cardGroup     the gather target
     */
    private void gatherAllCards(CardGroup cardGroup) throws NullViewCardException {

        for(Card card : cardGroup )
        {
            changeCardGroup(new CardUpdate(CardUpdateType.MOVE_CARD_BETWEEN_GROUPS, card, null),
                    INITIAL_DECK_POSITION, 1000);
        }
    }


    /**
     * This return the number of viewCard node in a Group
     * @since   v0.6.5
     * @param   group    the Group object.
     * @return the number of viewCard node in a Group
     */
    private int getNbViewCard(Group group) {
        int nb = 0;
        for (Node node : group.getChildren())
        {
            if (node instanceof ViewCard)
                nb++;
        }
        return nb;
    }


    /**
     * This method return the associated ViewCard
     * of the actual scene of a Card model object
     * If the ViewCard doesn't exist it return null
     * @since   v0.6
     *
     * @param   card     the model card object
     * @return  the associated ViewCard of a modelCard
     */
    private ViewCard getViewCardFromCard(Card card) {
        for (Map.Entry<ViewCard, Group> entry : viewCardToGroup.entrySet())
        {
            if (entry.getKey().getModelCard() == card)
            {
                return entry.getKey();
            }
        }
        return null;
    }


    /**
     * This method return the associated JavaFX Group of a CardGroup
     * Return   the @root3D group if no specific group exist
     * @since   v0.6
     *
     * @param   cardGroup     the cardGroup object
     * @return  the associated JavaFX Group of a CardGroup
     */
    public Group getGroupFromCardGroup(CardGroup cardGroup) {
        updateCardGroupToGroup();
        if (cardGroupToGroup.containsKey(cardGroup))
            return cardGroupToGroup.get(cardGroup);
        else
            return root3D;
    }


    /**
     * This method return the associated CardGroup of a JavaFx Group
     * Return the null if no specific group exist
     * @since   v0.7
     *
     * @param   viewGroup     the viewGroup object
     * @return  the associated CardGroup of a JavaFx Group
     */
    public CardGroup getCardGroupFromGroup(Group viewGroup) {
        updateCardGroupToGroup();
        for (Map.Entry<CardGroup, Group> entry : cardGroupToGroup.entrySet())
        {
            if (entry.getValue() == viewGroup)
            {
                return entry.getKey();
            }
        }
        return null;
    }


    /**
     * This method return the correct default position
     * of a card depending on the group
     * @since   v0.7
     * @param   viewCard    the viewCard object
     * @return  the default position of a card
     */
    public Point3D getCardDefaultPosition(@NotNull ViewCard viewCard) {

        Point3D point3D = new Point3D(0, 0, 0);
        Group group = viewCardToGroup.get(viewCard);
        if (viewCardToGroup.get(viewCard) == hands[0] || viewCardToGroup.get(viewCard) == hands[1]
                || viewCardToGroup.get(viewCard) == hands[2] || viewCardToGroup.get(viewCard) == hands[3])
        {
            switch (gameModel.getPlayerHandler().getPlayerCardinalPoint((Hand) getCardGroupFromGroup(group)))
            {
                case North:
                    point3D = new Point3D(
                            TABLE_SIZE - HAND_MARGIN_LEFT - ViewCard.getWidth()
                                    - (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS,
                            HAND_MARGIN_UP, (-1)*ViewCard.getDepth());
                    break;
                case West:
                    point3D = new Point3D((ViewCard.getHeight() - ViewCard.getWidth())/2 + HAND_MARGIN_UP,
                            (-1)*((ViewCard.getHeight() - ViewCard.getWidth())/2) + HAND_MARGIN_LEFT
                                    + (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS,
                            (-1)*ViewCard.getDepth());
                    break;
                case South:
                    point3D = new Point3D(HAND_MARGIN_LEFT + (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS,
                            TABLE_SIZE-HAND_MARGIN_UP-ViewCard.getHeight(),(-1)*ViewCard.getDepth());
                    break;
                case East:
                    point3D = new Point3D(TABLE_SIZE - ViewCard.getWidth() -((ViewCard.getHeight()
                            - ViewCard.getWidth())/2) - HAND_MARGIN_UP, TABLE_SIZE - HAND_MARGIN_LEFT
                            - ViewCard.getWidth() - ((ViewCard.getHeight() - ViewCard.getWidth())/2)
                            - (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS, (-1)*ViewCard.getDepth());
                    break;
            }
        }
        else if (viewCardToGroup.get(viewCard) == pickedCardDeck) {
            point3D = new Point3D(PICKED_CARD_DECK_POSITION.getX() + (ViewCard.getWidth() + MARGIN_CARDS) * getNbViewCard(pickedCardDeck),
                    PICKED_CARD_DECK_POSITION.getY(), PICKED_CARD_DECK_POSITION.getZ() - ViewCard.getDepth());
        }
        else if (viewCardToGroup.get(viewCard) == talon) {
            point3D = new Point3D(TALON_POSITION.getX(), TALON_POSITION.getY(), TALON_POSITION.getZ()
                    - ViewCard.getDepth()*(getNbViewCard(talon)));
        }
        else if (viewCardToGroup.get(viewCard) == wholeCardsDeck) {
            point3D = new Point3D(INITIAL_DECK_POSITION.getX(), INITIAL_DECK_POSITION.getY(),
                    INITIAL_DECK_POSITION.getZ() -ViewCard.getDepth()*(getNbViewCard(wholeCardsDeck)));
        }
        else if (viewCardToGroup.get(viewCard) == root3D) {
            point3D = new Point3D(0,0, -ViewCard.getDepth());
        }
        return point3D;
    }


    /**
     * This method return the correct default rotation
     * of a card depending on the group
     * @since   v0.7
     * @param   viewCard    the viewCard object
     * @return  the default rotation of a card
     */
    public double getCardDefaultRotation(@NotNull ViewCard viewCard) {
        int angle = 0;
        if (viewCardToGroup.get(viewCard) == hands[0] || viewCardToGroup.get(viewCard) == hands[1]
                || viewCardToGroup.get(viewCard) == hands[2] || viewCardToGroup.get(viewCard) == hands[3])
        {
            switch (gameModel.getPlayerHandler().getPlayerCardinalPoint((Hand)
                    getCardGroupFromGroup(viewCardToGroup.get(viewCard))))
            {
                case North:
                    angle = 180;
                    break;
                case West:
                    angle = 90;
                    break;
                case South:
                    angle = 0;
                    break;
                case East:
                    angle = 270;
                    break;
            }
        }
        else if (viewCardToGroup.get(viewCard) == talon) {
            angle = 0;
        }
        else if (viewCardToGroup.get(viewCard) == wholeCardsDeck) {
            angle = 0;
        }
        else if (viewCardToGroup.get(viewCard) == pickedCardDeck) {
            angle = 0;
        }
        else if (viewCardToGroup.get(viewCard) == root3D) {
            angle = 0;
        }
        return angle;
    }


//GETTERS & SETTERS - no documentation needed

    public GameModel getGameModel() {
        return gameModel;
    }
    public AppPresenter getAppPresenter() {
        return appPresenter;
    }
    public Group getRoot3d()
    {
        return root3D;
    }
    public Group getSouth() {
        return getGroupFromCardGroup(gameModel.getOurPlayer());
    }
    public Group getTalon()
    {
        return talon;
    }
    public Group getWholeCardsDeck() {
        return wholeCardsDeck;
    }
    public HashMap<ViewCard, Group> getViewCardToGroup() {
        return viewCardToGroup;
    }
    public boolean isHandlingCardPicking() {
        return handleCardPicking;
    }

    public Label getToolTip() {
        return toolTip;
    }

    public void setHandleCardPicking(boolean handleCardPicking) {
        this.handleCardPicking = handleCardPicking;
    }
}