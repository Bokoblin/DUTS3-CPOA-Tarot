package sample;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private Scene scene;
    private RotateTransition rotateTransition;
    private ParallelTransition parallelTransition;

    static MeshView create3DRectangle(float width, float height, float deep)
    {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(
                0, 0, deep,      //P0
                width, 0, deep,    //P1
                0, height, deep,    //P2
                width, height, deep,  //P3
                0, 0, 0,        //P4
                width, 0, 0,      //P5
                0, height, 0,      //P6
                width, height, 0     //P7
        );

        mesh.getTexCoords().addAll(
                (20f/3112f), 0,       //T0
                (1556f/3112f), 0,        //T1
                0, (20f/3112f),       //T2
                (20f/3112f), (20f/2703f),   //T3
                (1556f/3112f), (20f/2703f),    //T4
                (1576f/3112f), (20f/2703f),   //T5
                1, (20f/2703f),       //T6
                0, (2683f/2703f),        //T7
                (20f/3112f), (2683f/2703f),    //T8
                (1556f/3112f), (2683f/2703f),     //T9
                (1576f/3112f), (2683f/2703f),    //T10
                1, (2683f/2703f),        //T11
                (20f/3112f), 1,   //T12
                (1556f/3112f), 1     //T13
        );

        mesh.getFaces().addAll(
                5,1,4,0,0,3     //P5,T1 ,P4,T0  ,P0,T3
                ,5,1,0,3,1,4    //P5,T1 ,P0,T3  ,P1,T4
                ,0,3,4,2,6,7    //P0,T3 ,P4,T2  ,P6,T7
                ,0,3,6,7,2,8    //P0,T3 ,P6,T7  ,P2,T8
                ,1,4,0,3,2,8    //P1,T4 ,P0,T3  ,P2,T8
                ,1,4,2,8,3,9    //P1,T4 ,P2,T8  ,P3,T9
                ,5,5,1,4,3,9    //P5,T5 ,P1,T4  ,P3,T9
                ,5,5,3,9,7,10   //P5,T5 ,P3,T9  ,P7,T10
                ,4,6,5,5,7,10   //P4,T6 ,P5,T5  ,P7,T10
                ,4,6,7,10,6,11  //P4,T6 ,P7,T10 ,P6,T11
                ,3,9,2,8,6,12   //P3,T9 ,P2,T8  ,P6,T12
                ,3,9,6,12,7,13  //P3,T9 ,P6,T12 ,P7,T13
        );

        MeshView meshView = new MeshView();
        meshView.setMesh(mesh);
        return meshView;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("La super carte!");
        Group root = new Group();
        Scene scene = new Scene(root, 600, 600, true, SceneAntialiasing.BALANCED);
        primaryStage.setScene(scene);
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(new Image("file:./ressources/testCarte.jpg"));
        MeshView rectangle = create3DRectangle(200,284,5);
        rectangle.setMaterial(mat);
        root.getChildren().add(rectangle);
        primaryStage.show();

        rotateTransition = new RotateTransition(Duration.seconds(12), root);
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(360);
        rotateTransition.setAutoReverse(true);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);

        parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(rotateTransition);
        parallelTransition.setCycleCount(Timeline.INDEFINITE);
        parallelTransition.play();
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
