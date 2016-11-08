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

package tarotCardDistribution.view;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import tarotCardDistribution.controller.AppController;
import tarotCardDistribution.model.GameModel;

import java.util.Observable;
import java.util.Observer;

/**
 * The {@code AppView} class consists in the MVC architecture view
 * @author Alexandre, Arthur
 * @version v0.3
 * @since v0.2
 *
 * @see Observer
 * @see Scene
 */
public class AppView extends Scene implements Observer{
    private GameModel gameModel;
    private AppController appController;
    private Group root3d;
    private Group rootGUI;
    private Group background;

    /**
     * Constructs a view for a specific root node and with a gameModel and a appController
     * @since v0.1
     * @param gameModel the gameModel it reads
     * @param appController the appController it sends event information
     */
    public AppView(Group root, GameModel gameModel, AppController appController) {
        super(root, 800, 600, true, SceneAntialiasing.BALANCED);
        root3d = new Group();
        rootGUI = new Group();
        background = new Group();
        root.getChildren().addAll(root3d, rootGUI);
        root3d.getChildren().add(background);
        this.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                root3d.setRotationAxis(Rotate.Z_AXIS);
                switch (keyEvent.getCode())
                {
                    case D:
                        root3d.setRotate(root3d.getRotate()-1);
                        break;
                    case Q:
                        root3d.setRotate(root3d.getRotate()+1);
                        break;
                    default:
                        break;
                }
            }
        });

        //Create the scene objects
        //root3d.getChildren().add(new ViewCard(100, 200, 2, "file:./res/testCarte.jpg", 1536, 2663, null));
        RectangleMesh table = new RectangleMesh(2000, 2000, 182, "file:./res/table.jpg", 1100, 1100);
        background.getChildren().add(table);
        this.setCamera(new ViewCamera(true));
        this.getViewCamera().setTranslateX(1000);
        this.getViewCamera().setTranslateY(2400);
        this.getViewCamera().setTranslateZ(-3600);

        //Lets define the camera
        getViewCamera().getTransformations().getRotateX().setAngle(20);
        root3d.getChildren().add(new AmbientLight(Color.WHITE));
    }

    /**
     * This method is called whenever the observed object is changed.
     * It determines which object of the gameModel has been changed with arg
     * parameter and updates view in consequence
     * @since v0.2
     *
     * @param   o     the observable object.
     * @param   arg   an argument passed to the <code>notifyObservers</code> method.
     */
    @Override
    public void update(Observable o, Object arg) {

    }


    //GETTERS - no documentation needed

    public Group getRoot3d()
    {
        return root3d;
    }
    public Group getRootGUI()
    {
        return rootGUI;
    }
    public ViewCamera getViewCamera()
    {
        return (ViewCamera)getCamera();
    }
}