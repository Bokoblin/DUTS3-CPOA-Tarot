package tests;

import exceptions.CardGroupNumberException;
import exceptions.CardNumberException;
import exceptions.CardUniquenessException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;
import org.junit.*;
import tarotCardDistribution.controller.*;
import tarotCardDistribution.model.*;
import tarotCardDistribution.view.*;

import static org.junit.Assert.*;

/**
 * GameView Unit tests
 *
 * @author Alexandre
 * @version v0.6
 * @since v0.6
 */
public class GameViewTests extends Application
{
    private static AppController appController;
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
        } catch (CardNumberException | CardUniquenessException | CardGroupNumberException e) {
            e.getMessage();
        }
        scene = new AppView(root, gameModel, appController);
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
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0)));
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
        int nbNodeHandBefore = scene.cardGroupToViewGroup(hand).getChildren().size();
        int nbNodeTalonBefore = scene.getTalon().getChildren().size();
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0), hand));
        assertTrue(scene.cardGroupToViewGroup(hand).getChildren().size() == nbNodeHandBefore+1);
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS, ((ViewCard)scene.cardGroupToViewGroup(hand).getChildren().get(0)).getModelCard(), talon));
        assertTrue(scene.cardGroupToViewGroup(hand).getChildren().size() == nbNodeHandBefore);
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
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0), talon));
        assertTrue(scene.getTalon().getChildren().size() == nbNodeTalonBefore + 1);
        int nbNodeBeforeAddingCard = scene.getRoot3d().getChildren().size();
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.REMOVE_CARD_FROM_GROUP, ((ViewCard)scene.getTalon().getChildren().get(0)).getModelCard()));
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
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.ADD_CARD, gameModel.getInitialDeck().get(0), talon));
        int nbNodeTalonBefore = scene.getTalon().getChildren().size();
        gameModel.updateCard(new CardUpdate(ActionPerformedOnCard.DELETE_CARD, ((ViewCard)scene.getTalon().getChildren().get(0)).getModelCard()));
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
        appController = new AppController();
        scene = new AppView(root, gameModel, appController);
        appController.setGameModel(gameModel);
        appController.setAppView(scene);

        primaryStage.setScene(scene);
    }
}
