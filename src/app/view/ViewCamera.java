package app.view;

import javafx.scene.PerspectiveCamera;

/**
 * The {@code ViewCamera} class contain the camera of the scene.
 * @author Alexandre
 * @version v0.4
 * @since v0.4
 */
public class ViewCamera extends PerspectiveCamera {
    private Transformations transformations;

    public ViewCamera(boolean fixedEyedAtCameraZero)
    {
        super(fixedEyedAtCameraZero);
        this.setFarClip(100000);
        transformations = new Transformations(this);
    }


    //GETTERS - no documentation needed

    public Transformations getTransformations()
    {
        return transformations;
    }
}
