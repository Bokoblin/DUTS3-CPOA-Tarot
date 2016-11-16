/*
Copyright 2016 Jacquot Alexandre, Jolivet Arthur
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

package tarotCardDistribution.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.effect.Light;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import tarotCardDistribution.controller.*;
import tarotCardDistribution.model.*;
import exceptions.ViewCardUpdateExistException;

import java.awt.*;
import java.util.*;

/**
 * The {@code AppView} class consists in the MVC architecture view
 * @author Alexandre
 * @author Arthur
 * @version v0.6
 * @since v0.2
 *
 * @see Observer
 * @see Scene
 */
public class AppView extends Scene implements Observer{
    //const
    private static final double TABLE_SIZE = 2000;
    private static final double TABLE_DEPTH = 182;
    private static final Point3D TALON_POSITION = new Point3D((TABLE_SIZE/2)-(ViewCard.getCardWidth()/2), (TABLE_SIZE/2)-(ViewCard.getCardHeight()/2), TABLE_DEPTH);
    private static final double HAND_MARGIN = 200;
    private static final double MARGIN_BETWEEN_HAND_CARDS = (TABLE_SIZE-(2*HAND_MARGIN))/18;

    private GameModel gameModel;
    private AppController appController;
    private Group root3d;
    private Group rootGUI;
    private Group talon;
    private Group[] hands = new Group[4];
    private Group initialDeck;
    private Group background;
    private HashMap<CardGroup, Group> cardGroupToGroup;
    private HashMap<ViewCard, Group> viewCardToGroup;

