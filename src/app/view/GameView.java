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
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.Vec3f;
import exceptions.NullViewCardException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.*;


/**
 * The {@code GameView} class consists in the MVC architecture view
 * @author Alexandre
 * @author Arthur
 * @version v0.11
 * @since v0.2
 *
 * @see Observer
 * @see Scene
 */
public class GameView extends Scene implements Observer {

    private static final float CARPET_SIZE = 2500;
    private static final float MARGIN_TABLE = 130;
    private static final float MARGIN_CARDS = 30;
    private static final float CARPET_DEPTH = 30;
    private static final float HAND_MARGIN_UP = 100;
    private static final float HAND_MARGIN_LEFT = (float)(0.2 *  CARPET_SIZE);
    private static final float MARGIN_BETWEEN_HAND_CARDS = ( CARPET_SIZE-(2*HAND_MARGIN_LEFT))/18;
    private static final Point3D TALON_POSITION = new Point3D(( CARPET_SIZE/2) - (ViewCard.getWidth()/2),
            ( CARPET_SIZE/2)-(ViewCard.getHeight()/2), 0);
    private static final Point3D INITIAL_DECK_POSITION = new Point3D(-350,  CARPET_SIZE/2, -300);
    private static final Point3D PICKED_CARD_DECK_POSITION = new Point3D(MARGIN_TABLE,
            CARPET_SIZE-MARGIN_TABLE-ViewCard.getHeight(), 0);
    private static final Point3D CAMERA_POSITION_1 = new Point3D(CARPET_SIZE/2, 4200, -3800);
    private static final double CAMERA_ROTATION_1 = 35;
    private static final Point3D CAMERA_POSITION_2 = new Point3D(CARPET_SIZE/2, 2600, -2800);
    private static final double CAMERA_ROTATION_2 = 15;


    private GameModel gameModel;
    private AppPresenter appPresenter;
    private ViewCamera camera3D;

    //Groups
    private Group root3D;
    private Group wholeCardsDeck;
    private Group pickedCardDeck;
    private Group talon;
    private Group[] hands = new Group[4];
    private HashMap<CardGroup, Group> cardGroupToGroup;
    private HashMap<ViewCard, Group> viewCardToGroup;

    //GUI elements
    private Label stateTitle;
    private Label toolTip;
    private VBox bidBox;


