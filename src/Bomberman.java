//By Sujen Sathiyanathan
//Imports Classes
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

//Bomberman Class
public class Bomberman extends Canvas implements KeyListener
{
  //Variables and objects
  static int grid[][] = new int[11][11];
  static int numOfPlayers = 2;
  static boolean game = false;
  PowerUpManager pum = new PowerUpManager();
  static int maxBombs = 1;
  static int bombRange = 2;
  static int health = 1;
  static int deadPlayers = 0;
  int winPlayer = 5;
  static int mode = 0; //Mode represents the mode the user is in the game: 0 for titlescreen, 1 for options screen, 2 for rules screen, 3 for controls screen, 4 for credits, 5 for game
  public static final int WIDTH = 850;
  public static final int HEIGHT = 678;
  public static final int SPEED = 10;
  public static final int VELOCITY = 50;
  public BufferStrategy bs;
  static ArrayList bombs = new ArrayList();
  BufferedImage gridBackground;
  BufferedImage scoreBoard;
  BufferedImage heart;
  static BufferedImage[] bombermanSprite = new BufferedImage[16];
  static BufferedImage[] normalBombermanSprite = new BufferedImage[16];
  static BufferedImage[] transparentBombermanSprite = new BufferedImage[16];
  static BufferedImage[] phaseBombermanSprite = new BufferedImage[16];
  static BufferedImage[] miscBombermanSprite = new BufferedImage[8];
  static BufferedImage[] tileImage = new BufferedImage[20];
  static JFrame window;
  JPanel gamePanel;
  JPanel titlePanel;
  JPanel rulesPanel;
  JPanel controlsPanel;
  JPanel creditsPanel;
  Options optionsPanel;
  static Player[] player = new Player[4];
  static String map = "Classic";
  static String[] names = {"Player 1", "Player 2", "Player 3", "Player 4"};
  
  //Arrays for controls
  static boolean[] keyPressed = new boolean[256]; //This array stores whether or not a key is pressed.

  //The main method which starts the program. Goes in a loop while the window is still open and while in the loop it displays certain JPanels depending on the variable mode
  public static void main(String[] args)
  {
    //Creates a Bomberman object
    Bomberman bomberman =  new Bomberman();

    //Loads all the images and sprites
    bomberman.load();

    //Loops while the window is open
    while(window.isVisible())
    {
        //Depending on mode, a certain JPanel will be made visible by calling a method
        switch(mode)
        {
            case 1: bomberman.options();break;
            case 2: bomberman.rules();break;
            case 3: bomberman.controls();break;
            case 4: bomberman.credits();break;
            case 5: if(game)bomberman.newGame();break;
        }
    }
  }  

  //The constructor which sets up all the JFrame, Canvas, JPanels, and sets the buffer strategy.
  public Bomberman ()
  {
    //makes the Canvas visible
    setVisible(true);
    
    //Creates window and sets it up
    window = new JFrame("BOMBERMAN");
    window.setBounds(0, 0, WIDTH, HEIGHT);
    window.setResizable(false); 
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.addKeyListener(this);
    setBounds(0, 0, WIDTH, HEIGHT);
    window.getContentPane().setLayout(new CardLayout());

    //Creates the menu bar
    window.setJMenuBar(new Menu());

    //Creates an option panel that has all the options
    optionsPanel = new Options();
    window.getContentPane().add(optionsPanel,"OptionsPanel");

    //Creates a title panel which is the title screen
    titlePanel = new JPanel();
    titlePanel.setPreferredSize(new Dimension (WIDTH, HEIGHT));
    titlePanel.setLayout(new GridLayout(1,1));
    titlePanel.add(new JLabel(new ImageIcon(getClass().getClassLoader().getResource("titleScreen.png"))));
    window.getContentPane().add(titlePanel,"TitlePanel");

    //Creates and sets up the game panel where the game is played on
    gamePanel = new JPanel();
    gamePanel.setPreferredSize(new Dimension (WIDTH, HEIGHT));
    gamePanel.setLayout(null);
    gamePanel.add(this);
    window.getContentPane().add(gamePanel,"GamePanel");

    //Creates a rules screen panel which is the rules screen
    rulesPanel = new JPanel();
    rulesPanel.setPreferredSize(new Dimension (WIDTH, HEIGHT));
    rulesPanel.setLayout(new GridLayout(1,1));
    rulesPanel.add(new JLabel(new ImageIcon(getClass().getClassLoader().getResource("rules.png"))));
    window.getContentPane().add(rulesPanel,"RulesPanel");

    //Creates a controls screen panel which is the controls screen
    controlsPanel = new JPanel();
    controlsPanel.setPreferredSize(new Dimension (WIDTH, HEIGHT));
    controlsPanel.setLayout(new GridLayout(1,1));
    controlsPanel.add(new JLabel(new ImageIcon(getClass().getClassLoader().getResource("controls.png"))));
    window.getContentPane().add(controlsPanel,"ControlsPanel");

    //Creates a controls screen panel which is the controls screen
    creditsPanel = new JPanel();
    creditsPanel.setPreferredSize(new Dimension (WIDTH, HEIGHT));
    creditsPanel.setLayout(new GridLayout(1,1));
    creditsPanel.add(new JLabel(new ImageIcon(getClass().getClassLoader().getResource("credits.png"))));
    window.getContentPane().add(creditsPanel,"CreditsPanel");

    //Makes certain panels visible/invisible
    gamePanel.setVisible(false);
    optionsPanel.setVisible(false);
    rulesPanel.setVisible(false);
    controlsPanel.setVisible(false);
    creditsPanel.setVisible(false);
    titlePanel.setVisible(true);
    window.setVisible(true);

    //double buffering
    createBufferStrategy(2);
    bs = getBufferStrategy();
  }

