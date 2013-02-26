
import java.awt.event.KeyEvent;


public class Player implements Runnable
{
    private int health = 0;
    private int xPos = 0;
    private int yPos = 0;
    private int maxBombs = 0;
    private int usedBombs = 0;
    private int bombRange = 0;
    private int playerNum = 0;
    private boolean postPhase = false;
    private int direction = 0; // 0 for down, 1 for left, 2 for up, 3 for right
    private Thread timer;
    private int state = 0; //sates: 0 for normal, 1 for invincible, 2 for phase, 3 for dead, 4 for walking backwards
    private boolean alive = true;
    private String name = "";

    public Player(int x, int y, int healthInput, int maxBombsInput, int range, int p, String n)
    {
        xPos = x;
        yPos = y;
        health = healthInput;
        maxBombs = maxBombsInput;
        bombRange = range;
        playerNum = p;
        name = n;
    }
    
    public void setXPos(int x)
    {
        xPos = x;
    }
    
    public void setYPos (int y)
    {
        yPos = y;
    }

    public void setDirection(int d)
    {
        direction = d;
    }

    public void setState(int s)
    {
        state = s;

        switch(state)
        {
            case 2: timer = new Thread(this,"phase"); timer.start();
            case 4: timer = new Thread(this,"dropping bombs"); timer.start();
        }
    }

    public void increaseBombRange(int r)
    {
        bombRange += r;
    }

    public void increaseBombLimit(int x)
    {
        maxBombs += x;
    }
    
    public void updateHealth (int h)
    {
        health += h;
    }

    public void blasted()
    {
        health--;
        state = 1;
        if (health > 0)
        {
            timer = new Thread(this,"timer");
            timer.start();
        }
        else
        {
            Bomberman.deadPlayers++;
            Bomberman.changeSprites(4, playerNum);
        }
    }

    public void bombUsed()
    {
            usedBombs++;
            System.out.println(usedBombs);
    }

    public void phaseOver()
    {
        postPhase = false;
    }

    public void bombExploded()
    {
        if(usedBombs>0)
        {
            usedBombs--;
        }
    }

    public int getXPos()
    {
        return xPos;
    }

    public int getGridXPos()
    {
        return (xPos - 50) / 50;
    }

    public int getGridYPos()
    {
        return (yPos - 50) / 50;
    }

    public int getYPos()
    {
        return yPos;
    }

    public int getHealth()
    {
        return health;
    }

    public int getDirection()
    {
        return direction;
    }

    public int getUsedBombs()
    {
        return usedBombs;
    }

    public int getMaxBombs()
    {
        return maxBombs;
    }

    public int getBombRange()
    {
        return bombRange;
    }

    public int getPlayerNum()
    {
        return playerNum;
    }

    public int getState()
    {
        return state;
    }

    public String getName()
    {
        return name;
    }

    public boolean checkPostPhase()
    {
        return postPhase;
    }

    public void run()
    {
        try
        {
            switch(state)
            {
                case 1: Bomberman.changeSprites(2,playerNum); timer.sleep(3000);state = 0;Bomberman.changeSprites(1,playerNum);break;
                case 2: Bomberman.changeSprites(3,playerNum);timer.sleep(8000);postPhase = true;break;
                case 4: timer.sleep(5000);state = 0;resetBombKey(playerNum);break;
            }
        }
        catch(Exception e)
        {

        }
        if(state != 2)state = 0;
    }

    public void resetBombKey(int p)
    {
        switch(p)
        {
            case 3: Bomberman.keyPressed[KeyEvent.VK_SPACE] = false;break;
            case 2: Bomberman.keyPressed[KeyEvent.VK_SHIFT] = false;break;
            case 1: Bomberman.keyPressed[KeyEvent.VK_CONTROL] = false; break;
            case 0: Bomberman.keyPressed[KeyEvent.VK_NUMPAD0] = false; break;
        }
    }
}
