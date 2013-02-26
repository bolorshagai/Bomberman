
public class PowerUpManager implements Runnable
{
    public PowerUpManager()
    {
    }

    public void startPowerUps()
    {
        (new Thread(this,"powerup manager")).start();
    }

    public void run()
    {
        while(Bomberman.game)
        {
            try { Thread.sleep (10000);}//delay
            catch (InterruptedException e){ }

            new PowerUp();
        }
    }
}
