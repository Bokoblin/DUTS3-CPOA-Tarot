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
import static app.model.PlayerHandler.PlayersCardinalPoint.*;
import app.presenter.AppPresenter;
import com.sun.istack.internal.NotNull;
import exceptions.NullViewCardException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.*;


/**
 * The {@code AppView} class consists in the MVC architecture view
 * @author Alexandre
 * @author Arthur
 * @version v0.8
 * @since v0.2
 *
 * @see Observer
 * @see Scene
 */
public class AppView extends Scene implements Observer{

    private static final float TABLE_SIZE = 2000;
    private static final float TABLE_DEPTH = 182;
    private static final float HAND_MARGIN_UP = 100;
    private static final float HAND_MARGIN_LEFT = (float)(0.2 * TABLE_SIZE);
    private static final float MARGIN_BETWEEN_HAND_CARDS = (TABLE_SIZE-(2*HAND_MARGIN_LEFT))/18;
    private static final Point3D TALON_POSITION = new Point3D((TABLE_SIZE/2) - (ViewCard.getCardWidth()/2),
            (TABLE_SIZE/2)-(ViewCard.getCardHeight()/2), 0);
    private static final Point3D INITIAL_DECK_POSITION = new Point3D(-300, TABLE_SIZE/2, -200);

    private GameModel gameModel;
    private AppPresenter appPresenter;
    private Group root3d;
    private Group rootGUI;
    private Group background;
    private Group initialDeck;
    private Group pickedCardDeck;
    private Group talon;
    private Group[] hands = new Group[4];
    private HashMap<CardGroup, Group> cardGroupToGroup;
    private HashMap<ViewCard, Group> viewCardToGroup;


