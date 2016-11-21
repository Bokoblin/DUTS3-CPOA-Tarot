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

package app.model;

/**
 * The {@code ActionPerformedOnCard} enumeration defines actions
 * that are done on cards. They are passed in model:notifyObserver()
 * to inform the view to do the similar action with its ViewCards
 * @author Arthur
 * @author Alexandre
 * @version v0.6
 * @since v0.6
 */
public enum ActionPerformedOnCard {
    ADD_CARD,                   //To create a new ViewCard
    TURN_CARD,                  //To turn a card (shown/hidden) - card must exist
    MOVE_CARD_BETWEEN_GROUPS,   //To move a card between two groups - card must exist
    REMOVE_CARD_FROM_GROUP,     //To move a card from a group to the table - card must exist
    DELETE_CARD                 //To delete the related ViewCard - card must exist
}
