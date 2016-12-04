package app.view;

import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;

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
        this.setNearClip(1);
        this.setFarClip(10000);
        transformations = new Transformations(this);
    }


    //GETTERS - no documentation needed

    public Transformations getTransformations()
    {
        return transformations;
    }
}
