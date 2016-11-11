package tarotCardDistribution.view;

public enum UpdateViewCardType {
    ADDNEWCARD,                     //Create the ViewCard, a group can be set on the creation.
    TURNBACKTCARD,                  //Turn back the card on the view.
    CHANGECARDGROUP,                //Change the card group, it will start an animation on the view. The card must have been created before !
    REMOVETHECARDFROMCURRENTGROUP,  //It will put the card on a free place on the table
    DELETECARD;                     //Delete the ViewCard
}
