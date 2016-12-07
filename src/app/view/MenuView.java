/*
Copyright 2016 Jacquot Alexandre, Jolivet Arthur S3A
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
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Observer;


/**
 * The {@code MenuView} class consists in one
 * of the MVP architecture view
 * @author Arthur
 * @version v0.10
 * @since v0.10
 *
 * @see Observer
 * @see Scene
 */
public class MenuView extends Scene {

    private AppPresenter appPresenter;

    /**
     * Constructs a view for a specific root node and with a model and a presenter
     * @since   v0.10
     * @param   controller  the presenter it sends event information
     */
    public MenuView(Group root, AppPresenter controller) {
        super(root, 800, 600, true, SceneAntialiasing.DISABLED);

        this.appPresenter = controller;
        this.setFill(Color.BLACK);

        //=== Define the camera



        //=== Create GUI elements

        Label title = new Label("Tarot");
        title.setTranslateX(getWidth()/2);
        title.setTranslateY(getHeight()/2-50);
        title.setTextFill(Color.WHITE);
        title.setMinWidth(100);
        title.setMinHeight(30);
        title.setFont(new Font(40));

        Button playButton = new Button("PLAY");
        playButton.setTranslateX(getWidth()/2);
        playButton.setTranslateY(getHeight()/2);
        playButton.setMinWidth(100);
        playButton.setMinHeight(30);
        playButton.setOnAction(event -> appPresenter.launchGame());
        playButton.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        playButton.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        Button quitButton = new Button("QUIT");
        quitButton.setTranslateX(getWidth()/2);
        quitButton.setTranslateY(getHeight()/2+50);
        quitButton.setMinWidth(100);
        quitButton.setMinHeight(30);
        quitButton.setTextFill(Color.WHITE);
        quitButton.setOnAction(event -> appPresenter.quit());
        quitButton.setOnMouseEntered( event -> this.setCursor(Cursor.HAND) );
        quitButton.setOnMouseExited( event -> this.setCursor(Cursor.DEFAULT) );

        root.getChildren().addAll(playButton, quitButton);

        appPresenter.setGameModeSimplified(true);

    }

}