  /*This method runs the actual playing part of the game. It goes in a loop while game is true and constantly paints the game,
    requests the focus, checks for collisions, and checks if anyone won with a small delay. If game is false, it leaves the loop and the game is done.*/
  public void game () //constantly runs update and paint world    
  {
      //Loops while game is true
      while (game)
      {
        //Paints the game
        paintGame ();

        //Requests the focus for listening to key events
        window.requestFocus();

        //Checks if any player has collided with anything
        checkCollision();

        //Checks if any player has won
        checkWin();

        //Sleeps for SPEED milliseconds
        try { Thread.sleep (SPEED);}//delay
        catch (InterruptedException e){ }

        //Checks if window closed. If so, game is false
        if(!isVisible())
        {
            game = false;
        }
       }

      //Makes the mode 5 so that it still stays on the game screen after game is done
      mode = 5;

      //Paints the game once more
      paintGame();
  }
  //For KeyEvents that invole keys being pressed
  public void keyPressed (KeyEvent event)
  {
    //Assigns the keyPressed array at event.getKeyCode() to true as the key has been pressed
    keyPressed[event.getKeyCode()] = true;

    //Calls the update positions method to update the positions of the sprites.
    updatePositions();
  }
  
  //checking for key pressed
  public void keyReleased(KeyEvent event)
  {
    //Assigns the keyPressed array at event.getKeyCode() to false as the key has been released
    keyPressed[event.getKeyCode()] = false;
  }
  
  //checking for key pressed
  public void keyTyped(KeyEvent event)
  {
  }
  
  //Draws anything that is on the screen by using a Graphics object and using methods such as drawImage and drawString
  public void paintGame ()
    {
    //Creates a Graphics object
    Graphics g = bs.getDrawGraphics ();
    //draws background
    g.drawImage(gridBackground,0,0,this);

    //draws scoreboard
    g.drawImage(scoreBoard,650,0,this);

    //Switch statement used for drawing the information of the players on the right side.
    switch(numOfPlayers)
    {
        case 4: g.setColor(Color.RED);
                g.setFont(new Font("SansSerif",Font.BOLD,35));
                g.drawString(player[3].getName() + ":",675,430);
                if(winPlayer == 3)
                {
                    g.drawImage(miscBombermanSprite[7],670,443,this);
                }
                else if(player[3].getHealth() > 0)
                {
                    g.drawImage(bombermanSprite[12],670,443,this);
                }
                else
                {
                    g.drawImage(miscBombermanSprite[6],670,443,this);
                }
                //Loops to draw the hearts
                for(int i = 0; i < player[3].getHealth();i++)
                {
                    g.drawImage(heart,720 + (i * 40),455,this);
                }

        case 3: g.setColor(Color.GREEN);
                g.setFont(new Font("SansSerif",Font.BOLD,35));
                g.drawString(player[2].getName() + ":",675,320);
                if(winPlayer == 2)
                {
                    g.drawImage(miscBombermanSprite[5],670,333,this);
                }
                else if(player[2].getHealth() > 0)
                {
                    g.drawImage(bombermanSprite[8],670,333,this);
                }
                else
                {
                    g.drawImage(miscBombermanSprite[4],670,333,this);
                }
                //Loops to draw the hearts
                for(int i = 0; i < player[2].getHealth();i++)
                {
                    g.drawImage(heart,720 + (i * 40),345,this);
                }

        case 2: g.setColor(Color.BLACK);
                g.setFont(new Font("SansSerif",Font.BOLD,35));
                g.drawString(player[1].getName() + ":",675,210);
                if(winPlayer == 1)
                {
                    g.drawImage(miscBombermanSprite[3],670,223,this);
                }
                else if(player[1].getHealth() > 0)
                {
                    g.drawImage(bombermanSprite[4],670,223,this);
                }
                else
                {
                    g.drawImage(miscBombermanSprite[2],670,223,this);
                }
                //Loops to draw the hearts
                for(int i = 0; i < player[1].getHealth();i++)
                {
                    g.drawImage(heart,720 + (i * 40),235,this);
                }

        case 1: g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif",Font.BOLD,35));
                g.drawString(player[0].getName() + ":",675,100);
                if (winPlayer == 0)
                {
                    g.drawImage(miscBombermanSprite[1],670,113,this);
                }
                else if(player[0].getHealth() > 0)
                {
                    g.drawImage(bombermanSprite[0],670,113,this);
                }
                else
                {
                    g.drawImage(miscBombermanSprite[0],670,113,this);
                }
                //Loops to draw the hearts
                for(int i = 0; i < player[0].getHealth();i++)
                {
                    g.drawImage(heart,720 + (i * 40),125,this);
                }

                //Breaks from the switch statement
                break;
    }

    //Draws the map
    for(int i = 0; i < 11; i++)
    {
        for(int j = 0; j < 11; j++)
        {
            g.drawImage(tileImage[grid[i][j]],i * 50 + 50,j * 50 + 50,this);
        }
    }
    
    //Draws the bomberman sprites
    switch (numOfPlayers)
    {
            case 4: if(player[3].getHealth() > 0)g.drawImage(bombermanSprite[12 + player[3].getDirection()],player[3].getXPos(),player[3].getYPos(),this);
            case 3: if(player[2].getHealth() > 0)g.drawImage(bombermanSprite[8 + player[2].getDirection()],player[2].getXPos(),player[2].getYPos(),this);
            case 2: if(player[1].getHealth() > 0)g.drawImage(bombermanSprite[4 + player[1].getDirection()],player[1].getXPos(),player[1].getYPos(),this);
            case 1: if(player[0].getHealth() > 0)g.drawImage(bombermanSprite[0 + player[0].getDirection()],player[0].getXPos(),player[0].getYPos(),this);break;
    }

    //Draws that a player has won if winPlayer ranges from 1 to 3
    if (winPlayer >= 0 && winPlayer != 5)
    {
        g.setColor(Color.ORANGE);
        g.setFont(new Font("SansSerif",Font.BOLD,50));
        g.drawString("Player " + (winPlayer + 1) + " WINS!",150,290);
    }
    //Draws that there is a draw if winPlayer is -1
    else if(winPlayer == -1)
    {
        g.setColor(Color.ORANGE);
        g.setFont(new Font("SansSerif",Font.BOLD,50));
        g.drawString("DRAW",250,290);
    }

    //show new buffer
    bs.show();
 }

