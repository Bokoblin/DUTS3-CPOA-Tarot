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

package app;

import app.model.GameModel;
import app.presenter.AppPresenter;
import app.view.GameView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The {@code Main} class inits and launch game
 *
 * The app follows an MVP Architecture :
 * GameModel notifies change to GameView (its observer)
 * GameView sends user request AppPresenter and send state request to model
 * AppPresenter changes GameModel fields
 *
 * Our app implements a variant of MVP from CPOA TD2 (page 3)
 *
 * @author Alexandre
 * @author Arthur
 * @version v0.10
 * @since v0.2
 *
 * @see Application
 */
public class Main extends Application {
    @Override
    public void start(Stage window) throws Exception{
        Group root = new Group();
        GameModel gameModel = new GameModel(true);
        AppPresenter appPresenter = new AppPresenter();
        GameView scene = new GameView(root, gameModel, appPresenter);
        appPresenter.setGameModel(gameModel);
        appPresenter.setGameView(scene);
        gameModel.createCards();

        window.setTitle("JACQUOT JOLIVET S3A");
        window.setMaximized(true);
        window.setScene(scene);
        scene.setFill(Color.BLACK);
        window.show();

        window.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        gameModel.getGameThread().start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
