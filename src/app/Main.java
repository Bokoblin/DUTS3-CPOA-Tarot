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
 * The {@code Main} class inits MVC architecture and launch the architecture
 * @author Alexandre
 * @author Arthur
 * @version v0.8.1
 * @since v0.2
 *
 * @see Application
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Platform.setImplicitExit(false);
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

        Thread gameThread = new Thread(() -> {
            gameModel.chooseInitialDealer();
            gameModel.handleDealing();
            gameModel.handleBids();
            System.out.println(gameModel.toString());
        });
        gameThread.start();

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