  //This method updates the positions of the sprites based on the values in the keyPressed array
  public void updatePositions()
  {
    //Switch statement to see for which players it is to update positions
    switch(numOfPlayers)
    {
      case 4:
          //Only updates if the player is alive
          if(player[3].getHealth() > 0)
        {
            //Checks if the player is in a dropping bombs state to make the player drop bombs all the time
            if(player[3].getState() == 4)
            {
                //Makes the bomb key true so that the player drops a bomb
                keyPressed[KeyEvent.VK_SPACE] = true;
            }

            //Checks what keys have been pressed and moves accordingly. IF there is an obstacle in the way, the program does not let the player move
            if(keyPressed[KeyEvent.VK_G])
            {
                if((player[3].getGridXPos() - 1) >= 0 && (grid[(player[3].getGridXPos() - 1)][player[3].getGridYPos()] == 0 || (grid[(player[3].getGridXPos() - 1)][player[3].getGridYPos()] >= 6 && grid[(player[3].getGridXPos() - 1)][player[3].getGridYPos()] <= 10) || player[3].getState() == 2))
                {
                    player[3].setXPos(player[3].getXPos() - VELOCITY);
                    player[3].setDirection(1);
                }
            }
            else if(keyPressed[KeyEvent.VK_Y])
            {
                if((player[3].getGridYPos() - 1) >= 0 && (grid[player[3].getGridXPos()][(player[3].getGridYPos() - 1)] == 0 || (grid[(player[3].getGridXPos())][player[3].getGridYPos() - 1] >= 6 && grid[(player[3].getGridXPos())][player[3].getGridYPos() - 1] <= 10) || player[3].getState() == 2))
                {
                    player[3].setYPos(player[3].getYPos() - VELOCITY);
                    player[3].setDirection(2);
                }
            }
            else if(keyPressed[KeyEvent.VK_J])
            {
                if((player[3].getGridXPos() + 1) <= 10 && (grid[(player[3].getGridXPos() + 1)][player[3].getGridYPos()] == 0 || (grid[(player[3].getGridXPos() + 1)][player[3].getGridYPos()] >= 6 && grid[(player[3].getGridXPos() + 1)][player[3].getGridYPos()] <= 10) || player[3].getState() == 2))
                {
                    player[3].setXPos(player[3].getXPos() + VELOCITY);
                    player[3].setDirection(3);
                }
            }
            else if (keyPressed[KeyEvent.VK_H])
            {
                if((player[3].getGridYPos() + 1) <= 10 && (grid[player[3].getGridXPos()][(player[3].getGridYPos() + 1)] == 0 || (grid[(player[3].getGridXPos())][player[3].getGridYPos() + 1] >= 6 && grid[(player[3].getGridXPos())][player[3].getGridYPos() + 1] <= 10) || player[3].getState() == 2))
                {
                    player[3].setYPos(player[3].getYPos() + VELOCITY);
                    player[3].setDirection(0);
                }
            }

            //Checks if the player is dropping a bomb
            if (keyPressed[KeyEvent.VK_SPACE])
            {
              //Checks if the player can drop a bomb in that place
              if (grid[player[3].getGridXPos()][player[3].getGridYPos()] == 0)
                {
                    //Makes sure the player did not reach their bomb limit
                    if (player[3].getUsedBombs() < player[3].getMaxBombs())
                    {
                        //Adds an element, of type Bomb, to the bombs ArrayList and increases the bombs used by the player by 1
                        bombs.add(new Bomb(3,player[3].getBombRange()));
                        player[3].bombUsed();
                    }
                }
            }

            //If the player is in phase mode, which is when he gets the power-up to walk through obstacles, it checks if it can stop phase as in some places, stopping phase can trap the player.
            if(grid[player[3].getGridXPos()][player[3].getGridYPos()]!= 1 && grid[player[3].getGridXPos()][player[3].getGridYPos()]!= 2 && player[3].checkPostPhase())
            {
                player[3].setState(3);
                changeSprites(1,player[3].getPlayerNum());
                player[3].phaseOver();
            }
        }
      case 3:
          //Only updates if player is alive
          if(player[2].getHealth() > 0)
        {
            //Checks if the player is in a dropping bombs state to make the player drop bombs all the time
            if(player[2].getState() == 4)
            {
                //Makes the bomb key true so that the player drops a bomb
                keyPressed[KeyEvent.VK_SHIFT] = true;
            }

            //Checks what keys have been pressed and moves accordingly. IF there is an obstacle in the way, the program does not let the player move
            if(keyPressed[KeyEvent.VK_L])
            {
                if((player[2].getGridXPos() - 1) >= 0 && (grid[(player[2].getGridXPos() - 1)][player[2].getGridYPos()] == 0 || (grid[(player[2].getGridXPos() - 1)][player[2].getGridYPos()] >= 6 && grid[(player[2].getGridXPos() - 1)][player[2].getGridYPos()] <= 10) || player[2].getState() == 2))
                {
                    player[2].setXPos(player[2].getXPos() - VELOCITY);
                    player[2].setDirection(1);
                }
            }
            else if(keyPressed[KeyEvent.VK_P])
            {
                if((player[2].getGridYPos() - 1) >= 0 && (grid[player[2].getGridXPos()][(player[2].getGridYPos() - 1)] == 0 || (grid[(player[2].getGridXPos())][player[2].getGridYPos() - 1] >= 6 && grid[(player[2].getGridXPos())][player[2].getGridYPos() - 1] <= 10) || player[2].getState() == 2))
                {
                    player[2].setYPos(player[2].getYPos() - VELOCITY);
                    player[2].setDirection(2);
                }
            }
            else if(keyPressed[KeyEvent.VK_QUOTE])
            {
                if((player[2].getGridXPos() + 1) <= 10 && (grid[(player[2].getGridXPos() + 1)][player[2].getGridYPos()] == 0 || (grid[(player[2].getGridXPos() + 1)][player[2].getGridYPos()] >= 6 && grid[(player[2].getGridXPos() + 1)][player[2].getGridYPos()] <= 10) || player[2].getState() == 2))
                {
                    player[2].setXPos(player[2].getXPos() + VELOCITY);
                    player[2].setDirection(3);
                }
            }
            else if (keyPressed[KeyEvent.VK_SEMICOLON])
            {
                if((player[2].getGridYPos() + 1) <= 10 && (grid[player[2].getGridXPos()][(player[2].getGridYPos() + 1)] == 0 || (grid[(player[2].getGridXPos())][player[2].getGridYPos() + 1] >= 6 && grid[(player[2].getGridXPos())][player[2].getGridYPos() + 1] <= 10) || player[2].getState() == 2))
                {
                    player[2].setYPos(player[2].getYPos() + VELOCITY);
                    player[2].setDirection(0);
                }
            }
            //Checks if the player is dropping a bomb
            if (keyPressed[KeyEvent.VK_SHIFT])
            {
              //Checks if the player can drop a bomb in that place
              if (grid[player[2].getGridXPos()][player[2].getGridYPos()] == 0)
                {
                    //Makes sure the player did not reach their bomb limit
                    if (player[2].getUsedBombs() < player[2].getMaxBombs())
                    {
                        //Adds an element, of type Bomb, to the bombs ArrayList and increases the bombs used by the player by 1
                        bombs.add(new Bomb(2,player[2].getBombRange()));
                        player[2].bombUsed();
                    }
                }
            }

            //If the player is in phase mode, which is when he gets the power-up to walk through obstacles, it checks if it can stop phase as in some places, stopping phase can trap the player.
            if(grid[player[2].getGridXPos()][player[2].getGridYPos()]!= 1 && grid[player[2].getGridXPos()][player[2].getGridYPos()]!= 2 && player[2].checkPostPhase())
            {
                player[2].setState(2);
                changeSprites(1,player[2].getPlayerNum());
                player[2].phaseOver();
            }
        }
        case 2:

          //Only updates if player is alive
          if(player[1].getHealth() > 0)
        {
            //Checks if the player is in a dropping bombs state to make the player drop bombs all the time
            if(player[1].getState() == 4)
            {
                //Makes the bomb key true so that the player drops a bomb
                keyPressed[KeyEvent.VK_CONTROL] = true;
            }

            //Checks what keys have been pressed and moves accordingly. IF there is an obstacle in the way, the program does not let the player move
            if(keyPressed[KeyEvent.VK_A])
            {
                if((player[1].getGridXPos() - 1) >= 0 && (grid[(player[1].getGridXPos() - 1)][player[1].getGridYPos()] == 0 || (grid[(player[1].getGridXPos() - 1)][player[1].getGridYPos()] >= 6 && grid[(player[1].getGridXPos() - 1)][player[1].getGridYPos()] <= 10) || player[1].getState() == 2))
                {
                    player[1].setXPos(player[1].getXPos() - VELOCITY);
                    player[1].setDirection(1);
                }
            }
            else if(keyPressed[KeyEvent.VK_W])
            {
                if((player[1].getGridYPos() - 1) >= 0 && (grid[player[1].getGridXPos()][(player[1].getGridYPos() - 1)] == 0 || (grid[(player[1].getGridXPos())][player[1].getGridYPos() - 1] >= 6 && grid[(player[1].getGridXPos())][player[1].getGridYPos() - 1] <= 10) || player[1].getState() == 2))
                {
                    player[1].setYPos(player[1].getYPos() - VELOCITY);
                    player[1].setDirection(2);
                }
            }
            else if(keyPressed[KeyEvent.VK_D])
            {
                if((player[1].getGridXPos() + 1) <= 10 && (grid[(player[1].getGridXPos() + 1)][player[1].getGridYPos()] == 0 || (grid[(player[1].getGridXPos() + 1)][player[1].getGridYPos()] >= 6 && grid[(player[1].getGridXPos() + 1)][player[1].getGridYPos()] <= 10) || player[1].getState() == 2))
                {
                    player[1].setXPos(player[1].getXPos() + VELOCITY);
                    player[1].setDirection(3);
                }
            }
            else if (keyPressed[KeyEvent.VK_S])
            {
                if((player[1].getGridYPos() + 1) <= 10 && (grid[player[1].getGridXPos()][(player[1].getGridYPos() + 1)] == 0 || (grid[(player[1].getGridXPos())][player[1].getGridYPos() + 1] >= 6 && grid[(player[1].getGridXPos())][player[1].getGridYPos() + 1] <= 10) || player[1].getState() == 2))
                {
                    player[1].setYPos(player[1].getYPos() + VELOCITY);
                    player[1].setDirection(0);
                }
            }
            //Checks if the player is dropping a bomb
            if (keyPressed[KeyEvent.VK_CONTROL])
            {
              //Checks if the player can drop a bomb in that place
              if (grid[player[1].getGridXPos()][player[1].getGridYPos()] == 0)
                {
                    //Makes sure the player did not reach their bomb limit
                    if (player[1].getUsedBombs() < player[1].getMaxBombs())
                    {
                        //Adds an element, of type Bomb, to the bombs ArrayList and increases the bombs used by the player by 1
                        bombs.add(new Bomb(1,player[1].getBombRange()));
                        player[1].bombUsed();
                    }
                }
            }

            //If the player is in phase mode, which is when he gets the power-up to walk through obstacles, it checks if it can stop phase as in some places, stopping phase can trap the player.
            if(grid[player[1].getGridXPos()][player[1].getGridYPos()]!= 1 && grid[player[1].getGridXPos()][player[1].getGridYPos()]!= 2 && player[1].checkPostPhase())
            {
                player[1].setState(3);
                changeSprites(1,player[1].getPlayerNum());
                player[1].phaseOver();
            }
        }

        case 1:
        //Only updates if player is aliv
        if(player[0].getHealth() > 0)
        {
            //Checks if the player is in a dropping bombs state to make the player drop bombs all the time
            if(player[0].getState() == 4)
            {
                //Makes the bomb key true so that the player drops a bomb
                keyPressed[KeyEvent.VK_NUMPAD0] = true;
            }

            //Checks what keys have been pressed and moves accordingly. IF there is an obstacle in the way, the program does not let the player move
            if(keyPressed[KeyEvent.VK_LEFT])
            {
                if((player[0].getGridXPos() - 1) >= 0 && (grid[(player[0].getGridXPos() - 1)][player[0].getGridYPos()] == 0 || (grid[(player[0].getGridXPos() - 1)][player[0].getGridYPos()] >= 6 && grid[(player[0].getGridXPos() - 1)][player[0].getGridYPos()] <= 10) || player[0].getState() == 2))
                {
                    player[0].setXPos(player[0].getXPos() - VELOCITY);
                    player[0].setDirection(1);
                }
            }
            else if(keyPressed[KeyEvent.VK_UP])
            {
                if((player[0].getGridYPos() - 1) >= 0 && (grid[player[0].getGridXPos()][(player[0].getGridYPos() - 1)] == 0 || (grid[(player[0].getGridXPos())][player[0].getGridYPos() - 1] >= 6 && grid[(player[0].getGridXPos())][player[0].getGridYPos() - 1] <= 10) || player[0].getState() == 2))
                {
                    player[0].setYPos(player[0].getYPos() - VELOCITY);
                    player[0].setDirection(2);
                }
            }
            else if(keyPressed[KeyEvent.VK_RIGHT])
            {
                if((player[0].getGridXPos() + 1) <= 10 && (grid[(player[0].getGridXPos() + 1)][player[0].getGridYPos()] == 0 || (grid[(player[0].getGridXPos() + 1)][player[0].getGridYPos()] >= 6 && grid[(player[0].getGridXPos() + 1)][player[0].getGridYPos()] <= 10) || player[0].getState() == 2))
                {
                    player[0].setXPos(player[0].getXPos() + VELOCITY);
                    player[0].setDirection(3);
                }
            }
            else if (keyPressed[KeyEvent.VK_DOWN])
            {
                if((player[0].getGridYPos() + 1) <= 10 && (grid[player[0].getGridXPos()][(player[0].getGridYPos() + 1)] == 0 || (grid[(player[0].getGridXPos())][player[0].getGridYPos() + 1] >= 6 && grid[(player[0].getGridXPos())][player[0].getGridYPos() + 1] <= 10) || player[0].getState() == 2))
                {
                    player[0].setYPos(player[0].getYPos() + VELOCITY);
                    player[0].setDirection(0);
                }
            }
            //Checks if the player is dropping a bomb
            if (keyPressed[KeyEvent.VK_NUMPAD0])
            {
              //Checks if the player can drop a bomb in that place
              if (grid[player[0].getGridXPos()][player[0].getGridYPos()] == 0)
                {
                    //Makes sure the player did not reach their bomb limit
                    if (player[0].getUsedBombs() < player[0].getMaxBombs())
                    {
                        //Adds an element, of type Bomb, to the bombs ArrayList and increases the bombs used by the player by 1
                        bombs.add(new Bomb(0,player[0].getBombRange()));
                        player[0].bombUsed();
                    }
                }
            }

            //If the player is in phase mode, which is when he gets the power-up to walk through obstacles, it checks if it can stop phase as in some places, stopping phase can trap the player.
            if(grid[player[0].getGridXPos()][player[0].getGridYPos()]!= 1 && grid[player[0].getGridXPos()][player[0].getGridYPos()]!= 2 && player[0].checkPostPhase())
            {
                player[0].setState(0);
                changeSprites(1,player[0].getPlayerNum());
                player[0].phaseOver();
            }
        }
    }
  }

