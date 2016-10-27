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

package exceptions;

/**
 * Exception class on CardGroup number limit
 * @author Arthur
 * @version v0.1
 * @since v0.1
 */
public class CardGroupInstancesNumberException extends Exception {

    /**
     * Exception fired when user tries to instantiate
     * too much time the same CardGroup type
     * @since v0.1
     *
     * @param message personalized message
     */
    public CardGroupInstancesNumberException(String message) {
        super("Exception : " + message);
    }

    /**
     * Exception fired when user tries to instantiate
     * too much time the same CardGroup type
     * @since v0.1
     *
     * @param message personalized message
     * @param nbMax max number of cards possible
     */
    public CardGroupInstancesNumberException(String message, int nbMax) {
        super("Exception : " + message + " Max is " + String.valueOf(nbMax));
    }
}
