package tests;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;
import org.junit.*;
import tarotCardDistribution.controller.AppController;
import tarotCardDistribution.model.*;
import tarotCardDistribution.view.AppView;
import tarotCardDistribution.view.ViewCard;

import static org.junit.Assert.*;

public class GameViewTests extends Application
{
    private static AppController appController;
    private static GameModel gameModel;
    private static AppView scene;
    private static Thread thread;
    private static Group root;

    @BeforeClass
    public static void initApplication()
    {
        thread = new Thread("JavaFX Application Thread") {
            @Override
            public void run()
            {
                Application.launch(GameViewTests.class, new String[0]);
            }
        };
        thread.setDaemon(true);
        thread.start();
        try
        {
            thread.sleep(2000);
        } catch (InterruptedException e)
        {
            System.err.println(e.getMessage());
        }
    }

    @Before
    public void cleanClasses() {
        Card.resetClassForTesting();
        Hand.resetClassForTesting();
        Chien.resetClassForTesting();
        root = new Group();
        scene = new AppView(root, gameModel, appController);
    }

    @Test
    public void addCardToView()
    {
        int nbNodeBeforeAddingCard = scene.getRoot3d().getChildren().size();
        gameModel.testAddCard(null);
        assertTrue(scene.getRoot3d().getChildren().size() == nbNodeBeforeAddingCard+1);
    }

    @Test
    public void moveCard()
    {
        Hand hand = gameModel.getPlayerMap().get(1);
        Chien chien = gameModel.getChien();
        assertTrue(scene.getHands()[1].getChildren().size() == 0);
        assertTrue(scene.getChien().getChildren().size() == 0);
        gameModel.testAddCard(hand);
        assertTrue(scene.getHands()[1].getChildren().size() == 1);
        gameModel.testMoveCard(((ViewCard)scene.getHands()[1].getChildren().get(0)).getModelCard(), chien);
        assertTrue(scene.getHands()[1].getChildren().size() == 0);
        assertTrue(scene.getChien().getChildren().size() == 1);
    }

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