  //This method checks to see if the location of the sprites is shared with anything else such as a bomb or power-up
  public void checkCollision()
  {
        //Uses a switch statement to see what the sprite is on for the players. If the sprite is on a bomb, the player gets blasted. If the sprite is on a power-up, the player picks it up.
          switch (numOfPlayers)
          {
            case 4: if(grid[player[3].getGridXPos()][player[3].getGridYPos()] == 6 && player[3].getState() != 1){player[3].blasted();}
                    else if(grid[player[3].getGridXPos()][player[3].getGridYPos()] == 7 && player[3].getState() != 1){player[3].setState(2); grid[player[3].getGridXPos()][player[3].getGridYPos()] = 0;}
                    else if(grid[player[3].getGridXPos()][player[3].getGridYPos()] == 8 && player[3].getState() != 1){player[3].increaseBombRange(1); grid[player[3].getGridXPos()][player[3].getGridYPos()] = 0;}
                    else if(grid[player[3].getGridXPos()][player[3].getGridYPos()] == 9 && player[3].getState() != 1){player[3].increaseBombLimit(1); grid[player[3].getGridXPos()][player[3].getGridYPos()] = 0;}
                    else if(grid[player[3].getGridXPos()][player[3].getGridYPos()] == 10 && player[3].getState() != 1){player[3].setState(4); grid[player[3].getGridXPos()][player[3].getGridYPos()] = 0;}
            case 3: if(grid[player[2].getGridXPos()][player[2].getGridYPos()] == 6 && player[2].getState() != 1){player[2].blasted();}
                    else if(grid[player[2].getGridXPos()][player[2].getGridYPos()] == 7 && player[2].getState() != 1){player[2].setState(2); grid[player[2].getGridXPos()][player[2].getGridYPos()] = 0;}
                    else if(grid[player[2].getGridXPos()][player[2].getGridYPos()] == 8 && player[2].getState() != 1){player[2].increaseBombRange(1); grid[player[2].getGridXPos()][player[2].getGridYPos()] = 0;}
                    else if(grid[player[2].getGridXPos()][player[2].getGridYPos()] == 9 && player[2].getState() != 1){player[2].increaseBombLimit(1); grid[player[2].getGridXPos()][player[2].getGridYPos()] = 0;}
                    else if(grid[player[2].getGridXPos()][player[2].getGridYPos()] == 10 && player[2].getState() != 1){player[2].setState(4); grid[player[2].getGridXPos()][player[2].getGridYPos()] = 0;}
            case 2: if(grid[player[1].getGridXPos()][player[1].getGridYPos()] == 6 && player[1].getState() != 1){player[1].blasted();}
                    else if(grid[player[1].getGridXPos()][player[1].getGridYPos()] == 7 && player[1].getState() != 1){player[1].setState(2); grid[player[1].getGridXPos()][player[1].getGridYPos()] = 0;}
                    else if(grid[player[1].getGridXPos()][player[1].getGridYPos()] == 8 && player[1].getState() != 1){player[1].increaseBombRange(1); grid[player[1].getGridXPos()][player[1].getGridYPos()] = 0;}
                    else if(grid[player[1].getGridXPos()][player[1].getGridYPos()] == 9 && player[1].getState() != 1){player[1].increaseBombLimit(1); grid[player[1].getGridXPos()][player[1].getGridYPos()] = 0;}
                    else if(grid[player[1].getGridXPos()][player[1].getGridYPos()] == 10 && player[1].getState() != 1){player[1].setState(4); grid[player[1].getGridXPos()][player[1].getGridYPos()] = 0;}
            case 1: if(grid[player[0].getGridXPos()][player[0].getGridYPos()] == 6 && player[0].getState() != 1){player[0].blasted();}
                    else if(grid[player[0].getGridXPos()][player[0].getGridYPos()] == 7 && player[0].getState() != 1){player[0].setState(2); grid[player[0].getGridXPos()][player[0].getGridYPos()] = 0;}
                    else if(grid[player[0].getGridXPos()][player[0].getGridYPos()] == 8 && player[0].getState() != 1){player[0].increaseBombRange(1); grid[player[0].getGridXPos()][player[0].getGridYPos()] = 0;}
                    else if(grid[player[0].getGridXPos()][player[0].getGridYPos()] == 9 && player[0].getState() != 1){player[0].increaseBombLimit(1); grid[player[0].getGridXPos()][player[0].getGridYPos()] = 0;}
                    else if(grid[player[0].getGridXPos()][player[0].getGridYPos()] == 10 && player[0].getState() != 1){player[0].setState(4); grid[player[0].getGridXPos()][player[0].getGridYPos()] = 0;}
          }
  }

