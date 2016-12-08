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

import app.presenter.AppPresenter;
import app.view.MenuView;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;

/**
 * The {@code Main} class inits and launch game
 *
 * The app follows an MVP Architecture :
 * GameModel notifies change to GameView (its observer)
 * GameView sends user request to AppPresenter and send state request to model
 * AppPresenter changes GameModel fields
 *
 * Our app implements a variant of MVP from CPOA TD2 (page 3)
 *
 * @author Alexandre
 * @author Arthur
 * @version v0.11
 * @since v0.2
 *
 * @see Application
 */
public class Main extends Application {
    @Override
    public void start(Stage window) throws Exception{

        Group root = new Group();
        AppPresenter appPresenter = new AppPresenter(window);
        MenuView menuView = new MenuView(root, appPresenter);

        window.setTitle("JACQUOT JOLIVET S3A - MENU");
        window.setMinWidth(800);
        window.setMinHeight(600);
        window.setScene(menuView);
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