    /**
     * Constructs a view for a specific root node and with a model and a presenter
     * @since   v0.1
     * @param   model       the model it reads
     * @param   controller  the presenter it sends event information
     */
    public AppView(Group root, GameModel model, AppPresenter controller) {
        super(root, 800, 600, true, SceneAntialiasing.DISABLED);

        this.gameModel = model;
        this.appPresenter = controller;
        model.addObserver(this);

        //Create the groups
        root3d = new Group();
        rootGUI = new Group();
        background = new Group();
        initialDeck = new Group();
        pickedCardDeck = new Group();
        talon = new Group();
        viewCardToGroup = new HashMap<>();
        cardGroupToGroup = new HashMap<>();
        root.getChildren().addAll(root3d, rootGUI);
        root3d.getChildren().addAll(background, talon, initialDeck, pickedCardDeck);

        for (PlayerHandler.PlayersCardinalPoint playersCardinalPoint :
                PlayerHandler.PlayersCardinalPoint.values()) {
            hands[playersCardinalPoint.ordinal()] = new Group();
            root3d.getChildren().add(hands[playersCardinalPoint.ordinal()]);
        }
        updateCardGroupToGroup();


        //Create the scene objects
        RectangleMesh table = new RectangleMesh(TABLE_SIZE, TABLE_SIZE, TABLE_DEPTH,
                "file:./res/table.jpg", 1100, 1100);
        background.getChildren().add(table);

        //Define the camera
        this.setCamera(new ViewCamera(true));
        this.getViewCamera().setTranslateX(1000);
        this.getViewCamera().setTranslateY(3300);
        this.getViewCamera().setTranslateZ(-2900);
        getViewCamera().getTransformations().getRotateX().setAngle(35);

        //Define the light
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(getWidth()/2);
        pointLight.setTranslateY(getHeight()/2);
        pointLight.setTranslateZ(-20000);
        root3d.getChildren().add(pointLight);


        //=== EVENTS

        this.setOnKeyPressed(keyEvent -> {
            root3d.setRotationAxis(Rotate.Z_AXIS);
            switch (keyEvent.getCode())
            {
                case D:
                    root3d.setRotate(root3d.getRotate()-1);
                    break;
                case Q:
                    root3d.setRotate(root3d.getRotate()+1);
                    break;
                default:
                    break;
            }
        });

        this.setOnMouseClicked(event -> gameModel.getPlayerHandler().
                getPlayer(South).forEach((c) -> {
            try {
                c.setShown(!c.isShown());
                flipBackCard(c);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }));

    }


    /**
     * Because of the keys of the @cardGroupToGroup change,
     * whe have to update this HashMap before search keys on it.
     * @since v0.7
     *
     */
    public void updateCardGroupToGroup()
    {
        for (PlayerHandler.PlayersCardinalPoint playersCardinalPoint :
                PlayerHandler.PlayersCardinalPoint.values())
        {
            CardGroup cardGroup = gameModel.getPlayerHandler().getPlayer(playersCardinalPoint);
            cardGroupToGroup.put(cardGroup, hands[playersCardinalPoint.ordinal()]);
        }
        cardGroupToGroup.put(gameModel.getTalon(), talon);
        cardGroupToGroup.put(gameModel.getInitialDeck(), initialDeck);
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
                try {
                    switch (cardUpdate.getType()) {
                        case ADD_CARD:
                            addNewCard(cardUpdate);
                            break;
                        case FLIP_CARD:
                            if (cardUpdate.getCard() == null) {
                                for (Card c : cardUpdate.getCardGroup())
                                    flipBackCard(c);
                            } else
                                flipBackCard(cardUpdate.getCard());
                            break;
                        case MOVE_CARD_BETWEEN_GROUPS:
                            changeCardGroup(cardUpdate);
                            break;
                        case REMOVE_CARD_FROM_GROUP:
                            removeCardFromGroup(cardUpdate);
                            break;
                        case DELETE_CARD:
                            removeCard(cardUpdate);
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
                        default:
                            break;
                    }
                } catch (NullViewCardException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        else if ( arg instanceof ViewActionExpected) {
            switch (((ViewActionExpected)arg))
            {
                case PICK_CARD:
                    handleCardPicking();
                    break;
                case CHOOSE_ECART_CARD:
                    handleCardChoosing();
                    break;
                case CHOOSE_BID:
                    handleBidChoosing();
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * This method handles click event for selecting a card
     * that allows dealer choosing
     * @since   v0.8
     */
    private void handleCardPicking() {
        //TODO : CARD PICKING EVENT
        try {
            appPresenter.transmitUserChoice(new Random().nextInt(78));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method handles click event for selecting a bid
     * @since   v0.8
     */
    private void handleBidChoosing() {
        //TODO : BIDS CHOOSING EVENT
        try {
            appPresenter.transmitUserChoice(1 + (new Random().nextInt(5)) );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method handles click event for selecting a card
     * that allows Ecart Constitution
     * @since   v0.8
     */
    private void handleCardChoosing() {
        //TODO : CARD CHOOSING FOR ECART CONSTITUTING
        try {
            appPresenter.transmitUserChoice(new Random().nextInt(gameModel.getPlayerHandler().
                    getPlayer(PlayerHandler.PlayersCardinalPoint.South).size()));

        } catch (Exception e) {
            e.printStackTrace();
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
        if (getViewCard(cardUpdate.getCard()) != null)
        {
            throw new NullViewCardException(cardUpdate, false);
        }
        new ViewCard(cardUpdate.getCard(), this,
                getGroupFromCardGroup(cardUpdate.getCardGroup()));
        flipBackCard(cardUpdate.getCard());
    }

    /**
     * This method is called by @update if the update type is @FLIP_CARD
     * It apply a 180° on the 3D Card with a transition to show its other face
     * @since   v0.6
     * @param   card     the cardUpdate object.
     */
    private void flipBackCard(@NotNull Card card) throws NullViewCardException {

        ViewCard viewCard = getViewCard(card);

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
            if (viewCard.getTransformations().getRotateY().getAngle() >= 180) {
                finalRotateY = 0;
            }
            else {
                finalRotateY = 180;
            }

            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, initialTranslate),
                    new KeyFrame(new Duration(500) , finalTranslate),
                    new KeyFrame(new Duration(500) , new KeyValue( cardZ, 0) ),
                    new KeyFrame(new Duration(1000), new KeyValue( cardZ, -100) ),
                    new KeyFrame(new Duration(1000), new KeyValue( cardAngle, initialRotateY) ),
                    new KeyFrame(new Duration(1500), new KeyValue( cardAngle, finalRotateY) ),
                    new KeyFrame(new Duration(1500), new KeyValue( cardZ, -100) ),
                    new KeyFrame(new Duration(2000), new KeyValue( cardZ, 0) ),
                    new KeyFrame(new Duration(2000), finalTranslate),
                    new KeyFrame(new Duration(2500), initialTranslate)
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
    private void changeCardGroup(CardUpdate cardUpdate) throws NullViewCardException {
        ViewCard viewCard = getViewCard(cardUpdate.getCard());
        if (viewCard == null)
        {
            throw new NullViewCardException(cardUpdate, true);
        }
        Platform.runLater(() -> {
            Group oldGroup = viewCardToGroup.get(viewCard);
            Group newGroup = getGroupFromCardGroup(cardUpdate.getCardGroup());
            oldGroup.getChildren().remove(viewCard);
            viewCardToGroup.replace(viewCard, newGroup);
            oldGroup.getChildren().remove(viewCard);
            newGroup.getChildren().add(viewCard);

            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateXProperty(), getCardDefaultPosition(viewCard).getX())),
                    new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateYProperty(), getCardDefaultPosition(viewCard).getY())),
                    new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateZProperty(), getCardDefaultPosition(viewCard).getZ())),
                    new KeyFrame(new Duration(1000), new KeyValue(viewCard.rotateProperty(), getCardDefaultRotation(viewCard)))
            );
            timeline.play();
        });
    }


    /**
     * This method is called by @update if the update type is @REMOVE_CARD_FROM_GROUP
     * It remove a ViewCard from its actual JavaFX group
     * and place it to the default group that is @root3d
     * @since   v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     */
    private void removeCardFromGroup(CardUpdate cardUpdate) throws NullViewCardException {
        changeCardGroup(cardUpdate);
    }


    /**
     * This method is called by @update if the update type is @DELETE_CARD
     * It delete the ViewCard associated to a model Card from the View
     * @since   v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     */
    private void removeCard(CardUpdate cardUpdate) throws NullViewCardException {
        ViewCard viewCard = getViewCard(cardUpdate.getCard());
        if (viewCard == null)
        {
            throw new NullViewCardException(cardUpdate, true);
        }
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
        //TODO : SHUFFLING CARDS ANIMATION
    }

    /**
     * This method is called by @update if the update type is @SORT_DECK
     * It sorts a deck of card following cards priority from Tarot's rules
     * @since   v0.8.1
     * @param   cardGroup     the cardUpdate object.
     */
    private void sortDeck(CardGroup cardGroup) throws NullViewCardException {
        //TODO : SORTING DECK ANIMATION
    }

    /**
     * This method is called by @update if the update type is @CUT_DECK
     * It cut the deck of a given group in two
     * @since v0.7.1
     * @param   cardGroup     the cardUpdate object.
     */
    private void cutDeck(CardGroup cardGroup) throws NullViewCardException {
        //TODO : CUTTING DECK ANIMATION
    }

    /**
     * This method is called by @update if the update type is @SPREAD_CARDS
     * It spreads the card of a group on the table
     * @since v0.7.1
     * @param   cardGroup     the cardUpdate object.
     */
    private void spreadAllCards(CardGroup cardGroup) throws NullViewCardException {
        //TODO : SPREADING CARDS ANIMATION
        /*
        All cards will be displayed next to each other with a certain margin
        and line break depending on cards number.
        The cards must fit on the table (resized if needed)
         */
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
    private ViewCard getViewCard(Card card) {
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
     * return the @root3d group if no specific group exist
     * @since   v0.6
     *
     * @param   cardGroup     the cardGroup object
     * @return  the associated JavaFX Group of a CardGroup
     */
    public Group getGroupFromCardGroup(CardGroup cardGroup) {
        if (cardGroupToGroup.containsKey(cardGroup))
            return cardGroupToGroup.get(cardGroup);
        else
            return root3d;
    }


    /**
     * This method return the associated CardGroup of a JavaFx Group
     * return the null if no specific group exist
     * @since   v0.7
     *
     * @param   viewGroup     the viewGroup object
     * @return  the associated CardGroup of a JavaFx Group
     */
    private CardGroup getCardGroupFromGroup(Group viewGroup) {
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
                            TABLE_SIZE - HAND_MARGIN_LEFT - ViewCard.getCardWidth()
                                    - (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS,
                            HAND_MARGIN_UP, (-1)*ViewCard.getCardDepth());
                    break;
                case West:
                    point3D = new Point3D((ViewCard.getCardHeight() - ViewCard.getCardWidth())/2 + HAND_MARGIN_UP,
                            (-1)*((ViewCard.getCardHeight() - ViewCard.getCardWidth())/2) + HAND_MARGIN_LEFT
                                    + (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS,
                            (-1)*ViewCard.getCardDepth());
                    break;
                case South:
                    point3D = new Point3D(HAND_MARGIN_LEFT + (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS,
                            TABLE_SIZE-HAND_MARGIN_UP-ViewCard.getCardHeight(),(-1)*ViewCard.getCardDepth());
                    break;
                case East:
                    point3D = new Point3D(TABLE_SIZE - ViewCard.getCardWidth() -((ViewCard.getCardHeight()
                            - ViewCard.getCardWidth())/2) - HAND_MARGIN_UP, TABLE_SIZE - HAND_MARGIN_LEFT
                            - ViewCard.getCardWidth() - ((ViewCard.getCardHeight() - ViewCard.getCardWidth())/2)
                            - (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS, (-1)*ViewCard.getCardDepth());
                    break;
            }
        }
        else if (viewCardToGroup.get(viewCard) == pickedCardDeck) {
            point3D = new Point3D(TALON_POSITION.getX(), TALON_POSITION.getY(), TALON_POSITION.getZ()
                    - ViewCard.getCardDepth()*(getNbViewCard(talon)));
        }
        else if (viewCardToGroup.get(viewCard) == talon) {
            point3D = new Point3D(TALON_POSITION.getX(), TALON_POSITION.getY(), TALON_POSITION.getZ()
                    - ViewCard.getCardDepth()*(getNbViewCard(talon)));
        }
        else if (viewCardToGroup.get(viewCard) == initialDeck) {
            point3D = new Point3D(INITIAL_DECK_POSITION.getX(), INITIAL_DECK_POSITION.getY(),
                    INITIAL_DECK_POSITION.getZ() -ViewCard.getCardDepth()*(getNbViewCard(initialDeck)));
        }
        else if (viewCardToGroup.get(viewCard) == root3d) {
            //TODO : STATEMENT EMPTY
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
        else if (viewCardToGroup.get(viewCard) == initialDeck) {
            angle = 0;
        }
        else if (viewCardToGroup.get(viewCard) == pickedCardDeck) {
            angle = 0;
        }
        else if (viewCardToGroup.get(viewCard) == root3d) {
            angle = 0;
        }
        return angle;
    }


    //GETTERS - no documentation needed

    public Group getRoot3d()
    {
        return root3d;
    }
    public Group getTalon()
    {
        return talon;
    }
    private ViewCamera getViewCamera()
    {
        return (ViewCamera)getCamera();
    }
    public HashMap<ViewCard, Group> getViewCardToGroup() {
        return viewCardToGroup;
    }
}