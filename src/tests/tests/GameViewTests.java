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

package tests;

import exceptions.CardGroupNumberException;
import exceptions.CardNumberException;
import exceptions.CardUniquenessException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;
import org.junit.*;
import app.presenter.*;
import app.model.*;
import app.view.*;

import static org.junit.Assert.*;

/**
 * GameView Unit tests
 *
 * @author Alexandre
 * @version v0.8.2
 * @since v0.6
 */
public class GameViewTests extends Application
{
    private static AppPresenter appPresenter;
    private static GameModel gameModel;
    private static AppView scene;
    private static Group root;

    /**
     * Constructs a view and run it in another thread for the tests.
     * @since v0.6
     */
    @BeforeClass
    public static void initApplication()
    {
        Card.resetClassForTesting();
        Hand.resetClassForTesting();
        Talon.resetClassForTesting();
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
    public void cleanClasses() {
        Card.resetClassForTesting();
        Hand.resetClassForTesting();
        Talon.resetClassForTesting();
        root = new Group();
        try {
            gameModel = new GameModel();
            gameModel.createCards();
        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            e.getMessage();
        }
        scene = new AppView(root, gameModel, appPresenter);
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
                    new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0)));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        assertTrue(scene.getRoot3d().getChildren().size() == nbNodeBeforeAddingCard+1);
    }

    /**
     * Add a card to the view then move it to another group
     * and verify the number of cards of the group have been increase
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
                new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0), hand));
        assertTrue(scene.getGroupFromCardGroup(hand).getChildren().size() == nbNodeHandBefore+1);
        gameModel.notifyObserversOfCardUpdate(
                new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, ((ViewCard)scene.getGroupFromCardGroup(hand).getChildren().get(0)).getModelCard(), talon));
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
                new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0), talon));
        assertTrue(scene.getTalon().getChildren().size() == nbNodeTalonBefore + 1);
        int nbNodeBeforeAddingCard = scene.getRoot3d().getChildren().size();
        try {
            gameModel.notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.REMOVE_CARD_FROM_GROUP,
                    ((ViewCard)scene.getTalon().getChildren().get(0)).getModelCard()));
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
        gameModel.notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.ADD_CARD,
                gameModel.getInitialDeck().get(0), talon));
        int nbNodeTalonBefore = scene.getTalon().getChildren().size();
        try {
            gameModel.notifyObserversOfCardUpdate(new CardUpdate(ActionPerformedOnCard.DELETE_CARD,
                    ((ViewCard)scene.getTalon().getChildren().get(0)).getModelCard()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        assertTrue(scene.getTalon().getChildren().size() == nbNodeTalonBefore - 1);
    }

    /**
     * Create the scene of the application before the tests.
     * @since v0.6
     * @param primaryStage the app main stage
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new Group();
        gameModel = new GameModel();
        appPresenter = new AppPresenter();
        scene = new AppView(root, gameModel, appPresenter);
        appPresenter.setGameModel(gameModel);
        appPresenter.setAppView(scene);

        primaryStage.setScene(scene);
    }
}
