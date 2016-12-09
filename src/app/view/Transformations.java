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

package app.view;

import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * The {@code Transformations} class contain a list of basics transformations objects to add to the nodes.
 * @author Alexandre
 * @version v1.0.0
 * @since v0.4
 */
public class Transformations {
    private Rotate rotateX;
    private Rotate rotateY;
    private Rotate rotateZ;
    private Rotate incline;
    private Translate translate;

    /**
     * Constructs a Transformation with a node
     * @since v0.4
     *
     * @param object the node we add transformation getters
     * @see Node
     */
    public Transformations(Node object)
    {
        double pivotX = object.getBoundsInLocal().getWidth()/2;
        double pivotY = object.getBoundsInLocal().getHeight()/2;
        double pivotZ = object.getBoundsInLocal().getDepth()/2;
        rotateX = new Rotate(0, pivotX, pivotY, pivotZ, Rotate.X_AXIS);
        rotateY = new Rotate(0, pivotX, pivotY, pivotZ, Rotate.Y_AXIS);
        rotateZ = new Rotate(0, pivotX, pivotY, pivotZ, Rotate.Z_AXIS);
        incline = new Rotate(0, pivotX, object.getBoundsInLocal().getDepth(), pivotZ, Rotate.Y_AXIS);
        translate = new Translate(0,0,0);
        object.getTransforms().addAll(translate, incline, rotateX, rotateY, rotateZ); //The order of adding the transformations is important !
    }

    //GETTERS - no documentation needed

    public Rotate getRotateX()
    {
        return rotateX;
    }
    public Rotate getRotateY()
    {
        return rotateY;
    }
    public Rotate getIncline() {
        return incline;
    }
    public Translate getTranslate()
    {
        return translate;
    }
}
