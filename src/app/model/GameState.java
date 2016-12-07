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
 * The {@code GameState} enumeration
 * defines all steps that can occur
 * @author Arthur
 * @version v0.10
 * @since v0.10
 */
public enum GameState {
    CARDS_SPREADING,    //Cards are spreaded to choose dealer
    DEALER_CHOOSING,    //Players have to choose a card
    DEALER_CHOSEN,      //Dealer has been chosen, player can move to dealing
    CARDS_DEALING,      //Cards are dealt
    PETIT_SEC_DETECTED, //Petit sec configuration has been detected
    BID_CHOOSING,       //Player must choose his bid among the 4 + "Pass"
    BID_CHOSEN,         //Bid has been chosen, player can constitute ecart, re-deal or quit
    ECART_CONSTITUTING, //Player has to choose 6 cards for the Ecart
    ECART_CONSTITUTED, ENDED;  //Ecart has been chosen, player can quit
}
