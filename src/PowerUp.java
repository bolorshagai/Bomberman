
public class PowerUp implements Runnable
{
    int num = 0;
    int randX = 0;
    int randY = 0;
    int x = 0;
    int y = 0;
    int numOfPowerUps = 4;
    Thread thread;
    public PowerUp()
    {
        //creates powerup
         num = (int)(Math.random() * numOfPowerUps) + 7;
        randX = (int)(Math.random() * 11);
        randY = (int)(Math.random() * 11);
        for(int i = randX; i < 11; i++)
        {
            for(int j = randY; j < 11; j++)
            {
                if(Bomberman.grid[i][j] == 0)
                {
                    Bomberman.grid[i][j] = num;
                    System.out.println(num);
                    x = i;
                    y = j;
                    i = 11;
                    j = 11;
                }
            }
        }
        thread = new Thread(this,"thread");
        thread.start();
    }

    public PowerUp(int xLocation, int yLocation)
    {
        //makes the flame and extra range powerup whena brick is destroyed
        num = (int) (Math.random() * numOfPowerUps) + 7;

        if(num != 7 && num!= 10)
        {
        Bomberman.grid[xLocation][yLocation] = num;
        }
    }

    public void run()
    {
        try { Thread.sleep (5000);}//delay
         catch (InterruptedException e){ }

        destroyPowerUp();
    }

    public void destroyPowerUp()
    {
        Bomberman.grid[x][y] = 0;
    }
}
