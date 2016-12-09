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
package unitTests;

import app.model.*;
import app.presenter.AppPresenter;
import app.view.GameView;
import app.view.ViewCard;
import exceptions.CardGroupNumberException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * GameView Unit tests
 *
 * @author Alexandre
 * @version v1.0.0
 * @since v0.6
 */
public class GameViewTests extends Application
{
    private static AppPresenter appPresenter;
    private static GameModel gameModel;
    private static GameView scene;
    private static Group root;

    /**
     * Constructs a view and run it in another thread for the tests.
     * @since v0.6
     */
    @BeforeClass
    public static void initApplication()
    {
        Thread thread = new Thread("JavaFX Application Thread") {
            @Override
            public void run() {
                Application.launch(GameViewTests.class);
            }
        };
        thread.setDaemon(true);
        thread.start();
        try
        {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            System.err.println(e.getMessage());
        }
    }

    @Before
    public void initGame() {
        Card.resetClass();
        Hand.resetClass();
        Talon.resetClass();
        root = new Group();
        try {
            gameModel = new GameModel(false);
            gameModel.createCards();
        } catch (CardGroupNumberException e) {
            e.getMessage();
        }
        scene = new GameView(root, gameModel, appPresenter);
    }

    /**
     * Add a card to the view and verify the number of cards
     * of the default group (root3d) have been increase
     * @since v0.6
     */
    @Test
    public void addCardToView()
    {
        int nbNodeBeforeAddingCard = scene.getRoot3d().getChildren().size();
        try {
            gameModel.notifyObserversOfCardUpdate(
                    new CardUpdate(CardUpdateType.ADD_CARD, gameModel.getWholeCardsDeck().get(0)));
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        assertTrue(scene.getRoot3d().getChildren().size() == nbNodeBeforeAddingCard+1);
    }

    /**
     * Add a card to the view then move it to another group
     * and verify the number of cards of the group have been increase
     *
     * It might fail, if it does, relaunch the tests
     * @since v0.6
     */
    @Test
    public void moveCard()
    {
        Hand hand = gameModel.getPlayerHandler().getPlayer(PlayerHandler.PlayersCardinalPoint.South);
        Talon talon = gameModel.getTalon();
        int nbNodeHandBefore = scene.getGroupFromCardGroup(hand).getChildren().size();
        int nbNodeTalonBefore = scene.getTalon().getChildren().size();
        gameModel.notifyObserversOfCardUpdate(
                new CardUpdate(CardUpdateType.ADD_CARD, gameModel.getWholeCardsDeck().get(0), hand));
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        assertTrue(scene.getGroupFromCardGroup(hand).getChildren().size() == nbNodeHandBefore+1);
        gameModel.notifyObserversOfCardUpdate(
                new CardUpdate(CardUpdateType.MOVE_CARD_BETWEEN_GROUPS, ((ViewCard)scene.getGroupFromCardGroup(hand).getChildren().get(0)).getModelCard(), talon));
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        assertTrue(scene.getGroupFromCardGroup(hand).getChildren().size() == nbNodeHandBefore);
        assertTrue(scene.getTalon().getChildren().size() == nbNodeTalonBefore + 1);
    }

    /**
     * Add a card to the view then delete and verify the number of cards of the group have been decrease
     * @since v0.6
     */
    @Test
    public void removeCardFromGroup()
    {
        Talon talon = gameModel.getTalon();
        int nbNodeTalonBefore = scene.getTalon().getChildren().size();
        gameModel.notifyObserversOfCardUpdate(
                new CardUpdate(CardUpdateType.ADD_CARD, gameModel.getWholeCardsDeck().get(0), talon));
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        assertTrue(scene.getTalon().getChildren().size() == nbNodeTalonBefore + 1);
        int nbNodeBeforeAddingCard = scene.getRoot3d().getChildren().size();
        try {
            gameModel.notifyObserversOfCardUpdate(new CardUpdate(CardUpdateType.REMOVE_CARD_FROM_GROUP,
                    ((ViewCard)scene.getTalon().getChildren().get(0)).getModelCard()));
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        assertTrue(scene.getRoot3d().getChildren().size() == nbNodeBeforeAddingCard+1);
    }

    /**
     * Add a card to the view then delete it and verify the number of cards of the group have been increase
     * @since v0.6
     */
    @Test
    public void removeCard()
    {
        Talon talon = gameModel.getTalon();
        gameModel.notifyObserversOfCardUpdate(new CardUpdate(CardUpdateType.ADD_CARD,
                gameModel.getWholeCardsDeck().get(0), talon));
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        int nbNodeTalonBefore = scene.getTalon().getChildren().size();
        try {
            gameModel.notifyObserversOfCardUpdate(new CardUpdate(CardUpdateType.DELETE_CARD,
                    ((ViewCard)scene.getTalon().getChildren().get(0)).getModelCard()));
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        assertTrue(scene.getTalon().getChildren().size() == nbNodeTalonBefore - 1);
    }

    /**
     * Create the scene of the application before the tests.
     * @since v0.6
     * @param window the app main stage
     */
    @Override
    public void start(Stage window) throws Exception {
        appPresenter = new AppPresenter(window);
        root = new Group();
        gameModel = new GameModel(false);
        scene = new GameView(root, gameModel, appPresenter);
        appPresenter.setGameModel(gameModel);
        appPresenter.setGameView(scene);
        window.setScene(scene);
    }
}
