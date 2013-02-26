import java.util.*;
import java.lang.*;


public class Bomb implements Runnable
{
    Thread bombThread;
    int gridXPos;
    int gridYPos;
    int bombRange = 0;
    int delay = 0;
    int player = 0;

    public Bomb(int p, int range)
    {
        bombRange = range;
        player = p;
        delay = 3000;
        gridXPos = Bomberman.player[player].getGridXPos();
        gridYPos = Bomberman.player[player].getGridYPos();
        System.out.println(gridXPos + "," + gridYPos);
        Bomberman.grid[gridXPos][gridYPos] = 5;
        bombThread = new Thread(this, "bombThread");
        bombThread.start();
    }

    public Bomb(int p,int range, int time, int x, int y)
    {
        bombRange = range;
        gridXPos = x;
        gridYPos = y;
        player = p;
        Bomberman.grid[gridXPos][gridYPos] = 5;
        bombThread = new Thread(this, "bombThread");
        bombThread.start();
    }

    public void run()
    {
        try
        {
            bombThread.sleep(delay);
            Bomberman.grid[gridXPos][gridYPos] = 6;

            //Displays Blasts
            for(int i = 0; i < bombRange; i++)
            {
                if(gridYPos + i <= 10)
                {
                    switch(Bomberman.grid[gridXPos][gridYPos + i])
                    {
                        case 0: Bomberman.grid[gridXPos][gridYPos + i] = 6; break;
                        case 1: i = bombRange; break;
                        case 2: Bomberman.grid[gridXPos][gridYPos + i] = 6; new PowerUp(gridXPos,gridYPos + i); i = bombRange;break;
                        case 5: detonateBomb(gridXPos, gridYPos + i); break;
                    }
                }
            }

            for(int i = 0; i < bombRange; i++)
            {
                if(gridYPos - i >= 0)
                {
                    switch(Bomberman.grid[gridXPos][gridYPos - i])
                    {
                        case 0: Bomberman.grid[gridXPos][gridYPos - i] = 6; break;
                        case 1: i = bombRange; break;
                        case 2: Bomberman.grid[gridXPos][gridYPos - i] = 6; new PowerUp(gridXPos,gridYPos - i); i = bombRange;break;
                        case 5: detonateBomb(gridXPos, gridYPos - i); break;
                    }
                }
            }

            for(int i = 0; i < bombRange; i++)
            {
                if(gridXPos + i <= 10)
                {
                    switch(Bomberman.grid[gridXPos + i][gridYPos])
                    {
                        case 0: Bomberman.grid[gridXPos + i][gridYPos] = 6; break;
                        case 1: i = bombRange; break;
                        case 2: Bomberman.grid[gridXPos + i][gridYPos] = 6; new PowerUp(gridXPos + i,gridYPos); i = bombRange; break;
                        case 5: detonateBomb(gridXPos + i, gridYPos); break;
                    }
                }
            }

            for(int i = 0; i < bombRange; i++)
            {
                if(gridXPos - i >= 0)
                {
                    switch(Bomberman.grid[gridXPos - i][gridYPos])
                    {
                        case 0: Bomberman.grid[gridXPos - i][gridYPos] = 6; break;
                        case 1: i = bombRange; break;
                        case 2: Bomberman.grid[gridXPos - i][gridYPos] = 6;new PowerUp(gridXPos - i,gridYPos); i = bombRange; break;
                        case 5: detonateBomb(gridXPos - i,gridYPos); break;
                    }
                }
            }

            bombThread.sleep(700);
            Bomberman.grid[gridXPos][gridYPos] = 0;
            
            //Removes Blasts
            for(int i = 0; i < bombRange; i++)
            {
                if((gridYPos + i) <= 10 && Bomberman.grid[gridXPos][gridYPos + i] == 6)
                {
                    Bomberman.grid[gridXPos][gridYPos + i] = 0;
                }
            }

            for(int i = 0; i < bombRange; i++)
            {
                if((gridYPos - i) >= 0 && Bomberman.grid[gridXPos][gridYPos - i] == 6)
                {
                    Bomberman.grid[gridXPos][gridYPos - i] = 0;
                }
            }

            for(int i = 0; i < bombRange; i++)
            {
                if((gridXPos + i) <= 10 && Bomberman.grid[gridXPos + i][gridYPos] == 6)
                {
                    Bomberman.grid[gridXPos + i][gridYPos] = 0;
                }
            }

            for(int i = 0; i < bombRange; i++)
            {
                if((gridXPos - i) >= 0 && Bomberman.grid[gridXPos - i][gridYPos] == 6)
                {
                    Bomberman.grid[gridXPos - i][gridYPos] = 0;
                }
            }
            Bomberman.player[player].bombExploded();
        }
        catch (Exception e) {

        }
    }

    public int getGridXPos()
    {
        return gridXPos;
    }

    public int getGridYPos()
    {
        return gridYPos;
    }

    public int getBombRange()
    {
        return bombRange;
    }

    public int getPlayer()
    {
        return player;
    }

    public void detonateBomb(int x, int y)
    {
        try
        {
        for(int i = 0; i < Bomberman.bombs.size(); i++)
        {
            if(((Bomb)(Bomberman.bombs.get(i))).getGridXPos() == x && ((Bomb)(Bomberman.bombs.get(i))).getGridYPos() == y)
            {
                //stops the bomb thread
                ((Bomb)(Bomberman.bombs.get(i))).bombThread.interrupt();

                //detonates a new bomb immediately in the same spot
                new Bomb(((Bomb)(Bomberman.bombs.get(i))).getPlayer(),((Bomb)(Bomberman.bombs.get(i))).getBombRange(), 0, x, y);
            }
        }
        }
        catch (Exception e)
        {
        }

        }
}