    /**
     * Constructs a view for a specific root node and with a model and a presenter
     * @since   v0.1
     * @param   root        the root node
     * @param   model       the model it reads
     * @param   controller  the presenter it sends event information
     */
    public GameView(Group root, GameModel model, AppPresenter controller) {
        super(root, 800, 600, false, SceneAntialiasing.BALANCED);


        this.gameModel = model;
        this.appPresenter = controller;
        this.setFill(Color.BLACK);
        model.addObserver(this);

        //=== Create the groups
        root3D = new Group();
        BorderPane rootGUI = new BorderPane();
        HBox boxTop = new HBox();
        VBox boxBottom = new VBox();
        HBox boxLeft = new HBox();
        HBox boxRight = new HBox();
        HBox boxCenter = new HBox();

        boxRight.setAlignment(Pos.CENTER_RIGHT);
        boxLeft.setAlignment(Pos.CENTER_LEFT);
        boxCenter.setAlignment(Pos.CENTER);
        boxTop.setAlignment(Pos.CENTER);
        boxBottom.setAlignment(Pos.CENTER);
        rootGUI.setTop(boxTop);
        rootGUI.setRight(boxRight);
        rootGUI.setBottom(boxBottom);
        rootGUI.setLeft(boxLeft);
        rootGUI.setCenter(boxCenter);
        rootGUI.prefWidthProperty().bind(widthProperty());
        rootGUI.prefHeightProperty().bind(heightProperty());
        rootGUI.setPickOnBounds(false);
        for (Node node: rootGUI.getChildren()) {
            node.setPickOnBounds(false);
            ((Pane)node).setPadding(new Insets(10, 10, 10, 10));
        }

        SubScene subScene3D = new SubScene(root3D, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene3D.widthProperty().bind(widthProperty());
        subScene3D.heightProperty().bind(heightProperty());
        Group background = new Group();
        wholeCardsDeck = new Group();
        pickedCardDeck = new Group();
        talon = new Group();
        viewCardToGroup = new HashMap<>();
        cardGroupToGroup = new HashMap<>();

        for (PlayerHandler.PlayersCardinalPoint cardinalPoint :
                PlayerHandler.PlayersCardinalPoint.values()) { hands[cardinalPoint.ordinal()] = new Group();}
        updateCardGroupToGroup();

        //=== Define the background

        RectangleMesh carpet = new RectangleMesh( CARPET_SIZE,  CARPET_SIZE, CARPET_DEPTH,
                "file:./res/carpet.jpg", 1100, 1100);
        ImageView table = new ImageView("file:./res/table.jpg");
        table.setTranslateZ(CARPET_DEPTH);
        table.setTranslateX(-table.getImage().getWidth()/2 + CARPET_SIZE/2);
        table.setTranslateY(-table.getImage().getHeight()/2 + CARPET_SIZE/2);
        table.setScaleX(2);
        table.setScaleY(2);

        //=== Define the camera

        camera3D = new ViewCamera(true);
        camera3D.moveCamera(CAMERA_POSITION_1, CAMERA_ROTATION_1, 0);

        subScene3D.setCamera(camera3D);

        //=== Define the light

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(getWidth()/2);
        pointLight.setTranslateY(getHeight()/2);
        pointLight.setTranslateZ(-20000);

        //=== Create GUI elements

        stateTitle = new Label();
        stateTitle.setTextFill(Color.WHITE);
        stateTitle.setFont(new Font(25));

        toolTip = new Label();
        toolTip.setTextFill(Color.WHITE);
        toolTip.setFont(new Font(20));

        Button bidSmall = new Button("Small");
        bidSmall.setTextFill(Color.BLACK);
        bidSmall.setMinSize(180, 30);
        bidSmall.setOnAction(event -> {
            appPresenter.transmitUserChoice(1);
            bidBox.setVisible(false);
        });
        bidSmall.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        bidSmall.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        Button bidGuard = new Button("Guard");
        bidGuard.setTextFill(Color.BLACK);
        bidGuard.setMinSize(180, 30);
        bidGuard.setOnAction(event -> {
            appPresenter.transmitUserChoice(2);
            bidBox.setVisible(false);
        });
        bidGuard.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        bidGuard.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        Button bidGuardWithout = new Button("Guard Without The Kitty");
        bidGuardWithout.setTextFill(Color.BLACK);
        bidGuardWithout.setMinSize(180, 30);
        bidGuardWithout.setOnAction(event  -> {
            appPresenter.transmitUserChoice(3);
            bidBox.setVisible(false);
        });
        bidGuardWithout.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        bidGuardWithout.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        Button bidGuardAgainst = new Button("Guard Against The Kitty");
        bidGuardAgainst.setTextFill(Color.BLACK);
        bidGuardAgainst.setMinSize(180, 30);
        bidGuardAgainst.setOnAction(event -> {
            appPresenter.transmitUserChoice(4);
            bidBox.setVisible(false);
        });
        bidGuardAgainst.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        bidGuardAgainst.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        Button bidPass = new Button("Pass");
        bidPass.setTextFill(Color.BLACK);
        bidPass.setMinSize(180, 30);
        bidPass.setOnAction(event -> {
            appPresenter.transmitUserChoice(5);
            bidBox.setVisible(false);
        });
        bidPass.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        bidPass.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );


        bidBox = new VBox(10);
        bidBox.setAlignment(Pos.CENTER);
        bidBox.getChildren().addAll(bidSmall, bidGuard, bidGuardWithout, bidGuardAgainst, bidPass);
        bidBox.setVisible(false);

        //=== Add elements to groups

        background.getChildren().addAll(table, carpet);
        root.getChildren().addAll(subScene3D, rootGUI);
        boxTop.getChildren().add(stateTitle);
        boxBottom.getChildren().addAll(toolTip);
        boxCenter.getChildren().add(bidBox);
        root3D.getChildren().addAll(background, talon, wholeCardsDeck, pickedCardDeck, pointLight);
        for ( Group hand : hands)
            root3D.getChildren().add(hand);
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
                PlayerHandler.PlayersCardinalPoint.values()) {
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
        if (arg instanceof CardUpdate) {

            CardUpdate cardUpdate = (CardUpdate)arg;
            if (cardUpdate.getType() != null) {
                Platform.runLater(() -> {
                    try {
                        switch (cardUpdate.getType()) {
                            case ADD_CARD:
                                addNewCard(cardUpdate);
                                break;
                            case FLIP_CARD:
                                flipViewCard(cardUpdate, 2500);
                                break;
                            case MOVE_CARD_BETWEEN_GROUPS:
                                changeCardGroup(cardUpdate, 1000);
                                break;
                            case REMOVE_CARD_FROM_GROUP:
                                removeCardFromGroup(cardUpdate);
                                break;
                            case DELETE_CARD:
                                removeCard(cardUpdate);
                                break;
                            case SHUFFLE_CARDS:
                                shuffleDeck(cardUpdate);
                                break;
                            case SORT_DECK:
                                sortDeck(cardUpdate);
                                break;
                            case CUT_DECK:
                                cutDeck(cardUpdate);
                                break;
                            case SPREAD_CARDS:
                                spreadAllCards(cardUpdate);
                                break;
                            case GATHER_CARDS:
                                if ( gameModel.getGameState() == GameState.GAME_ENDED) {
                                    stateTitle.setText("GAME ENDING");
                                    toolTip.setText("");
                                }
                                gatherAllCards(cardUpdate);
                                break;
                            default:
                                break;
                        }
                        cardUpdate.waitAnimations(appPresenter);
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
                        toolTip.setText("Please select a card, Dealer will be whose with the weakest card");
                        break;
                    case CHOOSE_BID:
                        bidBox.setVisible(true);
                        toolTip.setText("Please select a bid with the buttons above");
                        break;
                    case UNAUTHORIZED_CARD_CHOICE:
                        toolTip.setText("You can't choose a Trump, a King or Excuse");
                        toolTip.setTextFill(Color.RED);
                        new Timeline(new KeyFrame( Duration.millis(2500), t -> {
                            toolTip.setText("Please select a card");
                            toolTip.setTextFill(Color.WHITE);
                        })).play();
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
                        toolTip.setText("Dealer is " +
                                gameModel.getPlayerHandler().getPlayerName(gameModel.getPlayerHandler().getDealer())
                                + ", Shuffler is " +
                                gameModel.getPlayerHandler().getPlayerName(gameModel.getPlayerHandler().getShuffler())
                                + " and Cutter is " +
                                gameModel.getPlayerHandler().getPlayerName(gameModel.getPlayerHandler().getCutter())
                        );
                        break;
                    case CARDS_SHUFFLING:
                        stateTitle.setText("CARDS SHUFFLING");
                        toolTip.setText("Please wait...");
                        break;
                    case CARDS_CUTTING:
                        stateTitle.setText("CARDS CUTTING");
                        toolTip.setText("Please wait...");
                        break;
                    case CARDS_DEALING:
                        stateTitle.setText("CARDS DEALING");
                        toolTip.setText("Please wait...");
                        break;
                    case PETIT_SEC_DETECTED:
                        stateTitle.setText("PETIT SEC DETECTED");
                        toolTip.setText("Re-dealing...");
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
                        toolTip.setText("You are the taker, please select a card");
                        camera3D.moveCamera(CAMERA_POSITION_2, CAMERA_ROTATION_2, 2000);
                        break;
                    case ECART_CONSTITUTED:
                        stateTitle.setText("ECART CONSTITUTED");
                        toolTip.setText("Game is now finished. You can quit");
                        camera3D.moveCamera(CAMERA_POSITION_1, CAMERA_ROTATION_1, 2000);
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
        stateTitle.setText("BID CHOSEN");
        switch (gameModel.getOurPlayer().getBidChosen()) {
            case Small:
                toolTip.setText("Small. You can constitute your ecart");
                break;
            case Guard:
                toolTip.setText("Guard. You can constitute your ecart");
                break;
            case GuardWithoutTheKitty:
                toolTip.setText("Guard Without The Kitty. Game is now finished. You can quit");
                break;
            case GuardAgainstTheKitty:
                toolTip.setText("Guard Against The Kitty. Game is now finished. You can quit");
                break;
            case Pass:
                toolTip.setText("You have chosen to Pass. Re-dealing...");
                break;
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
        if (getViewCardFromCard(cardUpdate.getCard()) != null) {
            throw new NullViewCardException(cardUpdate, false);
        }
        new ViewCard(cardUpdate.getCard(), this, getGroupFromCardGroup(cardUpdate.getCardGroup()));
        flipViewCard(cardUpdate, 0);
    }

    /**
     * This method is called by @update if the update type is @FLIP_CARD
     * It apply a 180° on the 3D Card with a transition to show its other face
     * @since   v0.6
     * @param   cardUpdate     the cardUpdate object.
     */
    private void flipViewCard(CardUpdate cardUpdate, int animationTime) throws NullViewCardException {
        Stack<Card> cardsStack = new Stack<>();
        if (animationTime<5)
            animationTime = 5;

        if (cardUpdate.getCard() == null)
        {
            cardUpdate.getCardGroup().forEach(cardsStack::push);
        } else {
            cardsStack.push(cardUpdate.getCard());
        }

        while (!cardsStack.empty()) {
            ViewCard viewCard = getViewCardFromCard(cardsStack.lastElement());

            if (viewCard == null) {
                throw new NullViewCardException(new CardUpdate(CardUpdateType.FLIP_CARD,cardsStack.lastElement()),true);
            }
            else if (viewCard.isShown() != viewCard.getModelCard().isShown()) {

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
                        new KeyFrame(new Duration(animationTime * 0.2), finalTranslate),
                        new KeyFrame(new Duration(animationTime * 0.2), new KeyValue(cardZ, 0)),
                        new KeyFrame(new Duration(animationTime * 0.4), new KeyValue(cardZ, -100)),
                        new KeyFrame(new Duration(animationTime * 0.4), new KeyValue(cardAngle, initialRotateY)),
                        new KeyFrame(new Duration(animationTime * 0.6), new KeyValue(cardAngle, finalRotateY)),
                        new KeyFrame(new Duration(animationTime * 0.6), new KeyValue(cardZ, -100)),
                        new KeyFrame(new Duration(animationTime * 0.8), new KeyValue(cardZ, 0)),
                        new KeyFrame(new Duration(animationTime * 0.8), finalTranslate),
                        new KeyFrame(new Duration(animationTime), initialTranslate)
                );
                if (cardsStack.size() == 1) {
                    timeline.setOnFinished(event -> cardUpdate.setAnimationFinished());
                }
                timeline.play();
            }
            else {
                if (cardsStack.size() == 1)
                    cardUpdate.setAnimationFinished();
            }
            cardsStack.pop();
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
        changeCardGroup(cardUpdate, null, animationTime);
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
        if (viewCard == null) {
            throw new NullViewCardException(cardUpdate, true);
        }


        Group oldGroup = viewCardToGroup.get(viewCard);
        Group newGroup = getGroupFromCardGroup(cardUpdate.getCardGroup());
        oldGroup.getChildren().remove(viewCard);
        viewCardToGroup.replace(viewCard, newGroup);
        newGroup.getChildren().add(viewCard);
        if (position == null) {
            position = getCardDefaultPosition(viewCard);
        }

        double tZ = 0.0;
        if (newGroup == pickedCardDeck) {
            tZ = -ViewCard.getDepth() * 200;
        }

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(new Duration(animationTime*0.2), new KeyValue(viewCard.getTransformations().getTranslate().zProperty(), tZ)),
                new KeyFrame(new Duration(animationTime*0.8), new KeyValue(viewCard.translateXProperty(), position.getX())),
                new KeyFrame(new Duration(animationTime*0.8), new KeyValue(viewCard.translateYProperty(), position.getY())),
                new KeyFrame(new Duration(animationTime*0.8), new KeyValue(viewCard.translateZProperty(), position.getZ())),
                new KeyFrame(new Duration(animationTime*0.8), new KeyValue(viewCard.rotateProperty(), getCardDefaultRotation(viewCard).z)),
                new KeyFrame(new Duration(animationTime*0.8), new KeyValue(viewCard.getTransformations().getRotateX().angleProperty(), getCardDefaultRotation(viewCard).x)),
                new KeyFrame(new Duration(animationTime*0.8), new KeyValue(viewCard.getTransformations().getIncline().angleProperty(), getCardDefaultRotation(viewCard).y)),
                new KeyFrame(new Duration(animationTime), new KeyValue(viewCard.getTransformations().getTranslate().zProperty(), 0))
        );
        timeline.setOnFinished(event -> cardUpdate.setAnimationFinished());
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
     *@param   cardUpdate     the viewCard related modelCard
     */
    private void removeCard(CardUpdate cardUpdate) throws NullViewCardException {
        ViewCard viewCard = getViewCardFromCard(cardUpdate.getCard());
        viewCardToGroup.get(viewCard).getChildren().remove(viewCard);
        viewCardToGroup.remove(viewCard);
        cardUpdate.setAnimationFinished();
    }

    /**
     * Reorganize all viewCards of a group with their default positions
     * @since   v0.9.2
     *
     * @param   cardUpdate     the cardUpdate object.
     */
    private void refreshGroupNodesPosition(CardUpdate cardUpdate)
    {
        Group group = getGroupFromCardGroup(cardUpdate.getCardGroup());
        List<ViewCard> originalDeck = new ArrayList<>();
        for (Node node : group.getChildren()) {
            if (node instanceof ViewCard) {
                originalDeck.add((ViewCard)node);
            }
        }
        for (ViewCard viewCard : originalDeck) {
            group.getChildren().remove(viewCard);
        }
        for (ViewCard viewCard : originalDeck) {
            try {
                CardUpdate subUpdate = new CardUpdate(CardUpdateType.MOVE_CARD_BETWEEN_GROUPS, viewCard.getModelCard(), cardUpdate.getCardGroup());
                if (group == hands[2] && originalDeck.size() > 18)
                {
                    changeCardGroup(subUpdate, getCardDefaultPosition(viewCard).subtract((originalDeck.size()-18)*(MARGIN_BETWEEN_HAND_CARDS/2), 0, 0), 1000);
                } else {
                    changeCardGroup(subUpdate, 1000);
                }
                cardUpdate.addSubUpdate(subUpdate);
            } catch (NullViewCardException e) {
                System.err.println(e.toString());
            }
        }
        cardUpdate.setAnimationFinished();
    }

    /**
     * This method is called by @update if the update type is @SHUFFLE_CARDS
     * It shuffles all the cards of a given group
     * @since v0.7.1
     * @param   cardUpdate     the cardUpdate object.
     */
    private void shuffleDeck(CardUpdate cardUpdate) throws NullViewCardException {
        Timeline timeline = new Timeline();
        timeline.setOnFinished(event -> cardUpdate.setAnimationFinished());
        int i = 1;
        int nbViewCards = getNbViewCard(getGroupFromCardGroup(cardUpdate.getCardGroup()));
        for (Card card : cardUpdate.getCardGroup())
        {
            ViewCard viewCard = getViewCardFromCard(card);

            if ( viewCard != null) {
                timeline.getKeyFrames().addAll(
                        new KeyFrame(new Duration(i * 100), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), 0)),
                        new KeyFrame(new Duration(((i + 1) * 100) - 50), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), ViewCard.getWidth() * 2)),
                        new KeyFrame(new Duration((i + 1) * 100), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), 0)),
                        new KeyFrame(new Duration((i + 1) * 100), new KeyValue(viewCard.translateZProperty(), INITIAL_DECK_POSITION.getZ() - (nbViewCards * 2 - i) * ViewCard.getDepth())),
                        new KeyFrame(new Duration((i + 1) * 100 + 1), event -> {
                            Group group = getGroupFromCardGroup(cardUpdate.getCardGroup());
                            group.getChildren().remove(viewCard);
                            group.getChildren().add(0, viewCard);
                        })
                );
                if (i == cardUpdate.getCardGroup().size()) {
                    timeline.getKeyFrames().add(new KeyFrame(new Duration((i + 3) * 100), event -> refreshGroupNodesPosition(cardUpdate)));
                }
                i++;
            }
        }
        timeline.play();
    }


