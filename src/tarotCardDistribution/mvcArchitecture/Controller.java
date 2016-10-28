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

package tarotCardDistribution.mvcArchitecture;

/**
 * The {@code Controller} class consists in the MVC architecture controller
 * @author Arthur
 * @version v0.2
 * @since v0.2
 */
public class Controller {
    private Model model;
    private View view;

    public void setModel(Model model) {
        this.model = model;
    }
    public void setView(View view) {
        this.view = view;
    }
}
