package Sigurd;

import java.util.*;

import Sigurd.BoardObjects.*;
import Sigurd.Questions.Assertion;
import Sigurd.Questions.Question;

/**
 * The current turn in the game
 * Team: Sigurd
 * Student Numbers:
 * 16751195, 16202907, 16375246
 * @author Adiran Wennberg
 */
public class Turn {
    private Player turnPlayer;
    private PlayerObject turnPlayerObject;
    private static final Set<String> MOVE_DIRECTIONS = new HashSet<String>(
            Arrays.asList(new String[] { "u", "d", "l", "r" }));

    private boolean canRoll;
    private boolean hasRolled;
    private int stepsLeft;
    private boolean hasEneteredRoom;
    private Question turnQuestion;
    private Assertion turnAssertion;

    public Turn(Player player, Iterable<Player> allPlayers) {
        stepsLeft = 0;
        turnPlayer = player;
        turnPlayerObject = turnPlayer.GetPlayerObject();
        
        hasEneteredRoom = false;
        canRoll = true;
        hasRolled = false;
        
        turnQuestion = new Question(player, allPlayers);
        turnAssertion = new Assertion(player);
        if (turnPlayerObject.HasMovedAfterLastTurn())
            turnQuestion.SetCanAsk();
    }

    public void Commands(String command) {

        DisplayMessage("> " + command);

        // If the player is making an assertion, pass on the input
        if (turnAssertion.IsActive())
            turnAssertion.Commands(command);

        // If the player is asking a question, pass on the input
        else if (turnQuestion.IsActive())
            turnQuestion.Commands(command);

        // If the player is in a room, check if the input was a number.
        else if (turnPlayerObject.IsInRoom() && command.length() == 1 && Character.isDigit(command.toCharArray()[0]))
            MoveOutOfRoom(Integer.parseInt(command));

        // Check if the input is a movable direction.
        else if (MOVE_DIRECTIONS.contains(command))
            MoveInDirection(command);

        else {
            switch (command) {
            case "help":
                DisplayHelp();
                break;
            case "roll":
                RollDice();
                break;
            case "passage":
                MoveThroughPassage();
                break;
            case "notes":
                DisplayMessage(turnPlayer.GetNotes());
                break;
            case "question":
                StartAskingQuestion();
                break;
            case "accuse":
                MakeAccuastion();
                break;
            case "cheat":
                DisplayMessage("You're not allowed to cheat.\n" + "Type #cheat if you want to ruin all the fun.");
                break;
            case "done":
                Game.NextTurn();
                break;
            case "quit":
                turnPlayer.KnockOutOfGame();
                Game.NextTurn();
                break;
            default:
                DisplayError("That is not a valid command.");
                break;
            }
        }
    }

    private void DisplayHelp() {
        DisplayMessage("Type \"roll\" to roll your dice \n"
                + "Type, d, l or r to move up, down, left, or right respectively \n"
                + "If you are in a room at the start of your turn, after "
                + "rolling, type the number corresponding to an exit to leave, "
                + "or type \"passage\" to use a secret passage\n" + "Type \"notes\" to see the cards that you own\n"
                + "If you have just entered a room, type \"question\" to question the other players\n"
                + "If you have just entered the basement, type \"accuse\" to try to solve the murder\n"
                + "Type \"done\" to end your turn \n" + "Type \"quit\" to leave the game \n"
                + "Type \"#help\" to see testing commands");
    }

    private void MoveInDirection(String dir) {
        if (hasRolled == false) {
            DisplayError("You need to roll the dice before you can move.\n");
            return;
        }
        if (hasEneteredRoom) {
            DisplayError("You cannot move after you have entered a room.\n");
            return;
        }
        if (stepsLeft == 0) {
            DisplayError("You do not have any steps left to move.\n");
            return;
        }
        if (turnPlayerObject.IsInRoom()) {
            DisplayError("You have to move out of the room first.\n");
            return;
        }
        Coordinates positionChange;
        switch (dir) {
        case "u":
            positionChange = Coordinates.UP;
            break;
        case "d":
            positionChange = Coordinates.DOWN;
            break;
        case "l":
            positionChange = Coordinates.LEFT;
            break;
        case "r":
            positionChange = Coordinates.RIGHT;
            break;
        default:
            throw new IllegalArgumentException("Move direction must be a string in the set {u, d, l, r}.\n");
        }

        Coordinates movingToCo = turnPlayerObject.GetCoordinates().Add(positionChange);

        if (Game.GetBoard().IsPositionMovable(turnPlayerObject.GetCoordinates(), movingToCo) == false)
            DisplayError(turnPlayer + " cannot move in direction " + dir);

        else {
            if (Game.GetBoard().IsDoor(movingToCo))
                EnterRoom(Game.GetBoard().GetDoorRoom(movingToCo));
            
            else {
                // Moves a player along the board grid.
                turnPlayerObject.Move(positionChange);
                DisplayMessage(turnPlayer + " moved in direction: " + dir);
                stepsLeft--;
                DisplayMessage(turnPlayer + " has " + stepsLeft + " steps left to move.");
                turnQuestion.ResetCanAsk();
            }
        }
    }

