package Sigurd.Questions;

import Sigurd.Game;
import Sigurd.Player;

public class Assertion extends AbstractQuestion {

	public Assertion(Player p) {
		super(p);
	}
	
	@Override
	protected void DoneWithInput() {
		if(Game.CompareToEnvelope(character, weapon, room) == true) {
            Game.GetDisplay().SendMessage("Correct Guess, you win the game");
			Game.EndGame();
		}
		else {
			asker.KnockOutOfGame();
			Game.GetDisplay().SendMessage("Incorecct Guess, you are out of the game");
            Game.GetDisplay().log(asker + " made an incorrect accusation\n"
                    + asker + " though it was " + character.getName() 
                    + " in the " + room.getName() + " with the " + weapon.getName());
		}
        Deactivate();
	}

	public void Activate() {
		super.Activate();
	}
}