    /**
     * Constructs a view for a specific root node and with a gameModel and a appController
     * @since v0.1
     * @param gameModel the gameModel it reads
     * @param appController the appController it sends event information
     */
    public AppView(Group root, GameModel gameModel, AppController appController) {
        super(root, 800, 600, true, SceneAntialiasing.BALANCED);

        this.gameModel = gameModel;
        this.appController = appController;
        gameModel.addObserver(this);

        root3d = new Group();
        rootGUI = new Group();
        background = new Group();
        talon = new Group();
        initialDeck = new Group();
        viewCardToGroup = new HashMap<>();
        cardGroupToGroup = new HashMap<>();
        root.getChildren().addAll(root3d, rootGUI);
        root3d.getChildren().addAll(background, talon, initialDeck);
        for (int i =0; i<4; i++)
        {
            hands[i] = new Group();
            root3d.getChildren().add(hands[i]);
        }
        for (PlayerHandler.PlayersCardinalPoint playersCardinalPoint : PlayerHandler.PlayersCardinalPoint.values())
        {
            cardGroupToGroup.put(gameModel.getPlayerHandler().getPlayer(playersCardinalPoint), hands[playersCardinalPoint.ordinal()]);
        }
        cardGroupToGroup.put(gameModel.getTalon(), talon);
        cardGroupToGroup.put(gameModel.getInitialDeck(), initialDeck);

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

        this.setOnMouseClicked(event -> {
            try {
                turnBackCard(new CardUpdate(ActionPerformedOnCard.TURN_CARD,gameModel.getInitialDeck().getCardList().get(1)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //Create the scene objects
        /*RectangleMesh table = new RectangleMesh(TABLE_SIZE, TABLE_SIZE, TABLE_DEPTH, "file:./res/table.jpg", 1100, 1100);
        background.getChildren().add(table);*/
        ViewCard viewCard =  new ViewCard(100, 200, 2, 1536, 2663, gameModel.getInitialDeck().getCardList().get(1));
        hands[0].getChildren().add(viewCard);
        viewCardToGroup.put(viewCard, hands[0]);
        this.setCamera(new ViewCamera(true));
        this.getViewCamera().setTranslateX(1000);
        this.getViewCamera().setTranslateY(2400);
        this.getViewCamera().setTranslateZ(-3600);

        //Lets define the camera
        getViewCamera().getTransformations().getRotateX().setAngle(20);
        root3d.getChildren().add(new AmbientLight(Color.WHITE));
    }

    /**
     * This method is called whenever the observed object is changed.
     * It determines which object of the gameModel has been changed with arg
     * parameter and updates view in consequence
     * @since v0.2
     *
     * @param   o     the observable object.
     * @param   arg   an argument passed to the <code>notifyObservers</code> method.
     */
    @Override
    public void update(Observable o, Object arg)
    {
        if (arg instanceof CardUpdate)
        {
            CardUpdate cardUpdate = (CardUpdate)arg;
            try {
                switch (cardUpdate.getType())
                {
                    case ADD_CARD:
                        addNewCard(cardUpdate);
                        break;
                    case TURN_CARD:
                        turnBackCard(cardUpdate);
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
                    default:
                        break;
                }
            } catch (ViewCardUpdateExistException e)
            {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * This method is called by @update if the update type is @ADD_CARD
     * It create a new ViewCard and add it to the corresponding javaFX group
     * @since v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     */
    private void addNewCard(CardUpdate cardUpdate)
            throws ViewCardUpdateExistException
    {
        if (getViewCard(cardUpdate.getCard()) != null)
        {
            throw new ViewCardUpdateExistException(cardUpdate, false);
        }
        ViewCard viewCard = new ViewCard(cardUpdate.getCard());
        Group group = cardGroupToViewGroup(cardUpdate.getCardGroup());
        viewCardToGroup.put(viewCard, group);
        group.getChildren().add(viewCard);
    }

    /**
     * This method is called by @update if the update type is @TURN_CARD
     * It apply a 180° on the 3D Card with a transition to show its other face
     * @since v0.6
     * @version 0.6.3
     * @param   cardUpdate     the cardUpdate object.
     */
    private void turnBackCard(CardUpdate cardUpdate)
            throws ViewCardUpdateExistException
    {
        ViewCard viewCard = getViewCard(cardUpdate.getCard());
        if (viewCard == null)
        {
            throw new ViewCardUpdateExistException(cardUpdate, true);
        }
        Timeline timeline = new Timeline();
        KeyValue initialTranslate = null;
        KeyValue finalTranslate = null;
        //If the card belong to a player deck the card will translate to the center of the table before rotating
        CardGroup cardGroup = viewGroupToCardGroup(viewCardToGroup.get(viewCard));
        if (cardGroup instanceof Hand)
        {
            switch (gameModel.getPlayerHandler().getPlayerCardinalPoint((Hand)cardGroup))
            {
                case North:
                    initialTranslate = new KeyValue(viewCard.translateYProperty(), 0);
                    finalTranslate = new KeyValue(viewCard.translateYProperty(), 220);
                    break;
                case West:
                    initialTranslate = new KeyValue(viewCard.translateXProperty(), 0);
                    finalTranslate = new KeyValue(viewCard.translateXProperty(), 220);
                    break;
                case South:
                    initialTranslate = new KeyValue(viewCard.translateYProperty(), 0);
                    finalTranslate = new KeyValue(viewCard.translateYProperty(), -220);
                    break;
                case East:
                    initialTranslate = new KeyValue(viewCard.translateXProperty(), 0);
                    finalTranslate = new KeyValue(viewCard.translateXProperty(), -220);
                    break;
            }
        } else {
            initialTranslate = new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), 0);
            finalTranslate = new KeyValue(viewCard.getTransformations().getTranslate().xProperty(), 0);
        }
        double initialRotateY, finalRotateY;
        if (viewCard.getTransformations().getRotateY().getAngle() >= 180)
        {
            initialRotateY = 180;
            finalRotateY = 0;
        } else {
            initialRotateY = 0;
            finalRotateY = 180;
        }
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, initialTranslate),
                new KeyFrame(new Duration(500), finalTranslate),
                new KeyFrame(new Duration(500), new KeyValue(viewCard.translateZProperty(), 0)),
                new KeyFrame(new Duration(1000), new KeyValue(viewCard.translateZProperty(), -100)),
                new KeyFrame(new Duration(1000), new KeyValue(viewCard.getTransformations().getRotateY().angleProperty(), initialRotateY)),
                new KeyFrame(new Duration(1500), new KeyValue(viewCard.getTransformations().getRotateY().angleProperty(), finalRotateY)),
                new KeyFrame(new Duration(1500), new KeyValue(viewCard.translateZProperty(), -100)),
                new KeyFrame(new Duration(2000), new KeyValue(viewCard.translateZProperty(), 0)),
                new KeyFrame(new Duration(2000), finalTranslate),
                new KeyFrame(new Duration(2500), initialTranslate)
        );
        timeline.play();
    }

    /**
     * This method is called by @update if the update type is @MOVE_CARD_BETWEEN_GROUPS
     * It move a ViewCard associated with a model Card to another JavaFX Group
     * @since v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     */
    private void changeCardGroup(CardUpdate cardUpdate)
            throws ViewCardUpdateExistException
    {
        ViewCard viewCard = getViewCard(cardUpdate.getCard());
        if (viewCard == null)
        {
            throw new ViewCardUpdateExistException(cardUpdate, true);
        }
        Group group = cardGroupToViewGroup(cardUpdate.getCardGroup());
        viewCardToGroup.get(viewCard).getChildren().remove(viewCard);
        viewCardToGroup.replace(viewCard, group);
        group.getChildren().add(viewCard);
    }

    /**
     * This method is called by @update if the update type is @REMOVE_CARD_FROM_GROUP
     * It remove a ViewCard from its actual JavaFX group
     * and place it to the default group that is @root3d
     * @since v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     */
    private void removeCardFromGroup(CardUpdate cardUpdate)
            throws ViewCardUpdateExistException
    {
        changeCardGroup(cardUpdate);
    }

    /**
     * This method is called by @update if the update type is @DELETE_CARD
     * It delete the ViewCard associated to a model Card from the View
     * @since v0.6
     *
     * @param   cardUpdate     the cardUpdate object.
     */
    private void removeCard(CardUpdate cardUpdate) throws ViewCardUpdateExistException
    {
        ViewCard viewCard = getViewCard(cardUpdate.getCard());
        if (viewCard == null)
        {
            throw new ViewCardUpdateExistException(cardUpdate, true);
        }
        viewCardToGroup.get(viewCard).getChildren().remove(viewCard);
        viewCardToGroup.remove(viewCard);
    }

    /**
     * This method return the correct posiion of a card depending on the group
     * @since v0.4
     * @version 0.6.4
     * @param   viewCard    the viewCard object.
     */
    public Point3D getCardPosition(ViewCard viewCard)
    {

    }


    /**
     * This method return the associated ViewCard of the actual scene of a Card model object
     * If the ViewCard doesn't exist it return null
     * @since v0.6
     *
     * @param   card     the model card object.
     */
    private ViewCard getViewCard(Card card)
    {
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
     * Return the @root3d group if no specific group exist
     * @since v0.6
     *
     * @param   cardGroup     the cardUpdate object.
     */
    public Group cardGroupToViewGroup(CardGroup cardGroup)
    {
        if (cardGroupToGroup.containsKey(cardGroup))
        {
            return cardGroupToGroup.get(cardGroup);
        } else {
            return root3d;
        }
    }

    public CardGroup viewGroupToCardGroup(Group viewgroup)
    {
        for (Map.Entry<CardGroup, Group> entry : cardGroupToGroup.entrySet())
        {
            if (entry.getValue() == viewgroup)
            {
                return entry.getKey();
            }
        }
        return null;
    }

    //GETTERS - no documentation needed

    public Group getRoot3d()
    {
        return root3d;
    }
    public Group getRootGUI()
    {
        return rootGUI;
    }
    public Group[] getHands()
    {
        return hands;
    }
    public Group getHand(PlayerHandler.PlayersCardinalPoint cardinalPoint)
    {
        return hands[cardinalPoint.ordinal()];
    }
    public Group getTalon()
    {
        return talon;
    }
    public Group getInitialDeck()
    {
        return initialDeck;
    }
    public ViewCamera getViewCamera()
    {
        return (ViewCamera)getCamera();
    }
}