package tests;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;
import org.junit.*;
import tarotCardDistribution.controller.*;
import tarotCardDistribution.model.*;
import tarotCardDistribution.view.*;

import static org.junit.Assert.*;
//TODO : Documentation
public class GameViewTests extends Application
{
    private static AppController appController;
    private static GameModel gameModel;
    private static AppView scene;
    private static Group root;
    //TODO : Documentation
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
    public void cleanClasses() {
        Card.resetClassForTesting();
        Hand.resetClassForTesting();
        Talon.resetClassForTesting();
        root = new Group();
        scene = new AppView(root, gameModel, appController);
    }
    //TODO : Documentation
    @Test
    public void addCardToView()
    {
        int nbNodeBeforeAddingCard = scene.getRoot3d().getChildren().size();
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0)));
        assertTrue(scene.getRoot3d().getChildren().size() == nbNodeBeforeAddingCard+1);
    }
    //TODO : Documentation
    @Test
    public void moveCard()
    {
        Hand hand = gameModel.getPlayerHandler().getPlayer(PlayerHandler.PlayersCardinalPoint.South);
        Talon chien = gameModel.getTalon();
        assertTrue(scene.getHands()[1].getChildren().size() == 0);
        assertTrue(scene.getChien().getChildren().size() == 0);
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0), hand));
        assertTrue(scene.getHands()[1].getChildren().size() == 1);
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, ((ViewCard)scene.getHands()[1].getChildren().get(0)).getModelCard(), chien));
        assertTrue(scene.getHands()[1].getChildren().size() == 0);
        assertTrue(scene.getChien().getChildren().size() == 1);
    }
    //TODO : Documentation
    @Test
    public void removeCardFromGroup()
    {
        Talon chien = gameModel.getTalon();
        assertTrue(scene.getChien().getChildren().size() == 0);
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0), chien));
        assertTrue(scene.getChien().getChildren().size() == 1);
        int nbNodeBeforeAddingCard = scene.getRoot3d().getChildren().size();
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.REMOVE_CARD_FROM_GROUP, ((ViewCard)scene.getChien().getChildren().get(0)).getModelCard()));
        assertTrue(scene.getRoot3d().getChildren().size() == nbNodeBeforeAddingCard+1);
    }
    //TODO : Documentation
    @Test
    public void removeCard()
    {
        Talon chien = gameModel.getTalon();
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0), chien));
        assertTrue(scene.getChien().getChildren().size() == 1);
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.DELETE_CARD, ((ViewCard)scene.getChien().getChildren().get(0)).getModelCard()));
        assertTrue(scene.getChien().getChildren().size() == 0);
    }
    //TODO : Documentation
    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new Group();
        gameModel = new GameModel();
        appController = new AppController();
        scene = new AppView(root, gameModel, appController);
        appController.setGameModel(gameModel);
        appController.setAppView(scene);

        primaryStage.setScene(scene);
    }
}
