package exceptions;

import tarotCardDistribution.view.UpdateViewCard;

public class ViewCardUpdateExistException extends Exception
{
    public ViewCardUpdateExistException(UpdateViewCard updateViewCard, boolean existExcepted)
    {
        super(createMessage(updateViewCard, existExcepted));
    }

    private static String createMessage(UpdateViewCard updateViewCard, boolean existExcepted)
    {
        String message = "Error when updating the card " + updateViewCard.getCard().getName() + "(" + updateViewCard.getType().toString() + "), the related View Card was excepted to ";
        if (existExcepted)
        {
            message += "exist.";
        } else {
            message += "not exist.";
        }
        return message;
    }
}
