package Sigurd.Questions;

import Cards.*;
import Sigurd.*;

public abstract class AbstractQuestion {

    private boolean isActive;
    private boolean doneAsking;
    protected Player asker;
    protected boolean canAsk;

    protected PlayerCard character = null;
    protected WeaponCard weapon = null;
    protected RoomCard room = null;

    protected AbstractQuestion(Player asker) {
        this.asker = asker;
        isActive = false;
        canAsk = false;
    }

    public boolean IsActive() {
        return isActive;
    }

    public boolean CanAsk() {
        return canAsk;
    }
    
    public void SetCanAsk()
    {
        canAsk = true;
    }

    public boolean HasBeenAsked() {
        return isActive || doneAsking;
    }
    
    protected boolean HasCharacter() {
        return character != null;
    }
    
    protected boolean HasWeapon() {
        return weapon != null;
    }
    
    protected boolean HasRoom() {
        return room != null;
    }
    
    public void Commands(String command) {
        boolean helperCommand = true;
        switch (command) {
        case "characters":
            Game.GetDisplay().SendMessage("The possible characters are:");
            PrintNames(Reasource.GetCharacterNames());
            break;
        case "weapons":
            Game.GetDisplay().SendMessage("The possible weapons are:");
            PrintNames(Reasource.GetWeaponNames());
            break;
        case "rooms":
            Game.GetDisplay().SendMessage("The possible rooms are:");
            PrintNames(Reasource.GetRoomNames());
            break;
        default:
            helperCommand = false;
            break;
        }

        if (helperCommand == false) {
            TakeCardInput(command);
        }
        if (doneAsking == false)
            DisplayInputPrompt();
    }

    private void TakeCardInput(String command) {
        if (HasCharacter() == false) {
            if (Game.DoesCharacterExist(command) == false)
                Game.GetDisplay().SendError(command + " is not a valid character");
            else {
                character = Game.GetCard(command, PlayerCard.class);
                ValidInput();
            }
        } else if (HasWeapon() == false) {
            if (Game.DoesWeaponExist(command) == false)
                Game.GetDisplay().SendError(command + " is not a valid weapon");
            else {
                weapon = Game.GetCard(command, WeaponCard.class);
                ValidInput();
            }
        } else if (HasRoom() == false) {
            if (Game.DoesRoomExist(command) == false)
                Game.GetDisplay().SendError(command + " is not a valid room");
            else {
                room = Game.GetCard(command, RoomCard.class);
                ValidInput();
            }
        } else
            throw new IllegalStateException("All cards have been entered");
    }

    private void PrintNames(Iterable<String> names) {
        StringBuilder sb = new StringBuilder();

        for (String string : names) {
            sb.append(string + "\n");
        }
        Game.GetDisplay().SendMessage(sb.toString().trim());
    }

    private void ValidInput() {
        if (HasCharacter() && HasRoom() && HasWeapon()) {
            doneAsking = true;
            DoneWithInput();
        }
    }

    private void DisplayInputPrompt() {
        if (HasCharacter() == false)
            Game.GetDisplay().SendMessage("Please input a character name");
        else if (HasWeapon() == false)
            Game.GetDisplay().SendMessage("Please input a weapon name");
        else if (HasRoom() == false)
            Game.GetDisplay().SendMessage("Please input a room name");
        else
            throw new IllegalStateException("All inputs allready taken");

    }

    protected void Activate() {
        if (canAsk == false)
            throw new IllegalStateException("Asking question when not allowed to");
        canAsk = false;
        isActive = true;
        doneAsking = false;
        DisplayInputPrompt();
    }

    protected void Deactivate() {
        if (isActive == false)
            throw new IllegalStateException("Question is not active");
        isActive = false;
        doneAsking = true;
    }

    protected abstract void DoneWithInput();
}
