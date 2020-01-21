package solution;

import battleship.BattleShip;
import java.awt.Point;
import java.util.Random;
/**
 * Jordan Jancic
 * December 5, 2019
 */
public class Bot
{
    private int gameSize;                                                               //Holds the value for the board size.
    private BattleShip battleShip;                                                      //Holds an instance of Battleship game object.
    private Random random;                                                              //Generates a random.
    private final int row = 10;                                                         //Holds the value for number of rows.
    private final int col = 10;                                                         //Holds the value for number of columns.
    private final battleship.CellState board[][] = new battleship.CellState[row][col];  //A 2D array that tracks the shot history and state of the board.

   /**
   * Constructor keeps a copy of the BattleShip instance
   * @param b previously created battleship instance - should be a new game
   */
    public Bot(BattleShip b)
    {
        battleShip = b;                                                         //Stores the game instance in variable b.
        gameSize = b.BOARDSIZE;                                                 //Stores the value of the game size.
        random = new Random();                                                  //Used to generate random integers. 
    
        /*
        Creates a brand new board representation by populating a 2D array
        with 100 Empty CellStates. These CellStates change of course as 
        the game is played.
        */
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = battleship.CellState.Empty;
            }
        }
        
    }
    
    int x;                                                                      //Declares row coordinate variable.
    int y;                                                                      //Declares column coordinate variable.
    
    /*
    This fireshot method is where the main logic is located. I converted it to
    a void method as I allowed multiple shots to be taken following a single
    successful hit and did not find it necessary to return a boolean anymore.
    
    You will notice that the fireshot method still uses a randomly generated
    coordinates. This was done intentionally as I was actually getting slightly
    better results with this method as opposed to using a parity based strategy. 
    */
    public void fireShot()
    {
        boolean hit = false;                                                    //Stores the boolean value of whether or not a hit was successful.

        x = random.nextInt(gameSize);                                           //Generates a random value for row coodinate.
        y = random.nextInt(gameSize);                                           //Generates a random value for column coodinate.
//    
//        /*
//        This while loop will continue to generate random numbers until they are
//        unique when compared to the CellStates on our 2D Board array.
//        */
        while(board[x][y] == battleship.CellState.Hit || board[x][y] == battleship.CellState.Miss) {        
            x = random.nextInt(gameSize);
            y = random.nextInt(gameSize);
        }

        hit = battleShip.shoot(new Point(x,y));                                 //Attempts a shot on the opposing battleships.
     
        if (hit) {                                                              //If the shot is successful, then we continue firing around it.
            board[x][y] = battleship.CellState.Hit;                             //Marks the coordinate on the 2D array board as hit.

            int initialRow = x;                                                 //Stores the row coordinate for future comparisons.
            int initialCol = y;                                                 //Stores the column coordinate for future comparisons.
            
            boolean checkUp = false;                                            //Initializes a boolean that tracks whether or not we need to search upwards after a hit.
            boolean checkDown = false;                                          //Initializes a boolean that tracks whether or not we need to search downwards after a hit.
            boolean checkLeft = false;                                          //Initializes a boolean that tracks whether or not we need to search to the left after a hit.
            boolean checkRight = false;                                         //Initializes a boolean that tracks whether or not we need to search to the right after a hit.
            
            //Checks if the current row is the first row.
            if(initialRow == 0) {
                checkUp = false;                                                //If true, then we do not bother checking upwards as it would result in an index out of bounds exception.

                if(board[x + 1][y] == battleship.CellState.Empty) {             //If, according to our 2D board, the cell below is empty, then we will be sure to check it (shoot at it).
                    checkDown = true;                                           //Marks true to check the cell below.
                }
                else {
                    checkDown = false;                                          //If the Cellstate is not empty, then we do not bother firing at it.
                }
            }
            //Checks if the current row is the final row.
            else if(initialRow == 9) {
                checkDown = false;                                              //If true, then we do not bother checking downwards as it would result in an index out of bounds exception.
                
                if(board[x - 1][y] == battleship.CellState.Empty) {             //Checks if the above cell is marked empty on the 2D Array.
                    checkUp = true;                                             //If empty, then we will check the above cell.
                }
                else {                                                          
                    checkUp = false;                                            //If the Cellstate is not empty, then we do not bother firing at it.
                }
            }
            //If the current row is neither the first or last,
            //then we may check both above and below.
            else {
                if(board[x - 1][y] == battleship.CellState.Empty) {             //Checks if the cell above is marked as empty on the 2D array.
                    checkUp = true;                                             //If it is, then this variable is set to true.
                }
                else {
                    checkUp = false;                                            //If the Cellstate is not empty, then we do not bother firing at it.
                }
                if(board[x + 1][y] == battleship.CellState.Empty) {             //Checks if the cell below is marked as empty on the 2D array.
                    checkDown = true;                                           //If it is, then this variable is set to true.
                }
                else {
                    checkDown = false;                                          //If the Cellstate is not empty, then we do not bother firing at it.
                }
            }
            
            int sunkenShips = battleShip.numberOfShipsSunk();                   //Tracks the current value of number of sunken ships.
            int goUp = x;                                                       //Copies the value of the current row into a new variable for future targetting of the next shot.
            int upLoop = initialRow;                                            //Tracks the maximum number of possible loops we can have with the current row before an index out of bounds exception occurs.
            
            /*
            This while loop will run as long as there are more valid
            cells to check upwards and the maximum number of possible
            loops does not exceed the max possible amount of remaining
            rows. 
            */
            while(checkUp && upLoop > 0) {
                
                goUp--;                                                         //Subtracts this value by one to prepare to check the above row.
                Point nextShot = new Point(goUp,y);                             //Prepares the next Point shot using the new row coordinate.
                boolean upHit = battleShip.shoot(nextShot);                     //Fires the new shot based on the new coordinate.
                
                if(upHit) {                                                     //Checks if the shot was a hit.
                    board[goUp][y] = battleship.CellState.Hit;                  //If a hit, we mark it on the 2D board array.
                    if(battleShip.numberOfShipsSunk() > sunkenShips) {          //After the hit, we check if a ship was sunk. If true, then we stop firing.
                        checkUp = false;                                        //Set to false to exit the while loop.
                    }
                }
                else {                                                          
                    board[goUp][y] = battleship.CellState.Miss;                 //If the hit was not successful, then we mark it as a miss on the 2D array board.
                    checkUp = false;                                            //Set to false to exit the while loop.
                }
                upLoop--;                                                       //Decrements to mark that another loop has passed.
            }
            
            sunkenShips = battleShip.numberOfShipsSunk();                       //Tracks the current value of number of sunken ships.
            int goDown = x;                                                     //Copies the value of the current row into a new variable for future targetting of the next shot.
            int downLoop = 9 - initialRow;                                      //Tracks the maximum number of possible loops we can have with the current row before an index out of bounds exception occurs.
            
            /*
            This while loop will run as long as there are more valid
            cells to check downwards and the maximum number of possible
            loops does not exceed the max possible amount of remaining
            rows. 
            */
            while(checkDown && downLoop > 0) {
                
                goDown++;                                                       //Adds to this value by one to prepare to check the below row.
                Point nextShot = new Point(goDown,y);                           //Prepares the next Point shot using the new row coordinate.
                boolean downHit = battleShip.shoot(nextShot);                   //Fires the new shot based on the new coordinate.
                
                if(downHit) {                                                   //Checks if the shot was a hit.
                    board[goDown][y] = battleship.CellState.Hit;                //If a hit, we mark it on the 2D board array.
                    if(battleShip.numberOfShipsSunk() > sunkenShips) {          //After the hit, we check if a ship was sunk. If true, then we stop firing.
                        checkDown = false;                                      //Set to false to exit the while loop.
                    }
                }
                else {
                    board[goDown][y] = battleship.CellState.Miss;               //If the hit was not successful, then we mark it as a miss on the 2D array board.
                    checkDown = false;                                          //Set to false to exit the while loop.
                }
                downLoop--;                                                     //Decrements to mark that another loop has passed.
            }
            
            //Checks if the current column is the first column.
            if(initialCol == 0) {
                checkLeft = false;                                              //If true, then we do not bother checking left as it would result in an index out of bounds exception.

                if(board[x][y + 1] == battleship.CellState.Empty) {             //If, according to our 2D board, the left cell is empty, then we will be sure to check it (shoot at it).
                    checkRight = true;                                          //Marks true to check the left cell.
                }
                else {
                    checkRight = false;                                         //If the Cellstate is not empty, then we do not bother firing at it.
                }
            }
            else if(initialCol == 9) {
                checkRight = false;                                             //If false, then we do not bother checking right as it would result in an index out of bounds exception.

                if(board[x][y - 1] == battleship.CellState.Empty) {             //Checks if the left cell is marked empty on the 2D Array.
                    checkLeft = true;                                           //If empty, then we will check the left cell.
                }
                else {
                    checkLeft = false;                                          //If the Cellstate is not empty, then we do not bother firing at it.
                }
            }
            else  {
                if(board[x][y - 1] == battleship.CellState.Empty) {             //Checks if the cell to the left is marked as empty on the 2D array.
                    checkLeft = true;                                           //If it is, then this variable is set to true.
                }
                else {
                    checkLeft = false;                                          //If the Cellstate is not empty, then we do not bother firing at it.
                }
                if(board[x][y + 1] == battleship.CellState.Empty) {             //Checks if the cell to the right is marked as empty on the 2D array.
                    checkRight = true;                                          //If it is, then this variable is set to true.
                }
                else {
                    checkRight = false;                                         //If the Cellstate is not empty, then we do not bother firing at it.
                }
            }
             
            sunkenShips = battleShip.numberOfShipsSunk();                       //Tracks the current value of number of sunken ships.
            int goLeft = y;                                                     //Copies the value of the current column into a new variable for future targetting of the next shot.
            int leftLoop = initialCol;                                          //Tracks the maximum number of possible loops we can have with the current row before an index out of bounds exception occurs.
            
            /*
            This while loop will run as long as there are more valid
            cells to check leftwards and the maximum number of possible
            loops does not exceed the max possible amount of remaining
            rows. 
            */
            while(checkLeft && leftLoop > 0) {
                
                goLeft--;                                                       //Subtracts this value by one to prepare to check the left column.
                Point nextShot = new Point(x,goLeft);                           //Prepares the next Point shot using the new row coordinate.
                boolean leftHit = battleShip.shoot(nextShot);                   //Fires the new shot based on the new coordinate.
                
                if(leftHit) {                                                   //Checks if the shot was a hit.
                    board[x][goLeft] = battleship.CellState.Hit;                //If a hit, we mark it on the 2D board array.
                    if(battleShip.numberOfShipsSunk() > sunkenShips) {          //After the hit, we check if a ship was sunk. If true, then we stop firing.
                        checkLeft = false;                                      //Set to false to exit the while loop.
                    }
                }
                else {
                    board[x][goLeft] = battleship.CellState.Miss;               //If the hit was not successful, then we mark it as a miss on the 2D array board.
                    checkLeft = false;                                          //Set to false to exit the while loop.
                }
                leftLoop--;                                                     //Decrements to limit loops.
            }
            
            sunkenShips = battleShip.numberOfShipsSunk();                       //Tracks the current value of number of sunken ships.
            int goRight = y;                                                    //Copies the value of the current column into a new variable for future targetting of the next shot.
            int rightLoop = 9 - initialCol;                                     //Tracks the maximum number of possible loops we can have with the current row before an index out of bounds exception occurs.
            
            /*
            This while loop will run as long as there are more valid
            cells to check rightwards and the maximum number of possible
            loops does not exceed the max possible amount of remaining
            rows. 
            */
            while(checkRight && rightLoop > 0) {
                
                goRight++;                                                      //Subtracts this value by one to prepare to check the right column.
                Point nextShot = new Point(x,goRight);                          //Prepares the next Point shot using the new row coordinate.
                boolean rightHit = battleShip.shoot(nextShot);                  //Fires the new shot based on the new coordinate.
                
                if(rightHit) {                                                  //Checks if the shot was a hit.
                    board[x][goRight] = battleship.CellState.Hit;               //If a hit, we mark it on the 2D board array.
                    if(battleShip.numberOfShipsSunk() > sunkenShips) {          //After the hit, we check if a ship was sunk. If true, then we stop firing.
                        checkRight = false;                                     //Set to false to exit the while loop.
                    }
                }
                else {
                    board[x][goRight] = battleship.CellState.Miss;              //If the hit was not successful, then we mark it as a miss on the 2D array board.
                    checkRight = false;                                         //Set to false to exit the while loop.
                }
                rightLoop--;                                                    //Decrements to limit loops.
            }

        }
        else {
            board[x][y] = battleship.CellState.Miss;                            //Marks board coordinate as Miss if hit was not successful.
        }

    }
  
    /*
    Returns a visual representation of the 2D Array board to the
    console. Not necessary for application function, however, it
    was used for debugging and analysis.
    */
    public void getBoard() {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                System.out.print(board[row][col]);
            }
        System.out.println("");
        }
    } 
}