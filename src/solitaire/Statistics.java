package solitaire;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.net.URL;

import java.awt.Container;
import java.awt.Insets;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 * A JPanel that displays Solitaire game statistics. This class contains a createStatistic
 * method that will open a new JFrame with this JPanel. The window contains a menu for user to
 * select differing games to get statistics on.
 * 
 * @author Connor McPherson
 */

public class Statistics extends JPanel implements ActionListener {

	/** Points to menu items in the game panel							*/
	private JMenuItem ArgosItem, AnnoItem, 
					  AmericanItem, AztecItem;

	private JLabel gameTitle, gameTotal, gameWins, gameLosses;

	private Statistics(String gameName) {
		//Create panel objects
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

		gameTitle = new JLabel(gameName);
		gameTitle.setFont(new Font(Font.SERIF, Font.PLAIN, 24));
		gameTitle.setAlignmentX(CENTER_ALIGNMENT);
		infoPanel.add(gameTitle);

		gameTotal = new JLabel("Total Games: ");
		gameTotal.setAlignmentX(CENTER_ALIGNMENT);
		infoPanel.add(gameTotal);

		//Wins losses row
		Container winLossBox = new Container();
			BoxLayout winLossLayout = new BoxLayout(winLossBox, BoxLayout.X_AXIS);
			winLossBox.setLayout(winLossLayout);

			gameWins = new JLabel("Wins: ");
			gameWins.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
			gameWins.setAlignmentX(LEFT_ALIGNMENT);
			winLossBox.add(gameWins);

			gameLosses = new JLabel("Losses: ");
			gameLosses.setAlignmentX(RIGHT_ALIGNMENT);
			gameLosses.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
			winLossBox.add(gameLosses);
		infoPanel.add(winLossBox);

		add(infoPanel);

		//Load statistics
		reloadStatistics(gameName);
		
		repaint();
	}
	
	/** 
	 * Returns the menu bar for the game panel
	 */
	private JMenuBar makeMenuBar()
	{
		JMenuBar bar = new JMenuBar();	//Holds all of the buttons.
		JMenu selectMenu = new JMenu("Select");
		
		ArgosItem = new JMenuItem("Argos"); 
		ArgosItem.addActionListener(this);
		selectMenu.add(ArgosItem);

		AnnoItem = new JMenuItem("Anno Domini");
		AnnoItem.addActionListener(this);
		selectMenu.add(AnnoItem);

		AmericanItem = new JMenuItem("American Toad");
		AmericanItem.addActionListener(this);
		selectMenu.add(AmericanItem);
		
		AztecItem = new JMenuItem("Aztec Pyramid");
		AztecItem.addActionListener(this);
		selectMenu.add(AztecItem);

		bar.add(selectMenu);

		return bar; //The bar has been created.
	}

	/**
	 * Responds to menu events to reload statistics of different games.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		//Reload statistics
		if(e.getSource() == ArgosItem){
			reloadStatistics("Argos");
		} else if(e.getSource() == AnnoItem){
			reloadStatistics("Anno Domini");
		} else if(e.getSource() == AmericanItem){
			reloadStatistics("American Toad");
		} else if(e.getSource() == AztecItem){
			reloadStatistics("Aztec Pyramid");
		}
		
		repaint();
	}

	/**
	 * Replaces current statistics set with new values by reloading
	 * the file of the specified game
	 */
	private void reloadStatistics(String gameName) {
		frame.setTitle(gameName + "Statistics");
		//Open files
		try
		{
			File file = new File("bin/data/" + gameName + ".txt");
			Scanner infile = new Scanner(file);
			
			int totalGames = infile.nextInt();
			int wins = infile.nextInt();

			//Set values of each item
			gameTitle.setText(gameName);
			gameTotal.setText("Total Games: " + totalGames);
			gameWins.setText("Wins: " + wins);
			gameLosses.setText("Losses: " + (totalGames-wins));

			infile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This set of methods adds to the current statistics of a certain game
	 * and then, if the statistics tab is open, reloads it automatically
	 */
	public static void addGame(String gameName) {
		int wins = 0, games = 0;
		try
		{
			//Open file
			File file = new File("bin/data/" + gameName + ".txt");
			Scanner infile = new Scanner(file);
			
			games = infile.nextInt();
			wins = infile.nextInt();
			infile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		games++;

		//Rewrite file
		try
		{
			//Open File
			File file = new File("bin/data/" + gameName + ".txt");
			FileWriter fileWriter = new FileWriter(file);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			//Write wins and losses
			printWriter.print(games);
			printWriter.println(wins);
			//Clean up
			printWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//If statsPanel is open, reload stats
		if(statsPanel != null) {
			statsPanel.reloadStatistics(gameName);
		}
	}

	public static void winGame(String gameName) {
		int wins = 0, games = 0;
		try
		{
			//Open file
			File file = new File("bin/data/" + gameName + ".txt");
			Scanner infile = new Scanner(file);
			
			games = infile.nextInt();
			wins = infile.nextInt();
			infile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		wins++;

		//Rewrite file
		try
		{
			//Open File
			File file = new File("bin/data/" + gameName + ".txt");
			FileWriter fileWriter = new FileWriter(file);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			//Write wins and losses
			printWriter.print(games);
			printWriter.println(wins);
			//Clean up
			printWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//If statsPanel is open, reload stats
		if(statsPanel != null) {
			statsPanel.reloadStatistics(gameName);
		}
	}

	//Creation of several high-level GUI components
	//I made these public, so other methods could modify them
	public static JFrame frame = new JFrame();
	public static Container contentPane = frame.getContentPane();
	public static Statistics statsPanel;
	
	public static void openStatistics(String gameName)
	{
		if(statsPanel == null) {
			statsPanel = new Statistics(gameName);
			//More GUI creation
			contentPane.add(statsPanel);
	
			frame.setTitle(gameName + " Statistics");
			frame.setLocation(10, 10);
			frame.setJMenuBar(statsPanel.makeMenuBar());
			//Set size to double the height of original for java swing
			//problems I don't understand on my machine... Oh dear.
			frame.setSize(170, 180);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else {
			frame.setVisible(true);
			statsPanel.reloadStatistics(gameName);
		}
	}
}