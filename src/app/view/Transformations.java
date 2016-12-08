package app.view;

import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * The {@code Transformations} class contain a list of basics transformations objects to add to the nodes.
 * @author Alexandre
 * @version v0.4
 * @since v0.4
 */
public class Transformations {
    private Rotate rotateX;
    private Rotate rotateY;
    private Rotate rotateZ;
    private Rotate incline;
    private Translate translate;

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
    public Rotate getRotateZ()
    {
        return rotateZ;
    }

    public Rotate getIncline() {
        return incline;
    }

    public Translate getTranslate()
    {
        return translate;
    }
}