  //This method loads the map required and loads all the images required
  public void load()
  {
    try
    {
      //Loads images
      gridBackground = ImageIO.read(getClass().getClassLoader().getResource("gridbackground.png"));
      System.out.println("LOADED background");
      scoreBoard = ImageIO.read(getClass().getClassLoader().getResource("scoreBoard.png"));
      heart = ImageIO.read(getClass().getClassLoader().getResource("heart.png"));
      tileImage[0] = ImageIO.read(getClass().getClassLoader().getResource("floor.png"));
      tileImage[1] = ImageIO.read(getClass().getClassLoader().getResource("block.png"));
      tileImage[2] = ImageIO.read(getClass().getClassLoader().getResource("brick.png"));
      tileImage[5] = ImageIO.read(getClass().getClassLoader().getResource("bomb1.png"));
      tileImage[6] = ImageIO.read(getClass().getClassLoader().getResource("blast2.png"));
      tileImage[7] = ImageIO.read(getClass().getClassLoader().getResource("phase.png"));
      tileImage[8] = ImageIO.read(getClass().getClassLoader().getResource("flame.png"));
      tileImage[9] = ImageIO.read(getClass().getClassLoader().getResource("bomb.png"));
      tileImage[10] = ImageIO.read(getClass().getClassLoader().getResource("evilBomb.png"));

      //Loads all the sprites
      switch (numOfPlayers) 
        {
            case 4: normalBombermanSprite[12] = ImageIO.read(getClass().getClassLoader().getResource("rb-s1.png"));
                    normalBombermanSprite[13] = ImageIO.read(getClass().getClassLoader().getResource("rb-w1.png"));
                    normalBombermanSprite[14] = ImageIO.read(getClass().getClassLoader().getResource("rb-n1.png"));
                    normalBombermanSprite[15] = ImageIO.read(getClass().getClassLoader().getResource("rb-e1.png"));
                    bombermanSprite[12] = ImageIO.read(getClass().getClassLoader().getResource("rb-s1.png"));
                    bombermanSprite[13] = ImageIO.read(getClass().getClassLoader().getResource("rb-w1.png"));
                    bombermanSprite[14] = ImageIO.read(getClass().getClassLoader().getResource("rb-n1.png"));
                    bombermanSprite[15] = ImageIO.read(getClass().getClassLoader().getResource("rb-e1.png"));
                    transparentBombermanSprite[12] = ImageIO.read(getClass().getClassLoader().getResource("rb-s1b.png"));
                    transparentBombermanSprite[13] = ImageIO.read(getClass().getClassLoader().getResource("rb-w1b.png"));
                    transparentBombermanSprite[14] = ImageIO.read(getClass().getClassLoader().getResource("rb-n1b.png"));
                    transparentBombermanSprite[15] = ImageIO.read(getClass().getClassLoader().getResource("rb-e1b.png"));
                    phaseBombermanSprite[12] = ImageIO.read(getClass().getClassLoader().getResource("rb-s1c.png"));
                    phaseBombermanSprite[13] = ImageIO.read(getClass().getClassLoader().getResource("rb-w1c.png"));
                    phaseBombermanSprite[14] = ImageIO.read(getClass().getClassLoader().getResource("rb-n1c.png"));
                    phaseBombermanSprite[15] = ImageIO.read(getClass().getClassLoader().getResource("rb-e1c.png"));
                    miscBombermanSprite[6] = ImageIO.read(getClass().getClassLoader().getResource("rb-s1d.png"));
                    miscBombermanSprite[7] = ImageIO.read(getClass().getClassLoader().getResource("rb-s1e.png"));

            case 3: normalBombermanSprite[8] = ImageIO.read(getClass().getClassLoader().getResource("gb-s1.png"));
                    normalBombermanSprite[9] = ImageIO.read(getClass().getClassLoader().getResource("gb-w1.png"));
                    normalBombermanSprite[10] = ImageIO.read(getClass().getClassLoader().getResource("gb-n1.png"));
                    normalBombermanSprite[11] = ImageIO.read(getClass().getClassLoader().getResource("gb-e1.png"));
                    bombermanSprite[8] = ImageIO.read(getClass().getClassLoader().getResource("gb-s1.png"));
                    bombermanSprite[9] = ImageIO.read(getClass().getClassLoader().getResource("gb-w1.png"));
                    bombermanSprite[10] = ImageIO.read(getClass().getClassLoader().getResource("gb-n1.png"));
                    bombermanSprite[11] = ImageIO.read(getClass().getClassLoader().getResource("gb-e1.png"));
                    transparentBombermanSprite[8] = ImageIO.read(getClass().getClassLoader().getResource("gb-s1b.png"));
                    transparentBombermanSprite[9] = ImageIO.read(getClass().getClassLoader().getResource("gb-w1b.png"));
                    transparentBombermanSprite[10] = ImageIO.read(getClass().getClassLoader().getResource("gb-n1b.png"));
                    transparentBombermanSprite[11] = ImageIO.read(getClass().getClassLoader().getResource("gb-e1b.png"));  
                    phaseBombermanSprite[8] = ImageIO.read(getClass().getClassLoader().getResource("gb-s1c.png"));
                    phaseBombermanSprite[9] = ImageIO.read(getClass().getClassLoader().getResource("gb-w1c.png"));
                    phaseBombermanSprite[10] = ImageIO.read(getClass().getClassLoader().getResource("gb-n1c.png"));
                    phaseBombermanSprite[11] = ImageIO.read(getClass().getClassLoader().getResource("gb-e1c.png"));
                    miscBombermanSprite[4] = ImageIO.read(getClass().getClassLoader().getResource("gb-s1d.png"));
                    miscBombermanSprite[5] = ImageIO.read(getClass().getClassLoader().getResource("gb-s1e.png"));            
            case 2: normalBombermanSprite[4] = ImageIO.read(getClass().getClassLoader().getResource("bb-s1.png"));
                    normalBombermanSprite[5] = ImageIO.read(getClass().getClassLoader().getResource("bb-w1.png"));
                    normalBombermanSprite[6] = ImageIO.read(getClass().getClassLoader().getResource("bb-n1.png"));
                    normalBombermanSprite[7] = ImageIO.read(getClass().getClassLoader().getResource("bb-e1.png"));
                    bombermanSprite[4] = ImageIO.read(getClass().getClassLoader().getResource("bb-s1.png"));
                    bombermanSprite[5] = ImageIO.read(getClass().getClassLoader().getResource("bb-w1.png"));
                    bombermanSprite[6] = ImageIO.read(getClass().getClassLoader().getResource("bb-n1.png"));
                    bombermanSprite[7] = ImageIO.read(getClass().getClassLoader().getResource("bb-e1.png"));
                    transparentBombermanSprite[4] = ImageIO.read(getClass().getClassLoader().getResource("bb-s1b.png"));
                    transparentBombermanSprite[5] = ImageIO.read(getClass().getClassLoader().getResource("bb-w1b.png"));
                    transparentBombermanSprite[6] = ImageIO.read(getClass().getClassLoader().getResource("bb-n1b.png"));
                    transparentBombermanSprite[7] = ImageIO.read(getClass().getClassLoader().getResource("bb-e1b.png"));
                    phaseBombermanSprite[4] = ImageIO.read(getClass().getClassLoader().getResource("bb-s1c.png"));
                    phaseBombermanSprite[5] = ImageIO.read(getClass().getClassLoader().getResource("bb-w1c.png"));
                    phaseBombermanSprite[6] = ImageIO.read(getClass().getClassLoader().getResource("bb-n1c.png"));
                    phaseBombermanSprite[7] = ImageIO.read(getClass().getClassLoader().getResource("bb-e1c.png"));
                    miscBombermanSprite[2] = ImageIO.read(getClass().getClassLoader().getResource("bb-s1d.png"));
                    miscBombermanSprite[3] = ImageIO.read(getClass().getClassLoader().getResource("bb-s1e.png"));
            case 1: normalBombermanSprite[0] = ImageIO.read(getClass().getClassLoader().getResource("wb-s1.png"));
                    normalBombermanSprite[1] = ImageIO.read(getClass().getClassLoader().getResource("wb-w1.png"));
                    normalBombermanSprite[2] = ImageIO.read(getClass().getClassLoader().getResource("wb-n1.png"));
                    normalBombermanSprite[3] = ImageIO.read(getClass().getClassLoader().getResource("wb-e1.png"));
                    bombermanSprite[0] = ImageIO.read(getClass().getClassLoader().getResource("wb-s1.png"));
                    bombermanSprite[1] = ImageIO.read(getClass().getClassLoader().getResource("wb-w1.png"));
                    bombermanSprite[2] = ImageIO.read(getClass().getClassLoader().getResource("wb-n1.png"));
                    bombermanSprite[3] = ImageIO.read(getClass().getClassLoader().getResource("wb-e1.png"));
                    transparentBombermanSprite[0] = ImageIO.read(getClass().getClassLoader().getResource("wb-s1b.png"));
                    transparentBombermanSprite[1] = ImageIO.read(getClass().getClassLoader().getResource("wb-w1b.png"));
                    transparentBombermanSprite[2] = ImageIO.read(getClass().getClassLoader().getResource("wb-n1b.png"));
                    transparentBombermanSprite[3] = ImageIO.read(getClass().getClassLoader().getResource("wb-e1b.png"));
                    phaseBombermanSprite[0] = ImageIO.read(getClass().getClassLoader().getResource("wb-s1c.png"));
                    phaseBombermanSprite[1] = ImageIO.read(getClass().getClassLoader().getResource("wb-w1c.png"));
                    phaseBombermanSprite[2] = ImageIO.read(getClass().getClassLoader().getResource("wb-n1c.png"));
                    phaseBombermanSprite[3] = ImageIO.read(getClass().getClassLoader().getResource("wb-e1c.png"));
                    miscBombermanSprite[0] = ImageIO.read(getClass().getClassLoader().getResource("wb-s1d.png"));
                    miscBombermanSprite[1] = ImageIO.read(getClass().getClassLoader().getResource("wb-s1e.png"));break;
         }
    }
    catch(Exception e)
    {
        System.out.println("Images Not Found");
    }
  }

