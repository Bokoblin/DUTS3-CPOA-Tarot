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

import app.model.GameModel;
import app.view.GameView;
import app.view.MenuView;
import exceptions.CardGroupNumberException;
import javafx.scene.Group;
import javafx.stage.Stage;

/**
 * The {@code AppPresenter} class consists in the MVC architecture presenter
 * @author Arthur
 * @version v0.10
 * @since v0.2
 */
public class AppPresenter {
    private GameModel gameModel;
    private Stage window;
    private GameView gameView;
    private MenuView menuView;
    private boolean gameModeSimplified;

    public AppPresenter(Stage window) {
        this.window = window;
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

    public void launchMenu() {
        gameView = null;
        window.setTitle("JACQUOT JOLIVET S3A - MENU");
        window.setMaximized(false);
        window.setScene(menuView);
        window.show();
    }

    public void launchGame() {
        try {
            window.hide();
            if ( gameModel == null)
            gameModel = new GameModel(gameModeSimplified);
            gameView = new GameView(new Group(), gameModel, this);
            gameModel.createCards();
            window.setTitle("JACQUOT JOLIVET S3A - GAME");
            window.setScene(gameView);
            window.show();
            gameModel.getGameThread().start();
        } catch (CardGroupNumberException e) {
            e.getMessage();
        }
    }

    public void quit() {
        window.close();
    }


    //SETTERS - no documentation needed

    public void setMenuView(MenuView menuView) {
        this.menuView = menuView;
    }
    public void setGameModeSimplified(boolean on) {
        this.gameModeSimplified = on;
    }
}
