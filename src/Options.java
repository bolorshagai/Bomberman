//By Sujen Sathiyanathan
//Imports Classes
import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//Options Class
public class Options extends JLayeredPane implements ActionListener, ChangeListener, ItemListener
{
    JLabel bgImage = new JLabel("options");
    JLabel[] playerIcon = new JLabel[4];
    JLabel gridBackground;
    JLabel[][] grid = new JLabel[11][11];
    ImageIcon floor = new ImageIcon(getClass().getClassLoader().getResource("floorSmall.png"));
    ImageIcon block = new ImageIcon(getClass().getClassLoader().getResource("blockSmall.png"));
    ImageIcon brick = new ImageIcon(getClass().getClassLoader().getResource("brickSmall.png"));
    JSpinner numOfPlayersSpinner = new JSpinner(new SpinnerNumberModel(2,2,4,1));
    JSpinner bombLimitSpinner = new JSpinner(new SpinnerNumberModel(1,1,10,1));
    JSpinner bombRangeSpinner = new JSpinner(new SpinnerNumberModel(1,1,10,1));
    JSpinner healthSpinner = new JSpinner(new SpinnerNumberModel(1,1,3,1));
    JButton setOptions = new JButton("Set Options");
    JTextField p1Name = new JTextField("Player 1");
    JTextField p2Name = new JTextField("Player 2");
    JTextField p3Name = new JTextField("Player 3");
    JTextField p4Name = new JTextField("Player 4");
    String[] maps = {"Classic", "Brick Grid","X"};
    String[] names = new String[4];
    JComboBox mapChooser = new JComboBox(maps);
    BufferedReader br;
    StringTokenizer st;
    String s = "";
    int bombRange = 0;
    int maxBombs = 0;
    int numOfPlayers = 0;
    int health = 0;

