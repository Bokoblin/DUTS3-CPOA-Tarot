package exceptions;

import tarotCardDistribution.model.CardUpdate;

public class ViewCardUpdateExistException extends Exception
{
    public ViewCardUpdateExistException(CardUpdate cardUpdate, boolean existExcepted)
    {
        super(createMessage(cardUpdate, existExcepted));
    }

    private static String createMessage(CardUpdate cardUpdate, boolean existExcepted)
    {
        String message = "Error when updating the card " + cardUpdate.getCard().getName() + "(" + cardUpdate.getType().toString() + "), the related View Card was excepted to ";
        if (existExcepted)
        {
            message += "exist.";
        } else {
            message += "not exist.";
        }
        return message;
    }
}