  //This method loads the map from a text file. It reads the numbers from the file and assigns them to the grid array.
  public void loadMap()
  {
      try
      {
          //Creates necessary objects and variables
          BufferedReader br = new BufferedReader(new FileReader(map + ".txt"));
          StringTokenizer st;
          String s;

          //Loops to go through every number in the file
          for(int i = 0; i < 11; i++)
          {
            //Uses the string tokenizer to get numbers that are seperated by a space
            st = new StringTokenizer(br.readLine()," ");
            for(int j = 0; j < 11; j++)
            {
              //Assigns the value in the file to grid[i][j]
              grid[i][j] = Integer.parseInt(st.nextToken());
            }
          }
      }
      catch(Exception e)
      {
          System.out.println("Map not fount");
      }
  }

  //This method changes the sprites of a player. It requires two parameters. n represents which set of sprites is to be changed for. p represents the player number
  public static void changeSprites(int n, int p)
  {
     //Uses a switch statement to switch sprites for the players that are playing the game
     switch(n)
     {
         case 1:bombermanSprite[p * 4 + 0] = normalBombermanSprite[p*4 + 0];
                bombermanSprite[p * 4 + 1] = normalBombermanSprite[p*4 + 1];
                bombermanSprite[p * 4 + 2] = normalBombermanSprite[p*4 + 2];
                bombermanSprite[p * 4 + 3] = normalBombermanSprite[p*4 + 3];break;
         case 2:bombermanSprite[p * 4 + 0] = transparentBombermanSprite[p*4 + 0];
                bombermanSprite[p * 4 + 1] = transparentBombermanSprite[p*4 + 1];
                bombermanSprite[p * 4 + 2] = transparentBombermanSprite[p*4 + 2];
                bombermanSprite[p * 4 + 3] = transparentBombermanSprite[p*4 + 3];break;
         case 3:bombermanSprite[p * 4 + 0] = phaseBombermanSprite[p*4 + 0];
                bombermanSprite[p * 4 + 1] = phaseBombermanSprite[p*4 + 1];
                bombermanSprite[p * 4 + 2] = phaseBombermanSprite[p*4 + 2];
                bombermanSprite[p * 4 + 3] = phaseBombermanSprite[p*4 + 3];break;
         case 4:bombermanSprite[p * 4 + 0] = phaseBombermanSprite[p*4 + 0];
                bombermanSprite[p * 4 + 1] = phaseBombermanSprite[p*4 + 1];
                bombermanSprite[p * 4 + 2] = phaseBombermanSprite[p*4 + 2];
                bombermanSprite[p * 4 + 3] = phaseBombermanSprite[p*4 + 3];break;
     }
  }

