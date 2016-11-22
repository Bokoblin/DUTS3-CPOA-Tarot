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

package app.controller;

import app.model.GameModel;
import app.view.AppView;

/**
 * The {@code AppController} class consists in the MVC architecture controller
 * @author Arthur
 * @version v0.2
 * @since v0.2
 */
public class AppController {
    private GameModel gameModel;
    private AppView appView;


    //SETTERS - no documentation needed

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }
    public void setAppView(AppView appView) {
        this.appView = appView;
    }
}