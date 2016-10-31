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
import javafx.stage.Stage;
import tarotCardDistribution.mvcArchitecture.Controller;
import tarotCardDistribution.mvcArchitecture.Model;
import tarotCardDistribution.mvcArchitecture.View;
import tarotCardDistribution.viewClasses.ViewCard;

/**
 * The {@code Main} class inits MVC architecture and launch the tarotCardDistribution
 * @author Arthur
 * @version v0.3
 * @since v0.2
 *
 * @see Application
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        Model model = new Model();
        Controller controller = new Controller();
        View scene = new View(root, model, controller);
        controller.setModel(model);
        controller.setView(scene);

        primaryStage.setTitle("JACQUOT JOLIVET S3A");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