  //This method checks to see if any player has won or if there is a draw
  public void checkWin()
  {
      //Checks if there is only one player alive
      if(numOfPlayers - deadPlayers == 1)
      {
          //Finds the player that is alive. The only player to be alive is the player that wins
          for(int i = 0; i < numOfPlayers; i++)
          {
            if(player[i].getHealth() > 0)
            {
                //Assigns winPlayer the player number and assigns game false as the game has ended
                winPlayer = i;
                game = false;
            }
          }
      }
      //Checks if all players are dead
      else if (numOfPlayers - deadPlayers == 0)
      {
          //Assigns winPlayer the value -1 as the game is a draw and assigns game false as the game has ended
          winPlayer = -1;
          game = false;
      }
  }

  //This method starts a newGame by initializing everything that is necessary before calling the game() method
  public void newGame()
  {
        //Calls the loadMap method to load the map for the game
        loadMap();

        //Calls the createPlayers method to create the players needed for the game
        createPlayers();

        //Starts the powerup manager to make powerups
        pum.startPowerUps();

        //Makes the gamePanel the only visible panel
        gamePanel.setVisible(true);
        optionsPanel.setVisible(false);
        rulesPanel.setVisible(false);
        controlsPanel.setVisible(false);
        creditsPanel.setVisible(false);
        titlePanel.setVisible(false);

        //Resets some variables needed to play the game
        winPlayer = 5;
        deadPlayers = 0;

        //Calls the game() method to play the game
        game();      
  }

