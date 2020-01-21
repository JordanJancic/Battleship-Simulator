package solution;

import battleship.BattleShip;

/**
 * Jordan Jancic
 * December 5, 2019
 */
public class BattleshipSimulator
{
   static final int NUMBEROFGAMES = 10000;
   public static void startingSolution()
  {
    int totalShots = 0;
    System.out.println(BattleShip.version());
    for (int game = 0; game < NUMBEROFGAMES; game++) {

      BattleShip battleShip = new BattleShip();
      Bot sampleBot = new Bot(battleShip);
      
      // Call Bot Fire randomly - You need to make this better!
      while (!battleShip.allSunk()) {
        sampleBot.fireShot();
      }
      int gameShots = battleShip.totalShotsTaken();
      totalShots += gameShots;
    }
    System.out.printf("SampleBot - The Average # of Shots required in %d games to sink all Ships = %.2f\n", NUMBEROFGAMES, (double)totalShots / NUMBEROFGAMES);
    
   
  }
  public static void main(String[] args)
  {
    startingSolution();
  }
}
