package tarotCardDistribution.view;

import javafx.scene.Node;
import javafx.scene.transform.Rotate;

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

    public Transformations(Node object)
    {
        rotateX = new Rotate(0, Rotate.X_AXIS);
        rotateY = new Rotate(0, Rotate.Y_AXIS);
        rotateZ = new Rotate(0, Rotate.Z_AXIS);
        object.getTransforms().addAll(rotateX, rotateY, rotateZ);
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
}