  //This method creates the players based on how much people are playing. It uses variables to determine the attributes of the player to be created
  public void createPlayers()
  {
     //Uses switch statement to create players depending on how much people are playing
    switch (numOfPlayers)
    {
        case 4: player[3] = new Player(550,50,health,maxBombs,bombRange,3,names[3]);
        case 3: player[2] = new Player(50,550,health,maxBombs,bombRange,2,names[2]);
        case 2: player[1] = new Player(550,550,health,maxBombs,bombRange,1,names[1]);
        case 1: player[0] = new Player(50,50,health,maxBombs,bombRange,0,names[0]);
    }
  }

  //This method makes only the options panel visible
  public void options()
  {
    //Makes only the optionsPanel visible
    gamePanel.setVisible(false);
    optionsPanel.setVisible(true);
    rulesPanel.setVisible(false);
    controlsPanel.setVisible(false);
    creditsPanel.setVisible(false);
    titlePanel.setVisible(false);
  }

  //This method makes only the rules panel visible
  public void rules()
  {
    //Makes only the rulesPanel visible
    gamePanel.setVisible(false);
    optionsPanel.setVisible(false);
    rulesPanel.setVisible(true);
    controlsPanel.setVisible(false);
    creditsPanel.setVisible(false);
    titlePanel.setVisible(false);
  }

  //This method makes only the controls panel visible
  public void controls()
  {
    //Makes only the controlsPanel visible
    gamePanel.setVisible(false);
    optionsPanel.setVisible(false);
    rulesPanel.setVisible(false);
    controlsPanel.setVisible(true);
    creditsPanel.setVisible(false);
    titlePanel.setVisible(false);
  }

  //This method makes only the credits panel visible
  public void credits()
  {
    //Makes only the creditsPanel visible
    gamePanel.setVisible(false);
    optionsPanel.setVisible(false);
    rulesPanel.setVisible(false);
    controlsPanel.setVisible(false);
    creditsPanel.setVisible(true);
    titlePanel.setVisible(false);
  }

  //This method is used by an outside class to change the variables in this class.It has 6 parameters and the variable names show what the variables are used for
  public static void initialize(int players, int bombLimit, int bombDistance, int lives, String mapName, String[] playerNames)
  {
      //Assigns the values that have been passed into the method to the variables
      numOfPlayers = players;
      maxBombs = bombLimit;
      bombRange = bombDistance + 1;
      health = lives;
      map = mapName;
      names = playerNames;
  }
}