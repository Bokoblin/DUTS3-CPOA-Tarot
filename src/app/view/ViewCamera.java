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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point3D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * The {@code ViewCamera} class contain the camera of the scene.
 * @author Alexandre
 * @version v1.0.0
 * @since v0.4
 */
public class ViewCamera extends PerspectiveCamera {
    private Transformations transformations;

    /**
     * Constructs Perspective camera
     * @since v0.2
     *
     * @param fixedEyedAtCameraZero the eye position is fixed at (0, 0, 0)
     */
    public ViewCamera(boolean fixedEyedAtCameraZero)
    {
        super(fixedEyedAtCameraZero);
        setNearClip(1);
        setFarClip(10000);
        setRotationAxis(Rotate.X_AXIS);
        transformations = new Transformations(this);
    }

    /**
     * Moves the camera
     * @since v0.2
     *
     * @param position the new camera position
     * @param rotation the new camera rotation
     * @param transitionTime the transition length to new camera position and rotation
     */
    public void moveCamera(Point3D position, double rotation, int transitionTime)
    {
        if (getTranslateX() != position.getX() || getTranslateY() != position.getY() || getTranslateZ() != position.getZ() || rotation != getRotate())
        {
            if (transitionTime < 1)
            {
                transitionTime = 1;
            }
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(new Duration(transitionTime), new KeyValue(translateXProperty(), position.getX())),
                    new KeyFrame(new Duration(transitionTime), new KeyValue(translateYProperty(), position.getY())),
                    new KeyFrame(new Duration(transitionTime), new KeyValue(translateZProperty(), position.getZ())),
                    new KeyFrame(new Duration(transitionTime), new KeyValue(rotateProperty(), rotation))
            );
            timeline.play();
        }
    }

    //GETTERS - no documentation needed

    public Transformations getTransformations()
    {
        return transformations;
    }
}
