package tarotCardDistribution.view;

//TODO : Documentation

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class RectangleMesh extends MeshView{
    private Transformations transformations;

    RectangleMesh(float width, float height, float depth, String texturePath, float textureFaceWidth, float textureFaceHeight)
    {
        super();
        float textureWidth = 0;
        float textureHeight = 0;
        javafx.scene.image.Image image;
        image = new javafx.scene.image.Image(texturePath);
        textureHeight = (float)image.getHeight();
        textureWidth = (float)image.getWidth();
        float textureDepth = (textureHeight - textureFaceHeight)/2;


        TriangleMesh mesh = new TriangleMesh();

        mesh.getPoints().addAll(
                0, 0, 0,        //P0
                width, 0, 0,      //P1
                0, height, 0,      //P2
                width, height, 0,     //P3
                0, 0, depth,      //P4
                width, 0, depth,    //P5
                0, height, depth,    //P6
                width, height, depth //P7
        );

        mesh.getTexCoords().addAll(
                (textureDepth/textureWidth), 0,       //T0
                ((textureDepth+textureFaceWidth)/textureWidth), 0,        //T1
                0, (textureDepth/textureWidth),       //T2
                (textureDepth/textureWidth), (textureDepth/textureHeight),   //T3
                ((textureDepth+textureFaceWidth)/textureWidth), (textureDepth/textureHeight),    //T4
                ((2*textureDepth+textureFaceWidth)/textureWidth), (textureDepth/textureHeight),   //T5
                1, (textureDepth/textureHeight),       //T6
                0, ((textureDepth+textureFaceHeight)/textureHeight),        //T7
                (textureDepth/textureWidth), ((textureDepth+textureFaceHeight)/textureHeight),    //T8
                ((textureDepth+textureFaceWidth)/textureWidth), ((textureDepth+textureFaceHeight)/textureHeight),     //T9
                ((2*textureDepth+textureFaceWidth)/textureWidth), ((textureDepth+textureFaceHeight)/textureHeight),    //T10
                1, ((textureDepth+textureFaceHeight)/textureHeight),        //T11
                (textureDepth/textureWidth), 1,   //T12
                ((textureDepth+textureFaceWidth)/textureWidth), 1     //T13
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
        this.setMesh(mesh);

        PhongMaterial material = null;
        material = new PhongMaterial();
        material.setDiffuseMap(image);
        this.setMaterial(material);

        transformations = new Transformations(this);
    }

    public Transformations getTransformations()
    {
        return transformations;
    }
}