    public Options()
    {
        //Initializes the JLabel[][] array
        for(int i = 0; i < 11; i ++)
        {
            for(int j = 0; j < 11; j++)
            {
                grid[i][j] = new JLabel();
            }
        }
        //Loading Images
        playerIcon[0] = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("wb-s1.png")));
        playerIcon[1] = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("bb-s1.png")));
        playerIcon[2] = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("gb-s1.png")));
        playerIcon[3] = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("rb-s1.png")));
        bgImage = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("optionsBackground.png")));
        gridBackground = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("gridBackgroundSmall.png")));

        //Draws a Map
        drawMap();

        //sets the layout
        this.setLayout(null);

        //Adds all components to panel. The background image will be on the first layer. The rest wil be on the second layer which is on top of the image
        this.add(bgImage,DEFAULT_LAYER);
        this.add(setOptions,PALETTE_LAYER);
        this.add(numOfPlayersSpinner,PALETTE_LAYER);
        this.add(bombRangeSpinner,PALETTE_LAYER);
        this.add(bombLimitSpinner,PALETTE_LAYER);
        this.add(healthSpinner,PALETTE_LAYER);
        this.add(playerIcon[0],PALETTE_LAYER);
        this.add(playerIcon[1],PALETTE_LAYER);
        this.add(playerIcon[2],PALETTE_LAYER);
        this.add(playerIcon[3],PALETTE_LAYER);
        this.add(p1Name,PALETTE_LAYER);
        this.add(p2Name,PALETTE_LAYER);
        this.add(p3Name,PALETTE_LAYER);
        this.add(p4Name,PALETTE_LAYER);
        this.add(gridBackground,PALETTE_LAYER);
        this.add(mapChooser,PALETTE_LAYER);

        //Adds the event listeners
        numOfPlayersSpinner.addChangeListener(this);
        mapChooser.addItemListener(this);
        setOptions.addActionListener(this);

        //Makes certain components invisible at first
        playerIcon[3].setVisible(false);
        p4Name.setVisible(false);
        playerIcon[2].setVisible(false);
        p3Name.setVisible(false);

        //Sets location for components
        bgImage.setBounds(0,0,850,678);
        playerIcon[0].setBounds(10,200,50,50);
        playerIcon[1].setBounds(10,300,50,50);
        playerIcon[2].setBounds(10,400,50,50);
        playerIcon[3].setBounds(10,500,50,50);
        p1Name.setBounds(70,200,100,20);
        p2Name.setBounds(70,300,100,20);
        p3Name.setBounds(70,400,100,20);
        p4Name.setBounds(70,500,100,20);
        numOfPlayersSpinner.setBounds(185,118,30,20);
        bombLimitSpinner.setBounds(402,118,35,20);
        bombRangeSpinner.setBounds(635,118,35,20);
        healthSpinner.setBounds(780,118,30,20);
        setOptions.setBounds(720,600,110,20);
        gridBackground.setBounds(430,190,325,325);
        mapChooser.setBounds(310,190,100,20);

        //Adds the grid[][] JLabel array to the panel and in the right location
        for(int i = 0; i < 11; i++)
        {
            for(int j = 0; j < 11; j++)
            {
                //Adds the JLabels that are part of the map into the modal layer which is the third layer

                this.add(grid[i][j],MODAL_LAYER);
                grid[i][j].setBounds(455 + (j * 25), 215 + (i * 25),25,25);
            }
        }
    }

    public void drawMap()
    {
        //Reads the map data from file and uses the values to determine what image to give the elements in the JLabel array
        try
        {
          br = new BufferedReader(new FileReader((String)mapChooser.getSelectedItem() + ".txt"));

          for(int i = 0; i < 11; i++)
          {
            st = new StringTokenizer(br.readLine()," ");
            for(int j = 0; j < 11; j++)
            {
              s = st.nextToken();
              if (Integer.parseInt(s) == 0)
              {
                  grid[i][j].setIcon(floor);
              }
              else if (Integer.parseInt(s) == 1)
              {
                  grid[i][j].setIcon(block);
              }
              else if (Integer.parseInt(s) == 2)
              {
                  grid[i][j].setIcon(brick);
              }
            }
          }
        }
        catch(Exception e)
        {
            System.out.println("Files not found");
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        numOfPlayers = (Integer)numOfPlayersSpinner.getValue();
        maxBombs = (Integer)bombLimitSpinner.getValue();
        bombRange = (Integer)bombRangeSpinner.getValue();
        health = (Integer)healthSpinner.getValue();
        names[0] = p1Name.getText();
        names[1] = p2Name.getText();
        names[2] = p3Name.getText();
        names[3] = p4Name.getText();
        Bomberman.initialize(numOfPlayers,maxBombs,bombRange,health,(String)mapChooser.getSelectedItem(),names);

        //Makes a popup window saying that the options have been set
        JOptionPane.showMessageDialog(Bomberman.window,"Your options have been set and will be applied to the next game.","Success!",JOptionPane.PLAIN_MESSAGE);

    }

    public void stateChanged(ChangeEvent e)
    {
        //If the numOfPlayers value is changed,
        if(e.getSource() == numOfPlayersSpinner)
        {
            //Initially makes all the player components visible
            playerIcon[3].setVisible(false);
            p4Name.setVisible(false);
            playerIcon[2].setVisible(false);
            p3Name.setVisible(false);

            //gets the value  and casts it to Integer type as the returned value is type Object
            int num = (Integer)numOfPlayersSpinner.getValue();

            //based on num, certain player components will be displayed
            switch(num)
            {
                case 4: playerIcon[3].setVisible(true);
                        p4Name.setVisible(true);
                case 3: playerIcon[2].setVisible(true);
                        p3Name.setVisible(true);
            }
        }
    }

    public void itemStateChanged(ItemEvent e)
    {
        if ((e.getItemSelectable () == mapChooser)&&(e.getStateChange() == ItemEvent.SELECTED))
        {
            drawMap();
        }
    }
}