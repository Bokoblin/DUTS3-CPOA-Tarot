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

package tarotCardDistribution.mvcArchitecture;

import javafx.scene.Group;
import javafx.scene.Scene;
import tarotCardDistribution.viewClasses.ViewCamera;
import tarotCardDistribution.viewClasses.ViewCard;

import java.util.Observable;
import java.util.Observer;

/**
 * The {@code View} class consists in the MVC architecture view
 * @author Arthur
 * @version v0.3
 * @since v0.2
 *
 * @see Observer
 * @see Scene
 */
public class View extends Scene implements Observer{
    private Model model;
    private Controller controller;
    private Group root3d;
    private Group rootGUI;

    /**
     * Constructs a view for a specific root node and with a model and a controller
     * @since v0.1
     * @version v0.3
     * @param model the model it reads
     * @param controller the controller it sends event information
     */
    public View(Group root, Model model, Controller controller) {
        super(root, 800, 600);
        root3d = new Group();
        rootGUI = new Group();
        root.getChildren().addAll(root3d, rootGUI);
        root3d.getChildren().add(new ViewCard(100, 200, 2, "file:./res/testCarte.jpg"));
        this.setCamera(new ViewCamera(true));
        this.getViewCamera().setTranslateX(50);
        this.getViewCamera().setTranslateY(100);
        this.getViewCamera().setTranslateZ(-200);
        this.model = model;
        this.controller = controller;
        model.addObserver(this);
    }

    /**
     * This method is called whenever the observed object is changed.
     * It determines which object of the model has been changed with arg
     * parameter and updates view in consequence
     * @since v0.2
     *
     * @param   o     the observable object.
     * @param   arg   an argument passed to the <code>notifyObservers</code> method.
     */
    @Override
    public void update(Observable o, Object arg) {

    }

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
