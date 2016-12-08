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

package app.presenter;

import app.model.CardUpdate;
import app.model.GameModel;
import app.model.GameState;
import app.view.GameView;
import exceptions.CardGroupNumberException;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.stage.Stage;

/**
 * The {@code AppPresenter} class consists in
 * the MVP architecture presenter
 * @author Arthur
 * @version v0.11
 * @since v0.2
 */
public class AppPresenter {
    private GameModel gameModel;
    private Stage window;
    private GameView gameView;
    private boolean dealerChoosingEnabled;

    /**
     * Constructs a view for a specific root node and with a model and a presenter
     * @since   v0.10
     * @param   window  the app window
     */
    public AppPresenter(Stage window) {
        this.window = window;

        window.setOnCloseRequest(event -> {
            if ( gameModel != null && gameModel.getGameState() == GameState.GAME_ENDED)
                new Thread( () -> gameModel.quitGame()).start();
            Platform.exit();
            System.exit(0);
        });
    }


    /**
     * Transmits user choice to the model
     * @since v0.8
     *
     * @param choice the user choice
     */
    public void transmitUserChoice(int choice) {
        if ( choice < 0)
            try {
                throw new Exception("choice is invalid");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        else
            gameModel.setUserChoice(choice);
    }

    /**
     * Aims to notify the model of an animation's end
     * @since   v0.10
     * @param   cardUpdate  the cardUpdate associated to the animation
     */
    public void notifyEndAnimation(CardUpdate cardUpdate)
    {
        if (cardUpdate != null)
        {
            gameModel.setLastEndedAnimation(cardUpdate.hashCode());
        }
    }

    /**
     * Launch a Tarot game from menu
     * @since   v0.11
     */
    public void launchGame() {
        try {
            gameModel = new GameModel(dealerChoosingEnabled);
            gameView = new GameView(new Group(), gameModel, this);
            gameModel.createCards();
            window.setTitle("JACQUOT JOLIVET S3A - GAME");
            window.setScene(gameView);
            window.setMaximized(true);
            window.show();
            gameModel.getGameThread().start();
        } catch (CardGroupNumberException e) {
            e.getMessage();
        }
    }

    /**
     * Quit app from menu
     * @since   v0.11
     */
    public void quit() {
        window.close();
    }


    //SETTERS - no documentation needed

    public void setDealerChoosingEnabled(boolean on) {
        this.dealerChoosingEnabled = on;
    }

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }
}