    /**
     * This method is called by @update if the update type is @CUT_DECK
     * It cut the deck of a given group in two
     * @since v0.7.1
     * @param   cardUpdate    the cardUpdate object.
     */
    private void cutDeck(CardUpdate cardUpdate) {
        Group initialGroup;
        List<ViewCard> group1 = new ArrayList<>();
        List<ViewCard> group2 = new ArrayList<>();
        int i = 0;
        int cutCardIndex = getGroupFromCardGroup(cardUpdate.getCardGroup()).getChildren().indexOf(getViewCardFromCard(cardUpdate.getCard()));
        initialGroup = getGroupFromCardGroup(cardUpdate.getCardGroup());
        if (initialGroup == talon || initialGroup == wholeCardsDeck) {
            while (i<initialGroup.getChildren().size()) {
                if (initialGroup.getChildren().get(i) instanceof ViewCard) {
                    if (i <= cutCardIndex) {
                        group1.add((ViewCard)initialGroup.getChildren().get(i));
                    }
                    else {
                        group2.add((ViewCard)initialGroup.getChildren().get(i));
                    }
                }
                i++;
            }
            Timeline timeline = new Timeline();
            for (ViewCard viewCard : group1) {
                timeline.getKeyFrames().add(new KeyFrame(new Duration(1000), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), -ViewCard.getWidth())));
                timeline.getKeyFrames().add(new KeyFrame(new Duration(2000), new KeyValue(viewCard.translateZProperty(), viewCard.getTranslateZ() - group1.size()*ViewCard.getDepth())));
                timeline.getKeyFrames().add(new KeyFrame(new Duration(3000), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), 0)));
            }
            for (ViewCard viewCard : group2) {
                timeline.getKeyFrames().add(new KeyFrame(new Duration(1000), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), ViewCard.getWidth())));
                timeline.getKeyFrames().add(new KeyFrame(new Duration(2000), new KeyValue(viewCard.translateZProperty(), viewCard.getTranslateZ() + group2.size()*ViewCard.getDepth())));
                timeline.getKeyFrames().add(new KeyFrame(new Duration(3000), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), 0)));
            }
            timeline.setOnFinished(event -> cardUpdate.setAnimationFinished());
            timeline.play();
        }
    }


    /**
     * This method is called by @update if the update type is @SORT_DECK
     * It sorts a deck of card following cards priority from Tarot's rules
     * @since   v0.8.1
     * @param   cardUpdate the cardUpdate object.
     */
    public void sortDeck(CardUpdate cardUpdate) throws NullViewCardException {
        CardGroup cardGroup = cardUpdate.getCardGroup();
        Group group = getGroupFromCardGroup(cardGroup);
        List<ViewCard> originalDeck = new ArrayList<>();
        for (Node node : group.getChildren()) {
            if (node instanceof ViewCard) {
                originalDeck.add((ViewCard) node);
            }
        }
        for (ViewCard viewCard : originalDeck) {
            group.getChildren().remove(viewCard);
        }
        for (Card card : cardGroup) {
            group.getChildren().add(getViewCardFromCard(card));
        }
        refreshGroupNodesPosition(cardUpdate);
    }


    /**
     * This method is called by @update if the update type is @SPREAD_CARDS
     * It spreads the card of a group on the table
     * @since v0.7.1
     * @param   cardUpdate     the cardUpdate object.
     */
    private void spreadAllCards(CardUpdate cardUpdate) throws NullViewCardException {
        int nbCardInRow = (int)(( CARPET_SIZE - MARGIN_TABLE*2)/(ViewCard.getWidth()+MARGIN_CARDS));
        int i = 0;
        int j = 0;
        for(Card card : cardUpdate.getCardGroup()) {
            Point3D position = new Point3D(MARGIN_TABLE + i*(MARGIN_CARDS+ViewCard.getWidth()), MARGIN_TABLE
                    + j*(MARGIN_CARDS+ViewCard.getHeight()), -ViewCard.getDepth());
            CardUpdate newCardUpdate = new CardUpdate(CardUpdateType.MOVE_CARD_BETWEEN_GROUPS, card, null);
            cardUpdate.addSubUpdate(newCardUpdate);
            changeCardGroup(newCardUpdate, position, 1000);
            i++;
            if (i>nbCardInRow-1)
            {
                i=0;
                j++;
            }
        }
        cardUpdate.setAnimationFinished();
    }

    /**
     * This method is called by @update if the update type is @GATHER_CARDS
     * It gather all cards of all groups to a target cardGroup
     * @since   v0.10
     * @param   cardUpdate     the cardUpdateObject
     */
    private void gatherAllCards(CardUpdate cardUpdate) throws NullViewCardException {

        for(Card card : cardUpdate.getCardGroup() ) {
            CardUpdate subUpdate =  new CardUpdate(CardUpdateType.MOVE_CARD_BETWEEN_GROUPS, card, cardUpdate.getCardGroup());
            cardUpdate.addSubUpdate(subUpdate);
            changeCardGroup(subUpdate, 1000);
        }
        cardUpdate.setAnimationFinished();
    }


    /**
     * This return the number of viewCard node in a Group
     * @since   v0.6.5
     * @param   group    the Group object.
     * @return the number of viewCard node in a Group
     */
    private int getNbViewCard(Group group) {
        int nb = 0;
        for (Node node : group.getChildren()) {
            if (node instanceof ViewCard) {
                nb++;
            }
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
        for (Map.Entry<ViewCard, Group> entry : viewCardToGroup.entrySet()) {
            if (entry.getKey().getModelCard() == card) {
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
        if (cardGroupToGroup.containsKey(cardGroup)) {
            return cardGroupToGroup.get(cardGroup);
        }
        else {
            return root3D;
        }
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
        for (Map.Entry<CardGroup, Group> entry : cardGroupToGroup.entrySet()) {
            if (entry.getValue() == viewGroup) {
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
    Point3D getCardDefaultPosition(@NotNull ViewCard viewCard) {

        Point3D point3D = new Point3D(0, 0, 0);
        Group group = viewCardToGroup.get(viewCard);
        if (viewCardToGroup.get(viewCard) == hands[0] || viewCardToGroup.get(viewCard) == hands[1]
                || viewCardToGroup.get(viewCard) == hands[2] || viewCardToGroup.get(viewCard) == hands[3])
        {
            switch (gameModel.getPlayerHandler().getPlayerCardinalPoint((Hand) getCardGroupFromGroup(group))) {
                case North:
                    point3D = new Point3D(
                            CARPET_SIZE - HAND_MARGIN_LEFT - ViewCard.getWidth()
                                    - (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS,
                            HAND_MARGIN_UP, (-1.5)*ViewCard.getDepth());
                    break;
                case West:
                    point3D = new Point3D((ViewCard.getHeight() - ViewCard.getWidth())/2 + HAND_MARGIN_UP,
                            (-1)*((ViewCard.getHeight() - ViewCard.getWidth())/2) + HAND_MARGIN_LEFT
                                    + (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS,
                            (-1.5)*ViewCard.getDepth());
                    break;
                case South:
                    point3D = new Point3D(HAND_MARGIN_LEFT + (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS,
                            CARPET_SIZE-HAND_MARGIN_UP-ViewCard.getHeight(),(-1.5)*ViewCard.getDepth());
                    break;
                case East:
                    point3D = new Point3D( CARPET_SIZE - ViewCard.getWidth() -((ViewCard.getHeight()
                            - ViewCard.getWidth())/2) - HAND_MARGIN_UP,  CARPET_SIZE - HAND_MARGIN_LEFT
                            - ViewCard.getWidth() - ((ViewCard.getHeight() - ViewCard.getWidth())/2)
                            - (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS, (-1.5)*ViewCard.getDepth());
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
     * @return  the default z hard rotation and the default transforrm y rotation
     */
    public Vec3d getCardDefaultRotation(@NotNull ViewCard viewCard) {
        Vec3d rotation = new Vec3d(0, 0, 0);
        if (viewCardToGroup.get(viewCard) == hands[0] || viewCardToGroup.get(viewCard) == hands[1]
                || viewCardToGroup.get(viewCard) == hands[2] || viewCardToGroup.get(viewCard) == hands[3])
        {
            double yAngle = -(Math.asin(ViewCard.getDepth() / (ViewCard.getWidth() - MARGIN_BETWEEN_HAND_CARDS)))*(180/Math.PI);
            switch (gameModel.getPlayerHandler().getPlayerCardinalPoint((Hand)
                    getCardGroupFromGroup(viewCardToGroup.get(viewCard))))
            {
                case North:
                    rotation.set(0,yAngle,180);
                    break;
                case West:
                    rotation.set(0,yAngle,90);
                    break;
                case South:
                    rotation.set(0,yAngle,0);
                    break;
                case East:
                    rotation.set(0,yAngle,270);
                    break;
            }
        }
        return rotation;
    }


    //GETTERS - no documentation needed

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
    public Label getToolTip() {
        return toolTip;
    }
}