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

import app.presenter.AppPresenter;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Observer;


/**
 * The {@code MenuView} class consists in one
 * of the MVP architecture view
 * It contains the menu elements
 * @author Arthur
 * @version v1.0.0
 * @since v0.10
 *
 * @see Observer
 * @see Scene
 */
public class MenuView extends Scene {

    private static final Point3D CAMERA_POSITION = new Point3D(1250, 4200, -3800);
    private static final double CAMERA_ROTATION = 35;

    private AppPresenter appPresenter;

    /**
     * Constructs a view for a specific root node and with a model and a presenter
     * @since   v0.11
     * @param   root        the root node
     * @param   controller  the presenter it sends event information
     */
    public MenuView(Group root, AppPresenter controller) {
        super(root, 800, 600, true, SceneAntialiasing.BALANCED);

        this.appPresenter = controller;
        this.setFill(Color.BLACK);

        Group root3D = new Group();
        BorderPane rootGUI = new BorderPane();

        HBox boxTop = new HBox();
        VBox boxCenter = new VBox(10);
        VBox boxBottom = new VBox();

        boxCenter.setAlignment(Pos.CENTER);
        boxTop.setAlignment(Pos.CENTER);
        boxBottom.setAlignment(Pos.CENTER);
        rootGUI.setTop(boxTop);
        rootGUI.setBottom(boxBottom);
        rootGUI.setCenter(boxCenter);
        rootGUI.prefWidthProperty().bind(widthProperty());
        rootGUI.prefHeightProperty().bind(heightProperty());
        rootGUI.setPickOnBounds(false);
        for (Node node: rootGUI.getChildren()) {
            node.setPickOnBounds(false);
            ((Pane)node).setPadding(new Insets(10, 10, 10, 10));
        }

        SubScene subScene3D = new SubScene(root3D, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene3D.widthProperty().bind(widthProperty());
        subScene3D.heightProperty().bind(heightProperty());

        ImageView table = new ImageView("file:./res/table.jpg");
        table.setScaleX(3);
        table.setScaleY(3);

        //=== Define the camera

        ViewCamera camera3D = new ViewCamera(true);
        camera3D.moveCamera(CAMERA_POSITION, CAMERA_ROTATION, 0);
        subScene3D.setCamera(camera3D);

        //=== Create GUI elements

        Label title = new Label("Project Tarot");
        title.setTextFill(Color.WHITE);
        title.setMinSize(100, 30);
        title.setFont(new Font(40));

        Button playButton = new Button("PLAY");
        playButton.setMinSize(110, 40);
        playButton.setOnAction(event -> appPresenter.launchGame());
        playButton.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        playButton.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        Button quitButton = new Button("QUIT");
        quitButton.setMinSize(110, 40);
        quitButton.setOnAction(event -> appPresenter.quit());
        quitButton.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        quitButton.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        CheckBox dealerChoosingCheckBox = new CheckBox();
        dealerChoosingCheckBox.setText("Enable Dealer choosing");
        dealerChoosingCheckBox.setTextFill(Color.WHITE);
        dealerChoosingCheckBox.setFont(new Font(15));
        dealerChoosingCheckBox.setSelected(false);
        dealerChoosingCheckBox.setOnAction(event -> appPresenter.setDealerChoosingEnabled(
                dealerChoosingCheckBox.selectedProperty().getValue()));
        dealerChoosingCheckBox.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        dealerChoosingCheckBox.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );
        appPresenter.setDealerChoosingEnabled(dealerChoosingCheckBox.selectedProperty().getValue());

        Label credit = new Label("2016 Jacquot Alexandre, Jolivet Arthur");
        credit.setTextFill(Color.WHITE);
        credit.setFont(new Font(20));
        credit.setPadding(new Insets(60, 10, 10, 10));

        //=== Add elements to groups

        root3D.getChildren().add(table);
        boxTop.getChildren().add(title);
        boxCenter.getChildren().addAll(playButton, quitButton );
        boxBottom.getChildren().addAll(dealerChoosingCheckBox, credit);
        root.getChildren().addAll(subScene3D, rootGUI);
    }

}