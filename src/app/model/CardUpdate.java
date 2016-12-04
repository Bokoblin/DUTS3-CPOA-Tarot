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

import app.presenter.AppPresenter;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

/**
 * This class is a container which is passed when calling notifyObservers() method.
 * It indicate to view what action to perform on a specific card with sometimes a specific cardGroup.
 * @author Alexandre
 * @version v0.9
 * @since v0.6
 */
public class CardUpdate {

    private CardGroup cardGroup;
    private Card card;
    private ActionPerformedOnCard type;
    private boolean animationFinished = false;
    private ArrayList<CardUpdate> subUpdates = new ArrayList<>();


    /**
     * Constructs CardUpdate with a card and a type
     * @since v0.6
     *
     * @param card the model card
     * @param type the type
     */
    public CardUpdate(ActionPerformedOnCard type, @NotNull Card card) {
        if (type == ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS || type == ActionPerformedOnCard.SHUFFLE_CARDS
                || type == ActionPerformedOnCard.SPREAD_CARDS || type == ActionPerformedOnCard.GATHER_CARDS
                || type == ActionPerformedOnCard.CUT_DECK)
        {
            System.err.println("Cannot do the specific action : " + type.toString() + " without specifying the group. The update will be canceled.");
            this.card = null;
            this.cardGroup = null;
            this.type = null;
        }
        this.card = card;
        this.cardGroup = null;
        this.type = type;
    }


    /**
     * Constructs CardUpdate with a card, a group and a type
     * Setting a null group will affect the card to the default view group
     * @since v0.6
     *@param type the type
     * @param card the model card
     * @param group the cardGroup
     */
    public CardUpdate(ActionPerformedOnCard type, @NotNull Card card, CardGroup group)
    {
        this.cardGroup = group;
        this.card = card;
        this.type = type;
    }


    /**
     * Constructs CardUpdate with a group and a type
     * @since v0.7
     *
     * @param type the type
     * @param cardGroup the cardGroup
     */
    public CardUpdate(ActionPerformedOnCard type, @NotNull CardGroup cardGroup) {
        if (type == ActionPerformedOnCard.MOVE_CARD_BETWEEN_GROUPS  || type == ActionPerformedOnCard.ADD_CARD ||
                type == ActionPerformedOnCard.REMOVE_CARD_FROM_GROUP || type == ActionPerformedOnCard.DELETE_CARD
                || type == ActionPerformedOnCard.CUT_DECK) {
            System.err.println("Cannot do the specific action : " + type.toString()
                    + " without specifying the card. The update will be canceled.");
            this.card = null;
            this.cardGroup = null;
            this.type = null;
        }
        this.cardGroup = cardGroup;
        this.card = null;
        this.type = type;
    }

    public void addSubUpdate(CardUpdate cardUpdate)
    {
        subUpdates.add(cardUpdate);
    }

    public synchronized void setAnimationFinished()
    {
        animationFinished = true;
        notifyAll();
    }

    public synchronized void waitAnimations(AppPresenter appPresenter)
    {
        CardUpdate thisCU = this;
        class WaitThread extends Thread
        {
            @Override
            public synchronized void run()
            {
                while (!checkUpdatesFinished(thisCU))
                {
                    try {
                        //wait();       because doesn't want to work...
                        sleep(10);
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                }
                appPresenter.notifyEndAnimation(thisCU);
            }

            boolean checkUpdatesFinished(CardUpdate cardUpdate)
            {
                if (cardUpdate.subUpdates.size() == 0)
                {
                    return cardUpdate.animationFinished;
                } else {
                    boolean isFinished = true;
                    for (CardUpdate children : cardUpdate.subUpdates)
                    {
                        isFinished = isFinished && checkUpdatesFinished(children);
                        if (isFinished == false)
                        {
                            break;
                        }
                    }
                    return isFinished;
                }
            }
        }
        WaitThread thread = new WaitThread();
        thread.start();
    }

    //GETTERS - no documentation needed

    public CardGroup getCardGroup() {
        return cardGroup;
    }
    public Card getCard() {
        return card;
    }
    public ActionPerformedOnCard getType() {
        return type;
    }
}
