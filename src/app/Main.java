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

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import app.controller.AppController;
import app.model.GameModel;
import app.view.AppView;

/**
 * The {@code Main} class inits MVC architecture and launch the architecture
 * @author Alexandre
 * @author Arthur
 * @version v0.6
 * @since v0.2
 *
 * @see Application
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        GameModel gameModel = new GameModel();
        AppController appController = new AppController();
        AppView scene = new AppView(root, gameModel, appController);
        appController.setGameModel(gameModel);
        appController.setAppView(scene);
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
