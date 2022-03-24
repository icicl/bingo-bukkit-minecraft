# What is this Plugin?
This plugin is a minigame for minecraft where all participants try to find as many items as they can from a 5 by 5 grid, in order to score the most points. Unlike other Bingo games, a bingo is not required to win, although it does help.

## Features
![image](https://raw.githubusercontent.com/icicl/bingo-bukkit-minecraft/main/images/gameplay_01.png)
- Pictured
  - Scorecard:
    - Shows which items you have found, and how many points you have received / will receive for each item.
  - Game status in tab display:
    - Health and score of other players.
    - Time remaining in game.
- Not pictured
  - Chat message and sound effect when a player finds an item.
  - Chat message and sound effect when a player finds a bingo.
  - Upon end of game, sound effect and chat message to announce winner(s), as well as updating the tab display to show winner(s).  
  - `/bingo top` command: warps player to surface.
  - Good customization; see **Commands** or **Options** for more details.  
## Gameplay
Each player receives a card with the same 25 target items at the beginning of the game, each worth an initial number of points. As you and other players explore the world and find these items, your score increases, and the point value of the item decreases for other players. Additionally, bingos - or finding all five items in a row, column, or diagonal - award bonus points. Each player will only receive points for the first time they collect a given item. The cards each player has will update to display either how many points they received for an item - or if they have not found it, how many points it is worth. The current score of all players can be seen in the player list (the display that appears when holding tab), alongside the time remaining in the game. This plugin also features sound effects on important game events, in case you aren't always reading the chat.

## How to Use / Commands
Simply drop the `bingo.jar` file into your `plugins` folder, and start the bukkit server. If you change any options in `config.yml`, you will have to either `/reload` plugins, or restart the server.
<br>
Once the server is running, create a new game by running `/bingo new`, have everyone who wishes to play join with `/bingo join`, and then begin the game with `/bingo start`. Note that only one game may take place at a time, so if someone uses `/bingo new` while a game is ongoing, it will end the current game.
<br>
### Commands:
 - `/bingo new [durationInSeconds] [advancedItems]`
   - Creates a new bingo game.
   - `durationInSeconds` is an integer, which will set the length of the game. If omitted, this defaults to 600, or 10 minutes.
   - `advancedItems` is a boolean that specifies whether to include "advanced" objectives. If omitted, this defaults to false for games under 25 minutes, and true otherwise. See the **Configuration** section for what counts as an advanced item.
 - `/bingo join`
   - Enrolls the player who executed it into the current bingo game. This will still work if the game has already been started, but the player receives no handicap.
 - `/bingo start [clearInventory] [teleportPlayers] [isolatePlayers]`
   - Starts the currently queueing bingo game, giving all players a bingo card and basic iron tools, and filling their health, hunger, and saturation.
   - `clearInventory` is a boolean that determines whether player's inventories will be cleared at the start of a game. I recommend setting this to true, however to avoid unfortunate accidents it defaults to false.
   - `teleportPlayers` is a boolean that determines whether players will be teleported to a far away location when the game begins. It defaults to true.
   - `isolatePlayers` is a boolean that determines whether the players are teleported nearby each other, or far away from each other. This does not apply if `teleportPlayers` is false. It defaults to false.
 - `/bingo goals [row] [col]`
   - Tells a player the specified goal(s) in text form. This is useful if it is difficult to distinguish an item by the texture on the card.
   - `row` is required. It is the row of  the item in question.
   - `col` is optional. It is the column of the item in question.
   - `row` and `col` should be a number from 1 to 5, although up to one may be a wildcard (`*`), and `col` defaults to wildcard.
 - `/bingo card`
   - Gives the player a new bingo card, in case they somehow lost theirs.
 - `/bingo top`
   - teleports the player to the surface.
 - `/bingo help`
   - shows a slightly less detailed copy of this section.


## Configuration
Many or the parameters of the gameplay and scoring mechanics are configurable.
The five gameplay parameters listed above are given every game, but the following scoring options persist until the are changed again. To change them, open the `config.yml` file in the `Bingo` subdirectory of the `plugins` folder.
### Options:
- `initial-reward-base`
  - Integer
  - How many points each goal will be worth initially.
  - Defaults to 10.
- `initial-reward-per-player`
  -  Integer
  -  How many points the initial point value increases by per player.
  -  Defaults to 5.
- `initial-decrement`
  - Integer
  - How many points the value for an item decreases by if it is the first time it is found.
  - Defaults to 10.
- `subsequent-decrement`
  - Integer
  - How many points the value for an item decreases by every time it is found other than the first.
  - Defaults to 5.
- `bingo-set-score-to-zero`
  - Boolean
  - If this is true, upon a player finding a bingo, all items in that bingo will become worth zero points for future finds, although they will still count towards bingos.
  - Defaults to false.
- `bingo-bonus-static`
  - Integer
  - A static bonus that will be rewards upon finding a bingo.
  - Defaults to 0.
- `bingo-bonus-multiplier`
  - Decimal
  - A multiplier that upon finding a bingo will increase your score by the sum of how many points you received for each item in that bingo, times the parameter.
  - Defaults to 1.0.
  - Note that this is *bonus* points, so a value of 0 means no bonus, and a value of 2 means the points for a bingo's items are tripled.
  - Also note that if you find intersecting bingos, the intersection will not grant its bonus twice.
- `blocks`
  - This specifies how likely blocks are to appear in "normal" or "advanced" mode. The first number is their weight in normal mode, and the second number is twice the weight that is added for advanced mode. For subcategories, such as each color, the weights of the subitems are their weights within that category, and the line `weight: [int1, int2]` represents the weight of that category. A category can only be chosen once.

As an example, with the default values and 4 players, the initial score for items is 30, and the score goes [30->20->15->10] 

## Suggestions / Bugs
If you have any suggestions or find a bug, please create an issue.
<br>
I tried my best to fumigate this project, but it is my first plugin, so I suspect plenty of bugs survived.