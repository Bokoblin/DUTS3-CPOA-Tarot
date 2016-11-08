package samples.card3DAlexandreSample;

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Path;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private Scene scene;
    private SubScene subScene;
    private RotateTransition rotateTransition;
    private ParallelTransition parallelTransition;
    private Rotate rotation;
    private Rotate rotation2;

    static MeshView create3DRectangle(float width, float height, float deep)
    {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(
                0, 0, 0,        //P0
                width, 0, 0,      //P1
                0, height, 0,      //P2
                width, height, 0,     //P3
                0, 0, deep,      //P4
                width, 0, deep,    //P5
                0, height, deep,    //P6
                width, height, deep //P7
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
        Group root3d = new Group();
        scene = new Scene(root);
        subScene = new SubScene(root3d, 600, 600, true, SceneAntialiasing.BALANCED);
        primaryStage.setScene(scene);

        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(new Image("file:./res/testCarte.jpg"));
        MeshView rectangle = create3DRectangle(200,284,1);
        rectangle.setMaterial(mat);
        root3d.getChildren().add(rectangle);
        final PointLight light = new PointLight(Color.WHITE);
        root3d.getChildren().add(light);
        light.setTranslateX(100);
        light.setTranslateY(150);
        light.setTranslateZ(-50);
        light.setRotationAxis(Rotate.X_AXIS);
        light.setRotate(-90);

        Camera camera = new PerspectiveCamera(true);
        subScene.setCamera(camera);
        camera.setTranslateX(100);
        camera.setTranslateY(150);
        camera.setTranslateZ(-400);
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setFarClip(100000);

        root.getChildren().add(subScene);
        root.getChildren().add(new Label("coucou tu veux voir ma b ... anane !"));

        primaryStage.show();


        rotation = new Rotate(0, 100, 150, 0.5, Rotate.Y_AXIS);
        rectangle.getTransforms().add(rotation);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 360)),
                new KeyFrame(new Duration(6000), new KeyValue(rotation.angleProperty(), 0))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        rotation2 = new Rotate(0, 100, 150, 2, Rotate.X_AXIS);
        rectangle.getTransforms().add(rotation2);

        Timeline timeline2 = new Timeline();
        timeline2.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(rotation2.angleProperty(), 360)),
                new KeyFrame(new Duration(9000), new KeyValue(rotation2.angleProperty(), 0))
        );
        timeline2.setCycleCount(Timeline.INDEFINITE);
        timeline2.play();

        //Another way to make transition effect ...
        /*rotateTransition = new RotateTransition(Duration.seconds(12), root);
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(360);
        rotateTransition.setAutoReverse(true);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);

        parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(rotateTransition);
        parallelTransition.setCycleCount(Timeline.INDEFINITE);
        parallelTransition.play();*/

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:    camera.setTranslateY(camera.getTranslateY()+1); break;
                    case DOWN:  camera.setTranslateY(camera.getTranslateY()-1); break;
                    case RIGHT:    camera.setTranslateX(camera.getTranslateX()-1); break;
                    case LEFT:  camera.setTranslateX(camera.getTranslateX()+1); break;
                    case PAGE_UP:    camera.setTranslateZ(camera.getTranslateZ()+10); break;
                    case PAGE_DOWN:  camera.setTranslateZ(camera.getTranslateZ()-10); break;
                    case Z:    camera.setRotate(camera.getRotate()+1); break;
                    case S:  camera.setRotate(camera.getRotate()-1); break;
                }
            }
        });
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
