package app.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * The {@code ViewCamera} class contain the camera of the scene.
 * @author Alexandre
 * @version v0.4
 * @since v0.4
 */
public class ViewCamera extends PerspectiveCamera {
    private Transformations transformations;

    /**
     * Constructs Perspective camera
     * @since v0.2
     *
     * @param fixedEyedAtCameraZero see javafx doc :)
     */
    public ViewCamera(boolean fixedEyedAtCameraZero)
    {
        super(fixedEyedAtCameraZero);
        setNearClip(1);
        setFarClip(10000);
        setRotationAxis(Rotate.X_AXIS);
        transformations = new Transformations(this);
    }

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
