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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.*;


/**
 * The {@code AppView} class consists in the MVC architecture view
 * @author Alexandre
 * @author Arthur
 * @version v0.9
 * @since v0.2
 *
 * @see Observer
 * @see Scene
 */
public class AppView extends Scene implements Observer {

    private static final float CARPET_SIZE = 2500;
    private static final float MARGIN_TABLE = 130;
    private static final float MARGIN_CARDS = 30;
    private static final float CARPET_DEPTH = 30;
    private static final float HAND_MARGIN_UP = 100;
    private static final float HAND_MARGIN_LEFT = (float)(0.2 *  CARPET_SIZE);
    private static final float MARGIN_BETWEEN_HAND_CARDS = ( CARPET_SIZE-(2*HAND_MARGIN_LEFT))/18;
    private static final Point3D TALON_POSITION = new Point3D(( CARPET_SIZE/2) - (ViewCard.getCardWidth()/2),
            ( CARPET_SIZE/2)-(ViewCard.getCardHeight()/2), 0);
    private static final Point3D INITIAL_DECK_POSITION = new Point3D(-300,  CARPET_SIZE/2, -300);
    private static final Point3D PICKED_CARD_DECK_POSITION = new Point3D(MARGIN_TABLE,
             CARPET_SIZE-MARGIN_TABLE-ViewCard.getCardHeight(), 0);

    private boolean handleCardPicking;
    private GameModel gameModel;
    private AppPresenter appPresenter;
    private Group root3d;
    private Group rootGUI;
    private Group background;
    private Group wholeCardsDeck;
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
        this.handleCardPicking = false;
        model.addObserver(this);

        //Create the groups
        root3d = new Group();
        rootGUI = new Group();
        background = new Group();
        wholeCardsDeck = new Group();
        pickedCardDeck = new Group();
        talon = new Group();
        viewCardToGroup = new HashMap<>();
        cardGroupToGroup = new HashMap<>();
        root.getChildren().addAll(root3d, rootGUI);
        root3d.getChildren().addAll(background, talon, wholeCardsDeck, pickedCardDeck);

        for (PlayerHandler.PlayersCardinalPoint playersCardinalPoint :
                PlayerHandler.PlayersCardinalPoint.values()) {
            hands[playersCardinalPoint.ordinal()] = new Group();
            root3d.getChildren().add(hands[playersCardinalPoint.ordinal()]);
        }
        updateCardGroupToGroup();

        //Create the scene objects
        RectangleMesh carpet = new RectangleMesh( CARPET_SIZE,  CARPET_SIZE, CARPET_DEPTH,
                "file:./res/table.jpg", 1100, 1100);
        ImageView table = new ImageView("file:./res/wood.jpg");
        table.setTranslateZ(CARPET_DEPTH);
        table.setTranslateX(-table.getImage().getWidth()/2 + CARPET_SIZE/2);
        table.setTranslateY(-table.getImage().getHeight()/2 + CARPET_SIZE/2);
        table.setScaleX(2);
        table.setScaleY(2);
        background.getChildren().addAll(carpet, table);

