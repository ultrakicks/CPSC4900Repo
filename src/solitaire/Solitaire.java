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
	/**global clip for sound    		                                	*/
	public static Clip clip;

	/** The Solitaire game.													*/
	static Klondike game;

	/**Points to menu items in main menu.                                   */
	private JMenuItem argosShowItem, annoShowItem, americanShowItem,
					  aztecShowItem, volumeItem, argosStatsItem,
					  annoStatsItem, americanStatsItem, aztecStatsItem;
	
	/** Points to menu items in the game panel							*/
	private JMenuItem mainMenuItem, klondikeItem, freeCellItem, 
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
		
		klondikeItem = new JMenuItem("Klondike"); 
		klondikeItem.addActionListener(this);
		selectMenu.add(klondikeItem);   //Adds the Klondike item.

		freeCellItem = new JMenuItem("Free Cell"); //And Free Cell item.
		freeCellItem.addActionListener(this);
		selectMenu.add(freeCellItem);

		JMenu spiderMenu = new JMenu("Spider"); //Holds the easy and hard items.
		easySpiderItem = new JMenuItem("Easy  "); //The easy spider item.
		easySpiderItem.addActionListener(this);
		spiderMenu.add(easySpiderItem);
		
		hardSpiderItem = new JMenuItem("Hard  "); //The hard spider item.
		hardSpiderItem.addActionListener(this);
		spiderMenu.add(hardSpiderItem);
		selectMenu.add(spiderMenu);
		
		yukonItem = new JMenuItem("Yukon"); //And Free Cell item.
		yukonItem.addActionListener(this);
		selectMenu.add(yukonItem);

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
		argosShowItem.setSelected(true);
		argosShowItem.addItemListener(this);

		
		annoShowItem = new JCheckBoxMenuItem("Show Anno Domini"); 
		annoShowItem.addActionListener(this);
		settingsMenu.add(annoShowItem);   //Toggles show of Anno Domini
		annoShowItem.setSelected(true);
		annoShowItem.addItemListener(this);
		
		americanShowItem = new JCheckBoxMenuItem("Show American Toad"); 
		americanShowItem.addActionListener(this);
		settingsMenu.add(americanShowItem);   //Toggles show of American Toad
		americanShowItem.setSelected(true);
		americanShowItem.addItemListener(this);
		
		aztecShowItem = new JCheckBoxMenuItem("Show Aztec"); 
		aztecShowItem.addActionListener(this);
		settingsMenu.add(aztecShowItem);   //Toggles show of Aztec
		aztecShowItem.setSelected(true);
		aztecShowItem.addItemListener(this);
		
		volumeItem = new JCheckBoxMenuItem("Volume Toggle"); 
		volumeItem.addActionListener(this);
		settingsMenu.add(volumeItem);   //Toggles show of Aztec
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
	 * @param e
	 */
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getItemSelectable();

		if (source == argosShowItem)
		{
			if (argosShowItem.isSelected() == false)
			{
				MainMenu.argosBtn.setVisible(false);
			} else {
				MainMenu.argosBtn.setVisible(true);
			}
		}

		if (source == annoShowItem)
		{
			if (annoShowItem.isSelected() == false)
			{
				MainMenu.annoBtn.setVisible(false);
			} else {
				MainMenu.annoBtn.setVisible(true);
			}
		}

		if (source == americanShowItem)
		{
			if (americanShowItem.isSelected() == false)
			{
				MainMenu.americanBtn.setVisible(false);
			} else {
				MainMenu.americanBtn.setVisible(true);
			}
		}

		if (source == aztecShowItem)
		{
			if (aztecShowItem.isSelected() == false)
			{
				MainMenu.aztecBtn.setVisible(false);
			} else {
				MainMenu.aztecBtn.setVisible(true);
			}
		}

		if (source == volumeItem)
		{
			if (volumeItem.isSelected() == false)
			{
				clip.stop();
			} else {
				clip.start();
			}
		}

	}

	/**
	 * Responds to menu events to set the form of Solitaire.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == rulesItem){
			try { //Opens the rules //TODO
				Desktop.getDesktop().open(new File("User Manual.pdf"));
			} catch (IOException ex){}
			
			return; //So we don't remove the listeners.
		}
		
		//The listeners need to be removed or else there will still be a
		//reference to the previous game object.
		this.removeMouseListener(game);
		this.removeMouseMotionListener(game);
		
		if(e.getSource() == mainMenuItem){
			switchScreens(0);
		} else if(e.getSource() == klondikeItem){
			game = new Klondike(this);
		} else if(e.getSource() == freeCellItem){
			game = new FreeCell(this);
		} else if(e.getSource() == easySpiderItem){
			game = new Spider(this, true);
		} else if(e.getSource() == hardSpiderItem){
			game = new Spider(this, false);
		} else if (e.getSource() == yukonItem){
			game = new Yukon(this);
		}
		
		repaint();
	}

	//Method that changes from the menu panel to the appropriate game panel and vice versa
	//The screen int keeps track of what type of switch we want to do
	// 0 is for going back to main menu, while 1 - 4 is for different games
	public static void switchScreens(int screen)
	{
		switch(screen)
		{
			case 0:
				cardLayout.next(contentPane);
				frame.setJMenuBar(gamePanel.makeMainMenuBar());
				break;
			case 1:
				game = new Yukon(contentPane);
				frame.setJMenuBar(gamePanel.makeGameMenuBar());
				cardLayout.next(contentPane);
				break;
			case 2:
				game = new Klondike(contentPane);
				frame.setJMenuBar(gamePanel.makeGameMenuBar());
				cardLayout.next(contentPane);
				break;
			case 3:
				game = new Spider(contentPane, true);
				frame.setJMenuBar(gamePanel.makeGameMenuBar());
				cardLayout.next(contentPane);
				break;
			case 4:
				game = new Spider(contentPane, false);
				frame.setJMenuBar(gamePanel.makeGameMenuBar());
				cardLayout.next(contentPane);
				break;
		}
	}

	public static void Sound()
	{
		try
		{
			File file = new File("NoisestormCrabRave.wav");
			AudioInputStream ais = AudioSystem.getAudioInputStream(Solitaire.class.getResource("/resources/" + file));
			clip = AudioSystem.getClip();
			clip.open(ais);

			clip.start();
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