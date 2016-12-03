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

package app.model;

/**
 * The {@code NotificationType} enumeration defines type
 * of notification that are sent through notifyObserver.
 * This allows Observer to have a personalized action depending
 * on notification type
 * @author Arthur
 * @version v0.10
 * @since v0.8
 */
public enum NotificationType {
    PICK_CARD,                  //To choose a card among spreaded ones (Dealer designation)
    CHOOSE_BID,                 //To choose a bid among those shown
    CHOOSE_ECART_CARD,          //To choose a card among blinking ones (Ecart constitution)
    UNAUTHORIZED_CARD_CHOICE    //To display an error message

}
