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
 * @version v0.10
 * @since v0.6
 */
public class CardUpdate {

    private CardGroup cardGroup;
    private Card card;
    private CardUpdateType type;
    private boolean animationFinished = false;
    private ArrayList<CardUpdate> subUpdates = new ArrayList<>();


    /**
     * Constructs CardUpdate with a card and a type
     * @since v0.6
     *
     * @param card the model card
     * @param type the type
     */
    public CardUpdate(CardUpdateType type, @NotNull Card card) {
        if (type == CardUpdateType.MOVE_CARD_BETWEEN_GROUPS || type == CardUpdateType.SHUFFLE_CARDS
                || type == CardUpdateType.SPREAD_CARDS || type == CardUpdateType.GATHER_CARDS
                || type == CardUpdateType.CUT_DECK)
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
    public CardUpdate(CardUpdateType type, @NotNull Card card, CardGroup group)
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
    public CardUpdate(CardUpdateType type, @NotNull CardGroup cardGroup) {
        if (type == CardUpdateType.MOVE_CARD_BETWEEN_GROUPS  || type == CardUpdateType.ADD_CARD ||
                type == CardUpdateType.REMOVE_CARD_FROM_GROUP || type == CardUpdateType.DELETE_CARD
                || type == CardUpdateType.CUT_DECK) {
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

    /**
     * Add a sub-Update to cardUpdate
     * @since v0.9
     *
     * @param cardUpdate the cardUpdate on which to add sub-update
     */
    public void addSubUpdate(CardUpdate cardUpdate)
    {
        subUpdates.add(cardUpdate);
    }

    /**
     * Set an animation as finished for model to resume its logic
     * @since v0.9
     */
    public void setAnimationFinished()
    {
        synchronized (CardUpdate.class)
        {
            animationFinished = true;
            CardUpdate.class.notifyAll();
        }
    }

    /**
     * Wait animation to finish before resuming
     * @since v0.9
     *
     * @param appPresenter the MVP presenter
     */
    public void waitAnimations(AppPresenter appPresenter)
    {
        CardUpdate thisCU = this;
        class WaitThread extends Thread
        {
            @Override
            public void run()
            {
                synchronized (CardUpdate.class)
                {
                    while (!checkUpdatesFinished(thisCU)) {
                        try {
                            CardUpdate.class.wait();
                        } catch (InterruptedException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                    appPresenter.notifyEndAnimation(thisCU);
                }
            }

            private boolean checkUpdatesFinished(CardUpdate cardUpdate)
            {
                if (cardUpdate.subUpdates.size() == 0)
                {
                    return cardUpdate.animationFinished;
                } else {
                    boolean isFinished = true;
                    for (CardUpdate children : cardUpdate.subUpdates)
                    {
                        isFinished = isFinished && checkUpdatesFinished(children);
                        if (!isFinished)
                            break;
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
    public CardUpdateType getType() {
        return type;
    }
}
