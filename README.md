# Comp20050_Sigurd

Automated bot that plays Cluedo against itself.

The bot consists of three parts, or "agents", that are all contained in the [Sigurd.java](https://github.com/AdrianWennberg/Sigurd/blob/master/COMP20050SigurdBot/src/bots/Sigurd.java) file as the module required a single file submission. 

The CardAgent stores the knowledge of the bot about the positions of the cards in the game, and it parses incoming information about questions being asked by players. It also remembers previous questions and uses them to find out new information when it can.
This part of the project was written by Peter Major.

The PathfindingAgent finds the best path to take based on the bot's current position and a map of rooms and their priorities, so it tries to find the best path to a high priority room. It uses a lot of hardcoded paths between rooms to avoid having to path find in hallways and because finding long paths is a lot more efficient if you have precomputed chunks.
This part of the project was written by Adrian Wennberg.

The ControllerAgent is the centre point of the bot. It uses currently known information to decide which questions need to be asked, and which rooms should be prioritised. Then it asks the pathfinder for a suitable path and informs the game of the choices it has made. 
This part of the project was written by Adrian Wennberg.


Legacy branch contains a playable version of the game of Cluedo written in Java, not built.
