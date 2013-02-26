//By Sujen Sathiyanathan
//Imports Classes
import java.awt.event.*;
import javax.swing.*;

//Menu Class
public class Menu extends JMenuBar implements ActionListener,ItemListener
{
    //Objects and variables
    JMenu game = new JMenu("Game");
    JMenu view = new JMenu("View");
    JMenu sound = new JMenu("Sound");
    JMenuItem newGame = new JMenuItem("New Game");
    JMenuItem options = new JMenuItem("Options");
    JMenuItem rules = new JMenuItem("Rules");
    JMenuItem controls = new JMenuItem("Controls");
    JMenuItem credits = new JMenuItem("Credits");
    JRadioButtonMenuItem battleMusicButton = new JRadioButtonMenuItem("Battle Theme");
    JRadioButtonMenuItem battle64MusicButton = new JRadioButtonMenuItem("Battle Theme 64");
    JRadioButtonMenuItem deadlyMusicButton = new JRadioButtonMenuItem("Deadly Battle Theme");
    JRadioButtonMenuItem minibossMusicButton = new JRadioButtonMenuItem("Miniboss Theme");
    JRadioButtonMenuItem bossMusicButton = new JRadioButtonMenuItem("Boss Theme");
    JRadioButtonMenuItem muteButton = new JRadioButtonMenuItem("Mute");
    JMenu music = new JMenu("Music");
    ButtonGroup musicButtonGroup = new ButtonGroup ();
    ClassLoader ldr = this.getClass().getClassLoader();
    java.applet.AudioClip battleTheme = JApplet.newAudioClip(ldr.getResource("Battle Theme.wav"));
    java.applet.AudioClip deadlyTheme = JApplet.newAudioClip(ldr.getResource("Deadly Battle.wav"));
    java.applet.AudioClip minibossTheme = JApplet.newAudioClip(ldr.getResource("Miniboss Theme.wav"));
    java.applet.AudioClip battle64Theme = JApplet.newAudioClip(ldr.getResource("Battle Theme 64.wav"));
    java.applet.AudioClip bossTheme = JApplet.newAudioClip(ldr.getResource("Boss Theme.wav"));
    java.applet.AudioClip audio = battleTheme;
    boolean mute = false;

    //The constructor which sets up the menu by adding the menu items into the right places
    public Menu()
    {
        //Adds event listeners to the menu items
        newGame.addActionListener(this);
        options.addActionListener(this);
        rules.addActionListener(this);
        controls.addActionListener(this);
        credits.addActionListener(this);
        battleMusicButton.addItemListener(this);
        battle64MusicButton.addItemListener(this);
        deadlyMusicButton.addItemListener(this);
        minibossMusicButton.addItemListener(this);
        bossMusicButton.addItemListener(this);
        muteButton.addItemListener(this);
        
        //Adds the radio buttons to the group
        musicButtonGroup.add(battleMusicButton);
        musicButtonGroup.add(battle64MusicButton);
        musicButtonGroup.add(deadlyMusicButton);
        musicButtonGroup.add(minibossMusicButton);
        musicButtonGroup.add(bossMusicButton);

        //Makes it so that the first song is selected
        battleMusicButton.setSelected(true);

        //Adds the menuitems into their respective menus
        game.add(newGame);
        game.add(options);
        view.add(rules);
        view.add(controls);
        view.add(credits);
        music.add(battleMusicButton);
        music.add(battle64MusicButton);
        music.add(deadlyMusicButton);
        music.add(minibossMusicButton);
        music.add(bossMusicButton);
        sound.add(music);
        sound.add(muteButton);


        //Adds the menus to the menubar
        add(game);
        add(view);
        add(sound);

        //Plays the music
        audio.play();
    }

    //Method for action events for when a menu item is clicked
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == newGame)
        {
            //Makes mode equal to 5 and makes game true so that the actual game in Bomberman class will run
            Bomberman.mode = 5;
            Bomberman.game = true;
        }
        else if(e.getSource() == options)
        {
            Bomberman.mode = 1;
        }
        else if(e.getSource() == rules)
        {
            Bomberman.mode = 2;
        }
        else if(e.getSource() == controls)
        {
            Bomberman.mode = 3;
        }
        else if(e.getSource() == credits)
        {
            Bomberman.mode = 4;
        }
    }

    //Method for item events for when a radio button is selected
    public void itemStateChanged(ItemEvent e)
    {
        //Depending on which song is selected by the user, the program assigns audio the object for that song and plays it.
        if(e.getItemSelectable() == battleMusicButton && !mute)
        {
            audio.stop();
            audio = battleTheme;
            audio.play();
        }
        else if(e.getItemSelectable() == deadlyMusicButton && !mute)
        {
            audio.stop();
            audio = deadlyTheme;
            audio.play();
        }
        else if(e.getItemSelectable() == battle64MusicButton && !mute)
        {
            audio.stop();
            audio = battle64Theme;
            audio.play();
        }
        else if(e.getItemSelectable() == minibossMusicButton && !mute)
        {
            audio.stop();
            audio = minibossTheme;
            audio.play();
        }
        else if(e.getItemSelectable() == bossMusicButton && !mute)
        {
            audio.stop();
            audio = bossTheme;
            audio.play();
        }

        //If the user selects mute
        if(e.getItemSelectable() == muteButton)
        {
            //If mute is already true, then the program plays the audio and enables music selection
            if(mute)
            {
                //Makes mute equal to false and plays the audio
                mute = false;
                audio.play();

                //Enables the radio buttons for selecting music
                battleMusicButton.setEnabled(true);
                battle64MusicButton.setEnabled(true);
                deadlyMusicButton.setEnabled(true);
                minibossMusicButton.setEnabled(true);
                bossMusicButton.setEnabled(true);
            }
            else
            {
                //Makes mute equal to true and stops the audio
                mute = true;
                audio.stop();

                //Disables the radio buttons for selecting music
                battleMusicButton.setEnabled(false);
                battle64MusicButton.setEnabled(false);
                deadlyMusicButton.setEnabled(false);
                minibossMusicButton.setEnabled(false);
                bossMusicButton.setEnabled(false);
            }
        }
    }
}