        //Define the camera
        this.setCamera(new ViewCamera(true));
        this.getViewCamera().setTranslateX( CARPET_SIZE/2);
        this.getViewCamera().setTranslateY(4000);
        this.getViewCamera().setTranslateZ(-3500);
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
                                flipBackCard(cardUpdate, 2500);
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
        else if ( arg instanceof ViewActionExpected) {
            switch (((ViewActionExpected)arg))
            {
                case PICK_CARD:
                    handleCardPicking = true;
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
        if (getViewCardFromCard(cardUpdate.getCard()) != null)
        {
            throw new NullViewCardException(cardUpdate, false);
        }
        new ViewCard(cardUpdate.getCard(), this,
                getGroupFromCardGroup(cardUpdate.getCardGroup()));
        flipBackCard(cardUpdate, 0);
    }

    /**
     * This method is called by @update if the update type is @FLIP_CARD
     * It apply a 180° on the 3D Card with a transition to show its other face
     * @since   v0.6
     * @param   cardUpdate     the cardUpdate object.
     */
    private void flipBackCard(CardUpdate cardUpdate, int animationTime) {
        Stack<Card> cardsStack = new Stack<>();
        if (animationTime<5)
        {
            //To prevent keyFrames from mixing
            animationTime = 5;
        }
        if (cardUpdate.getCard() == null)
        {
            cardUpdate.getCardGroup().forEach(cardsStack::push);
        } else {
            cardsStack.push(cardUpdate.getCard());
        }

        while (!cardsStack.empty()) {
            ViewCard viewCard = getViewCardFromCard(cardsStack.lastElement());

            if (viewCard != null && (viewCard.isShown() != viewCard.getModelCard().isShown())) {

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
                if (cardsStack.size() == 1)
                {
                    timeline.setOnFinished(event -> cardUpdate.setAnimationFinished());
                }
                timeline.play();
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
    private void changeCardGroup(CardUpdate cardUpdate) throws NullViewCardException {
        ViewCard viewCard = getViewCardFromCard(cardUpdate.getCard());

        if (viewCard == null)
            throw new NullViewCardException(cardUpdate, true);
        else {
            Group oldGroup = viewCardToGroup.get(viewCard);
            Group newGroup = getGroupFromCardGroup(cardUpdate.getCardGroup());
            oldGroup.getChildren().remove(viewCard);
            viewCardToGroup.replace(viewCard, newGroup);
            newGroup.getChildren().add(viewCard);

            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateXProperty(),
                            getCardDefaultPosition(viewCard).getX())),
                    new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateYProperty(),
                            getCardDefaultPosition(viewCard).getY())),
                    new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateZProperty(),
                            getCardDefaultPosition(viewCard).getZ())),
                    new KeyFrame(new Duration(1000), new KeyValue(viewCard.rotateProperty(),
                            getCardDefaultRotation(viewCard)))
            );
            timeline.setOnFinished(event -> cardUpdate.setAnimationFinished());
            timeline.play();
        }
    }


    /**
     * This method is called by @update if the update type is @MOVE_CARD_BETWEEN_GROUPS
     * It move a ViewCard associated with a model Card to another JavaFX Group
     * @since   v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     * @param specificPosition the destination position
     */
    private void changeCardGroup(CardUpdate cardUpdate, Point3D specificPosition)
            throws NullViewCardException {
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
                new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateXProperty(), specificPosition.getX())),
                new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateYProperty(), specificPosition.getY())),
                new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateZProperty(), specificPosition.getZ())),
                new KeyFrame(new Duration(1000), new KeyValue(viewCard.rotateProperty(), getCardDefaultRotation(viewCard)))
        );
        timeline.setOnFinished(event -> cardUpdate.setAnimationFinished());
        timeline.play();
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
        ViewCard viewCard = getViewCardFromCard(cardUpdate.getCard());
        if (viewCard == null)
        {
            throw new NullViewCardException(cardUpdate, true);
        }
        viewCardToGroup.get(viewCard).getChildren().remove(viewCard);
        viewCardToGroup.remove(viewCard);
        cardUpdate.setAnimationFinished();
    }

    /**
     * Reorganize all viewCards of a group with their default positions
     * @since   v0.9.2
     *
     * @param   group     the group object.
     */
    private void refreshGroupNodesPosition(Group group)
    {
        List<ViewCard> originalDeck = new ArrayList<>();
        for (Node node : group.getChildren())
        {
            if (node instanceof ViewCard)
            {
                originalDeck.add((ViewCard)node);
            }
        }
        for (ViewCard viewCard : originalDeck)
        {
            group.getChildren().remove(viewCard);
        }
        for (ViewCard viewCard : originalDeck)
        {
            try {
                changeCardGroup(new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, viewCard.getModelCard(), getCardGroupFromGroup(group)));
            } catch (NullViewCardException e) {
                System.err.println(e.toString());
            }
        }
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
            timeline.getKeyFrames().addAll(
                    new KeyFrame(new Duration(i * 100), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), 0)),
                    new KeyFrame(new Duration(((i+1) * 100)-50), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), ViewCard.getCardWidth()*2)),
                    new KeyFrame(new Duration((i+1) * 100), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), 0)),
                    new KeyFrame(new Duration((i+1) * 100), new KeyValue(viewCard.translateZProperty(), INITIAL_DECK_POSITION.getZ() - (nbViewCards*2 - i) * ViewCard.getCardDepth())),
                    new KeyFrame(new Duration((i+1) * 100+1), event -> {
                        Group group = getGroupFromCardGroup(cardUpdate.getCardGroup());
                        group.getChildren().remove(viewCard);
                        group.getChildren().add(0, viewCard);
                    })
            );
            if (i == cardUpdate.getCardGroup().size())
            {
                timeline.getKeyFrames().add(new KeyFrame(new Duration((i + 3) * 100), event -> refreshGroupNodesPosition(getGroupFromCardGroup(cardUpdate.getCardGroup()))));
            }
            i++;
        }
        timeline.play();
    }


    /**
     * This method is called by @update if the update type is @SORT_DECK
     * It sorts a deck of card following cards priority from Tarot's rules
     * @since   v0.8.1
     * @param   cardUpdate the cardUpdate object.
     */
    private void sortDeck(CardUpdate cardUpdate) throws NullViewCardException {
        CardGroup cardGroup = cardUpdate.getCardGroup();
        for (Card card : cardGroup) {
            ViewCard viewCard = getViewCardFromCard(card);

            if (viewCard != null) {
                Point3D newPosition = new Point3D(0, 0, 0);
                Group group = viewCardToGroup.get(viewCard);
                final int CARD_INDEX = cardGroup.indexOf(viewCard.getModelCard());

                switch (gameModel.getPlayerHandler().getPlayerCardinalPoint( (Hand)getCardGroupFromGroup(group) ) )
                {
                    case North:
                        newPosition = new Point3D(  CARPET_SIZE - HAND_MARGIN_LEFT - ViewCard.getCardWidth()
                                - CARD_INDEX*MARGIN_BETWEEN_HAND_CARDS,
                                HAND_MARGIN_UP, -ViewCard.getCardDepth());
                        break;
                    case West:
                        newPosition = new Point3D((ViewCard.getCardHeight() - ViewCard.getCardWidth())/2
                                + HAND_MARGIN_UP, (-1)*((ViewCard.getCardHeight() - ViewCard.getCardWidth())/2)
                                + HAND_MARGIN_LEFT + CARD_INDEX*MARGIN_BETWEEN_HAND_CARDS, -ViewCard.getCardDepth());
                        break;
                    case South:
                        newPosition = new Point3D( HAND_MARGIN_LEFT + CARD_INDEX*MARGIN_BETWEEN_HAND_CARDS,
                                 CARPET_SIZE-HAND_MARGIN_UP-ViewCard.getCardHeight(),-ViewCard.getCardDepth());
                        break;
                    case East:
                        newPosition = new Point3D( CARPET_SIZE - ViewCard.getCardWidth() -((ViewCard.getCardHeight()
                                - ViewCard.getCardWidth())/2) - HAND_MARGIN_UP,  CARPET_SIZE - HAND_MARGIN_LEFT
                                - ViewCard.getCardWidth() - ((ViewCard.getCardHeight() - ViewCard.getCardWidth())/2)
                                - CARD_INDEX*MARGIN_BETWEEN_HAND_CARDS, -ViewCard.getCardDepth());
                        break;
                }

                try {
                    CardUpdate newCardUpdate = new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, card, cardGroup);
                    cardUpdate.addSubUpdate(newCardUpdate);
                    changeCardGroup(newCardUpdate, newPosition);
                } catch (NullViewCardException e) {
                    System.err.println(e.toString());
                }
            } else {
                throw new NullViewCardException(new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, card, cardGroup), true);
            }
        }
        cardUpdate.setAnimationFinished();
    }


    /**
     * This method is called by @update if the update type is @CUT_DECK
     * It cut the deck of a given group in two
     * @since v0.7.1
     * @param   cardUpdate     the cardUpdate object.
     */
    private void cutDeck(CardUpdate cardUpdate){
        Group initialGroup;
        List<ViewCard> group1 = new ArrayList<>();
        List<ViewCard> group2 = new ArrayList<>();
        int i = 0;
        int cutCardIndex = getGroupFromCardGroup(cardUpdate.getCardGroup()).getChildren().indexOf(getViewCardFromCard(cardUpdate.getCard()));
        initialGroup = getGroupFromCardGroup(cardUpdate.getCardGroup());
        if (initialGroup == talon || initialGroup == wholeCardsDeck)
        {
            while (i<initialGroup.getChildren().size())
            {
                if (initialGroup.getChildren().get(i) instanceof ViewCard)
                {
                    if (i <= cutCardIndex)
                    {
                        group1.add((ViewCard)initialGroup.getChildren().get(i));
                    } else {
                        group2.add((ViewCard)initialGroup.getChildren().get(i));
                    }
                }
                i++;
            }
            Timeline timeline = new Timeline();
            for (ViewCard viewCard : group1)
            {
                timeline.getKeyFrames().add(new KeyFrame(new Duration(1000), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), -ViewCard.getCardWidth())));
                timeline.getKeyFrames().add(new KeyFrame(new Duration(2000), new KeyValue(viewCard.translateZProperty(), viewCard.getTranslateZ() - group1.size()*ViewCard.getCardDepth())));
                timeline.getKeyFrames().add(new KeyFrame(new Duration(3000), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), 0)));
            }
            for (ViewCard viewCard : group2)
            {
                timeline.getKeyFrames().add(new KeyFrame(new Duration(1000), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), ViewCard.getCardWidth())));
                timeline.getKeyFrames().add(new KeyFrame(new Duration(2000), new KeyValue(viewCard.translateZProperty(), viewCard.getTranslateZ() + group2.size()*ViewCard.getCardDepth())));
                timeline.getKeyFrames().add(new KeyFrame(new Duration(3000), new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), 0)));
            }
            timeline.setOnFinished(event -> cardUpdate.setAnimationFinished());
            timeline.play();
        }
    }


    /**
     * This method is called by @update if the update type is @SPREAD_CARDS
     * It spreads the card of a group on the table
     * @since v0.7.1
     * @param   cardUpdate     the cardUpdate object.
     */
    private void spreadAllCards(CardUpdate cardUpdate) throws NullViewCardException {
        int nbCardInRow = (int)(( CARPET_SIZE - MARGIN_TABLE*2)/(ViewCard.getCardWidth()+MARGIN_CARDS));
        int i = 0;
        int j = 0;
        for(Card card : cardUpdate.getCardGroup())
        {
            Point3D position = new Point3D(MARGIN_TABLE + i*(MARGIN_CARDS+ViewCard.getCardWidth()), MARGIN_TABLE
                    + j*(MARGIN_CARDS+ViewCard.getCardHeight()), -ViewCard.getCardDepth());
            CardUpdate newCardUpdate = new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, card, null);
            cardUpdate.addSubUpdate(newCardUpdate);
            changeCardGroup(newCardUpdate, position);
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
     * Return   the @root3d group if no specific group exist
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
            return root3d;
    }


    /**
     * This method return the associated CardGroup of a JavaFx Group
     * Return the null if no specific group exist
     * @since   v0.7
     *
     * @param   viewGroup     the viewGroup object
     * @return  the associated CardGroup of a JavaFx Group
     */
    CardGroup getCardGroupFromGroup(Group viewGroup) {
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
    Point3D getCardDefaultPosition(@NotNull ViewCard viewCard) {

        Point3D point3D = new Point3D(0, 0, 0);
        Group group = viewCardToGroup.get(viewCard);
        if (viewCardToGroup.get(viewCard) == hands[0] || viewCardToGroup.get(viewCard) == hands[1]
                || viewCardToGroup.get(viewCard) == hands[2] || viewCardToGroup.get(viewCard) == hands[3])
        {
            switch (gameModel.getPlayerHandler().getPlayerCardinalPoint((Hand) getCardGroupFromGroup(group)))
            {
                case North:
                    point3D = new Point3D(
                             CARPET_SIZE - HAND_MARGIN_LEFT - ViewCard.getCardWidth()
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
                             CARPET_SIZE-HAND_MARGIN_UP-ViewCard.getCardHeight(),(-1)*ViewCard.getCardDepth());
                    break;
                case East:
                    point3D = new Point3D( CARPET_SIZE - ViewCard.getCardWidth() -((ViewCard.getCardHeight()
                            - ViewCard.getCardWidth())/2) - HAND_MARGIN_UP,  CARPET_SIZE - HAND_MARGIN_LEFT
                            - ViewCard.getCardWidth() - ((ViewCard.getCardHeight() - ViewCard.getCardWidth())/2)
                            - (getNbViewCard(group)-1)*MARGIN_BETWEEN_HAND_CARDS, (-1)*ViewCard.getCardDepth());
                    break;
            }
        }
        else if (viewCardToGroup.get(viewCard) == pickedCardDeck) {
            point3D = new Point3D(PICKED_CARD_DECK_POSITION.getX() + (ViewCard.getCardWidth() + MARGIN_CARDS) * getNbViewCard(pickedCardDeck),
                    PICKED_CARD_DECK_POSITION.getY(), PICKED_CARD_DECK_POSITION.getZ() - ViewCard.getCardDepth());
        }
        else if (viewCardToGroup.get(viewCard) == talon) {
            point3D = new Point3D(TALON_POSITION.getX(), TALON_POSITION.getY(), TALON_POSITION.getZ()
                    - ViewCard.getCardDepth()*(getNbViewCard(talon)));
        }
        else if (viewCardToGroup.get(viewCard) == wholeCardsDeck) {
            point3D = new Point3D(INITIAL_DECK_POSITION.getX(), INITIAL_DECK_POSITION.getY(),
                    INITIAL_DECK_POSITION.getZ() -ViewCard.getCardDepth()*(getNbViewCard(wholeCardsDeck)));
        }
        else if (viewCardToGroup.get(viewCard) == root3d) {
            point3D = new Point3D(0,0, -ViewCard.getCardDepth());
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
        else if (viewCardToGroup.get(viewCard) == root3d) {
            angle = 0;
        }
        return angle;
    }


    //GETTERS & SETTERS - no documentation needed

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
    public AppPresenter getAppPresenter() {
        return appPresenter;
    }
    public boolean isHandlingCardPicking() {
        return handleCardPicking;
    }
    public Group getWholeCardsDeck() {
        return wholeCardsDeck;
    }

    public void setHandleCardPicking(boolean handleCardPicking) {
        this.handleCardPicking = handleCardPicking;
    }
}