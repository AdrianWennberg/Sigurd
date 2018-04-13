package bots;

import java.util.Random;

import gameengine.*;

public class TestBot1 implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the board or the player objects
    // It may only inspect the state of the board and the player objects
    
    static String[] options = {"u", "d", "l", "r"};

    private Player player;
    private Map map;
    private Dice dice;
    private Log log;
    private Deck deck;
    
    Random r = new Random();
    boolean turnOver; 
    boolean hasAsked;

    public TestBot1 (Player player, PlayersInfo playersInfo, Map map, Dice dice, Log log, Deck deck) {
        this.player = player;
        this.map = map;
        this.dice = dice;
        this.log = log;
        this.deck = deck;
        turnOver = true;
        hasAsked = false;
    }

    public String getName() {
        return "Bot1"; // must match the class name
    }

    public String getCommand() {
        if(turnOver)
        {
            turnOver = false;
            hasAsked = false;
            return "roll";
        }
        if(player.getToken().isInRoom() && hasAsked == false) {
            hasAsked = true;
            return "question";
        }
        turnOver = true;
        return "done";
    }

    public String getMove() {
        String letter;
        
        while(map.isValidMove(player.getToken().getPosition(), letter = options[r.nextInt(4)] ) == false);
        
        return letter;
    }

    public String getSuspect() {
        // Add your code here
        return Names.SUSPECT_NAMES[0];
    }

    public String getWeapon() {
        // Add your code here
        return Names.WEAPON_NAMES[0];
    }

    public String getRoom() {
        // Add your code here
        return Names.ROOM_NAMES[0];
    }

    public String getDoor() {
        // Add your code here
        return "1";
    }

    public String getCard(Cards matchingCards) {
        // Add your code here
        return matchingCards.get().toString();
    }

    public void notifyResponse(Log response) {
        // Add your code here
    }

}
