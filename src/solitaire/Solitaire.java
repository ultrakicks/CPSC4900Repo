package solitaire;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.*;
import java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.*;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * A JPanel that plays Solitaire. This class contains a main method that will
 * open a new JFrame with this JPanel. The window contains a menu for user to
 * select the kind of solitaire being played. {@link Klondike} Solitaire is played
 * by default but the user also has the option the choose {@link FreeCell} or 
 * {@link Spider} Solitaire.
 * 
 * @author Warren Godone-Maresca
 */
public class Solitaire extends JPanel implements ActionListener, ItemListener {
	public enum games{
		MENU, ARGOS, AMERICAN_TOAD, ANNO_DOMINI, AZTEC_PYRAMIDS, KLONDIKE, SPIDER_EASY, SPIDER_HARD, YUKON
	}

	/**global clip for sound    		                                	*/
	public static Clip clip;

	/** The game we want to launch                                           */
	static Klondike game;

	/**Points to menu items in main menu.                                   */
	private JMenuItem argosShowItem, annoShowItem, americanShowItem,
					  aztecShowItem, volumeItem, argosStatsItem,
					  annoStatsItem, americanStatsItem, aztecStatsItem;
	
	/** Points to menu items in the game panel							*/
	private JMenuItem argosItem, annoItem, americanItem, aztecItem, mainMenuItem, klondikeItem, freeCellItem,
					  easySpiderItem, hardSpiderItem, yukonItem;

	/** Holds the button to display the rules.								*/
	private JMenuItem rulesItem;

	/** 
	 * Instantiates this with Klondike Solitaire by default.
	 */
	public Solitaire(){
		game = new Klondike(this);
	}

	/** 
	 * Draws the game.
	 */
	@Override
	protected void paintComponent(Graphics pane) {
		super.paintComponent(pane);
		game.paint(pane);
	}

	/** 
	 * Returns the menu bar for the game panel
	 */
	private JMenuBar makeGameMenuBar()
	{
		JMenuBar bar = new JMenuBar();	//Holds all of the buttons.
		JMenu selectMenu = new JMenu("Select");
		
		mainMenuItem = new JMenuItem("Main Menu"); 
		mainMenuItem.addActionListener(this);
		selectMenu.add(mainMenuItem);   //Goes back to the main menu.

        argosItem = new JMenuItem("Argos");
        argosItem.addActionListener(this);
        selectMenu.add(argosItem);   //Adds the Argos item.

        americanItem = new JMenuItem("American Toad");
        americanItem.addActionListener(this);
        selectMenu.add(americanItem);   //Adds the American Toad item.

        annoItem = new JMenuItem("Anno Domini");
        annoItem.addActionListener(this);
        selectMenu.add(annoItem);   //Adds the Anno Domini item.

        aztecItem = new JMenuItem("Aztec Pyramids");
        aztecItem.addActionListener(this);
        selectMenu.add(aztecItem);   //Adds the Aztec Pyramids item.

		bar.add(selectMenu);

		JMenu rulesMenu = new JMenu("Rules"); //To display the rules.
		rulesItem = new JMenuItem("Open");
		rulesItem.addActionListener(this);
		rulesMenu.add(rulesItem);
		bar.add(rulesMenu);

		return bar; //The bar has been created.
	}
	
	/** 
	 * Returns the menu bar for the game panel
	 */
	private JMenuBar makeMainMenuBar()
	{
		JMenuBar bar = new JMenuBar();	//Holds all of the buttons.
		JMenu settingsMenu = new JMenu("Settings");
		
		argosShowItem = new JCheckBoxMenuItem("Show Argos"); 
		argosShowItem.addActionListener(this);
		settingsMenu.add(argosShowItem);   //Toggles show of Argos
		if (MainMenu.argosBtn.isVisible())
		{
			argosShowItem.setSelected(true);
		}
		else
		{
			argosShowItem.setSelected(false);
		}
		argosShowItem.addItemListener(this);

		
		annoShowItem = new JCheckBoxMenuItem("Show Anno Domini"); 
		annoShowItem.addActionListener(this);
		settingsMenu.add(annoShowItem);   //Toggles show of Anno Domini
		if (MainMenu.annoBtn.isVisible())
		{
			annoShowItem.setSelected(true);
		}
		else
		{
			annoShowItem.setSelected(false);
		}
		annoShowItem.addItemListener(this);
		
		americanShowItem = new JCheckBoxMenuItem("Show American Toad"); 
		americanShowItem.addActionListener(this);
		settingsMenu.add(americanShowItem);   //Toggles show of American Toad
		if (MainMenu.americanBtn.isVisible())
		{
			americanShowItem.setSelected(true);
		}
		else
		{
			americanShowItem.setSelected(false);
		}
		americanShowItem.addItemListener(this);
		
		aztecShowItem = new JCheckBoxMenuItem("Show Aztec"); 
		aztecShowItem.addActionListener(this);
		settingsMenu.add(aztecShowItem);   //Toggles show of Aztec
		aztecShowItem.setSelected(true);
		if (MainMenu.aztecBtn.isVisible())
		{
			aztecShowItem.setSelected(true);
		}
		else
		{
			aztecShowItem.setSelected(false);
		}
		aztecShowItem.addItemListener(this);
		
		volumeItem = new JCheckBoxMenuItem("Volume Toggle"); 
		volumeItem.addActionListener(this);
		settingsMenu.add(volumeItem);
		volumeItem.setSelected(true);
		volumeItem.addItemListener(this);

		bar.add(settingsMenu);

		/*Statistics for each game */
		JMenu statsMenu = new JMenu("Statistics");
		
		argosStatsItem = new JMenuItem("Argos");
		argosStatsItem.addActionListener(this);
		statsMenu.add(argosStatsItem);
		
		annoStatsItem = new JMenuItem("Anno Domini");
		annoStatsItem.addActionListener(this);
		statsMenu.add(annoStatsItem);
		
		americanStatsItem = new JMenuItem("American Toad");
		americanStatsItem.addActionListener(this);
		statsMenu.add(americanStatsItem);
		
		aztecStatsItem = new JMenuItem("Aztec");
		aztecStatsItem.addActionListener(this);
		statsMenu.add(aztecStatsItem);
		
		bar.add(statsMenu);

		return bar; //The bar has been created.
	}

