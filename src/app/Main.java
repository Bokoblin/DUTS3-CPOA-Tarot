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
import app.view.AppView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The {@code Main} class inits and launch game
 *
 * The app follows an MVP Architecture :
 * GameModel notify its view observer, AppView
 * AppView send user request to presenter, AppPresenter
 * AppPresenter change the model, GameModel
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
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        GameModel gameModel = new GameModel();
        AppPresenter appPresenter = new AppPresenter();
        AppView scene = new AppView(root, gameModel, appPresenter);
        appPresenter.setGameModel(gameModel);
        appPresenter.setAppView(scene);
        gameModel.createCards();

        primaryStage.setTitle("JACQUOT JOLIVET S3A");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        scene.setFill(Color.BLACK);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        gameModel.getGameThread().start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