    private void EnterRoom(Room r) {
        turnPlayerObject.MoveToRoom(r);
        hasEneteredRoom = true;
        DisplayMessage(turnPlayer + " entered the " + turnPlayerObject.GetRoom().GetName());

        if (r.GetName().equals(Reasource.BASEMENTNAME))
            turnAssertion.SetCanAsk();
        else
            turnQuestion.SetCanAsk();
    }

    private void MoveOutOfRoom(int exit) {
        PlayerObject playerObject = turnPlayer.GetPlayerObject();

        if (hasEneteredRoom)
            DisplayError("You have already entered a room on this turn");
        else if (hasRolled == false)
            DisplayError("You need to roll the dice before you can move");

        else if (exit < 1 || playerObject.GetRoom().GetDoors().length < exit)
            DisplayError("Please enter a valid door number");

        else {
            Room playerRoom = playerObject.GetRoom();
            playerObject.LeaveRoom();
            Game.GetBoard().ResetRoom();
            turnPlayerObject.MoveTo(playerRoom.GetDoors()[exit - 1].GetOutside());
            stepsLeft--;
            DisplayMessage(turnPlayer + " left the " + playerRoom.GetName() + " through exit number " + exit);
            DisplayMessage(turnPlayer + " has " + stepsLeft + " steps left to move");
        }
    }

    private void MoveThroughPassage() {

        if (turnPlayerObject.IsInRoom() == false) {
            DisplayError("You cannot take a passage while not in a room");
        } else if (turnPlayerObject.GetRoom().HasPassage() == false) {
            DisplayError("The current room has no passages");
        } else if (hasEneteredRoom) {
            DisplayError("You cannot use a passage if you entered a room this turn");
        } else {
            turnPlayerObject.MoveToRoom(turnPlayerObject.GetRoom().GetPassageRoom());
            Game.GetBoard().ResetRoom();
            DisplayMessage(turnPlayer + " took a secret passage to the " + turnPlayerObject.GetRoom().GetName());
            hasEneteredRoom = true;
            canRoll = false;
            turnQuestion.SetCanAsk();
        }
    }

    private void StartAskingQuestion() {
        if (turnPlayerObject.IsInRoom() == false)
            DisplayError("You need to be in a room to ask a question");
        
        else if (turnQuestion.HasBeenAsked())
            DisplayError("You have allready asked a question this turn");
        
        else if (turnQuestion.CanAsk() == false)
            DisplayError("You can only ask a question if you are in a room that you did entered after your last turn");
        
        else if (turnQuestion.IsActive())
            DisplayError("You are allready asking a question.");
        
        else {
            turnQuestion.StartAskingQuestion(turnPlayerObject.GetRoom());
            canRoll = false;
            stepsLeft = 0;
        }
    }


    private void MakeAccuastion() {
        if(turnAssertion.CanAsk() == false)
            DisplayError("You can only make an accusation after you have entered the basement");
        
        else if (turnAssertion.IsActive())
            DisplayError("You are allready making an accusation.");
        
        else {
            turnAssertion.Activate();
            canRoll = false;
            stepsLeft = 0;
        }
    }

    private void RollDice() {
        if (hasRolled) {
            DisplayError("You have already rolled your dice\n");
            return;
        }
        if (canRoll == false) {
            DisplayError("You cannot roll your dice at this time\n");
            return;
        }
        int d1, d2;
        Random rand = new Random();
        
        d1 = rand.nextInt(6) + 1;
        d2 = rand.nextInt(6) + 1;
        
        stepsLeft = d1 + d2;
        Game.GetDisplay().SendMessage("Die 1 gives: " + d1 + "\n" + "Die 2 gives: " + d2 + "\n" + turnPlayer + " now has "
                + stepsLeft + " steps to move" + "\n");

        canRoll = false;
        hasRolled = true;
    }

    public Player GetPlayer() {
        return turnPlayer;
    }

    public boolean CanLeaveRoom() {
        return (hasEneteredRoom == false && turnPlayerObject.IsInRoom());
    }

    private void DisplayMessage(String string) {
        Game.GetDisplay().SendMessage(string);
    }
    
    private void DisplayError(String string) {
        Game.GetDisplay().SendError(string);
    }

    /**
     * Sets the number of remaining steps in this turn to the specified amount.
     * To be used for debugging
     */
    public void SetStepsLeft(int steps) {
        stepsLeft = steps;
    }
}