	/**
	 * checks if a check box has changed to show/hide buttons on the main menu
	 * @param e - Event generated when box is checked/unchecked
	 */
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getItemSelectable();

		if (source == argosShowItem)
		{
			if (argosShowItem.isSelected())
			{
				MainMenu.argosBtn.setVisible(true);
			} else {
				MainMenu.argosBtn.setVisible(false);
			}
		}

		if (source == annoShowItem)
		{
			if (annoShowItem.isSelected())
			{
				MainMenu.annoBtn.setVisible(true);
			} else {
				MainMenu.annoBtn.setVisible(false);
			}
		}

		if (source == americanShowItem)
		{
			if (americanShowItem.isSelected())
			{
				MainMenu.americanBtn.setVisible(true);
			} else {
				MainMenu.americanBtn.setVisible(false);
			}
		}

		if (source == aztecShowItem)
		{
			if (aztecShowItem.isSelected())
			{
				MainMenu.aztecBtn.setVisible(true);
			} else {
				MainMenu.aztecBtn.setVisible(false);
			}
		}

		if (source == volumeItem)
		{
			if (!volumeItem.isSelected())
				clip.stop();
			else {
				clip.setFramePosition(0);
				clip.loop(0);
			}
		}

	}

	/**
	 * Responds to menu events to set the form of Solitaire.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		//Open rules
		if(e.getSource() == rulesItem){
			try { //Opens the rules //TODO
				Desktop.getDesktop().open(new File("User Manual.pdf"));
			} catch (IOException ex){}
			
			return; //So we don't remove the listeners.
		}
		
		//Open statistics
		if(e.getSource() == argosStatsItem){
			Statistics.openStatistics("Argos");
			return; //So we don't remove the listeners.
		} else if(e.getSource() == annoStatsItem){
			Statistics.openStatistics("Anno Domini");
			return; //So we don't remove the listeners.
		} else if(e.getSource() == americanStatsItem){
			Statistics.openStatistics("American Toad");
			return; //So we don't remove the listeners.
		} else if(e.getSource() == aztecStatsItem){
			Statistics.openStatistics("Aztec Pyramid");
			return; //So we don't remove the listeners.
		}
		
		//The listeners need to be removed or else there will still be a
		//reference to the previous game object.
		this.removeMouseListener(game);
		this.removeMouseMotionListener(game);
		
		//Change game view
		if(e.getSource() == mainMenuItem){
			switchScreens(games.MENU);
        } else if(e.getSource() == argosItem){
            game = new Argos(this);
        } else if(e.getSource() == americanItem){
            game = new Klondike(this);
        } else if(e.getSource() == annoItem){
            game = new AnnoDomini(this); 
        } else if (e.getSource() == aztecItem) {
            game = new Pyramid(this);
		}

		repaint();
	}

	//Method that changes from the menu panel to the appropriate game panel and vice versa
	//The screen int keeps track of what type of switch we want to do
	// 0 is for going back to main menu, while 1 - 4 is for different games
	public static void switchScreens(games selectedGame)
	{
		switch(selectedGame)
		{
			case MENU:
				cardLayout.next(contentPane);
				frame.setJMenuBar(gamePanel.makeMainMenuBar());
				break;
			case ARGOS:
				game = new Argos(gamePanel);
				frame.setJMenuBar(gamePanel.makeGameMenuBar());
				cardLayout.next(contentPane);
				break;
			case AMERICAN_TOAD:
				game = new AmericanToad(gamePanel);
				frame.setJMenuBar(gamePanel.makeGameMenuBar());
				cardLayout.next(contentPane);
				break;
			case ANNO_DOMINI:
				game = new AnnoDomini(gamePanel);
				frame.setJMenuBar(gamePanel.makeGameMenuBar());
				cardLayout.next(contentPane);
				break;
			case AZTEC_PYRAMIDS:
				game = new Pyramid(gamePanel);
				frame.setJMenuBar(gamePanel.makeGameMenuBar());
				cardLayout.next(contentPane);
				break;
		}
	}

	public static void Sound()
	{
		try
		{
			File file = new File("Arcade_Yasper.wav");
			AudioInputStream ais = AudioSystem.getAudioInputStream(Solitaire.class.getResource("/resources/" + file));
			clip = AudioSystem.getClip();
			clip.open(ais);
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-24.0f); //lowers the volume by 24db
			clip.loop(-1);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Creation of several high-level GUI components
	//I made these public, so other methods could modify them
	public static CardLayout cardLayout = new CardLayout();
	public static JFrame frame = new JFrame();
	public static Container contentPane = frame.getContentPane();
	public static MainMenu mainMenuPanel = new MainMenu();
	public static Solitaire gamePanel = new Solitaire();
	
	public static void main(String[] args)
	{
		//More GUI creation
		contentPane.setLayout(cardLayout);
		contentPane.add(mainMenuPanel, "MainMenu");
		contentPane.add(gamePanel, "Game");

		frame.setTitle("Solitare Pack");
		frame.setLocation(10, 10);
		frame.setJMenuBar(gamePanel.makeMainMenuBar());
		frame.setSize(gamePanel.getPreferredSize());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Sound();
	}
}