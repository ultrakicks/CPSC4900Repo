package solitaire;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;

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

	private JLabel gameTitle, gameTotal, gameWins,
						gameAverageTime, gameBestTime, gameWinPercentage;

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
		gameTotal.setBorder(new EmptyBorder(new Insets(10, 0, 0, 0)));
		infoPanel.add(gameTotal);

		//Wins losses row
		/*
		Container winLossBox = new Container();
			BoxLayout winLossLayout = new BoxLayout(winLossBox, BoxLayout.X_AXIS);
			winLossBox.setLayout(winLossLayout);

			gameLosses = new JLabel("Losses: ");
			gameLosses.setAlignmentX(RIGHT_ALIGNMENT);
			gameLosses.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
			winLossBox.add(gameLosses);
		*/

		gameWins = new JLabel("Wins: ");
		gameWins.setAlignmentX(CENTER_ALIGNMENT);
		infoPanel.add(gameWins);

		//infoPanel.add(winLossBox);

		gameWinPercentage = new JLabel("Wins (%): ");
		gameWinPercentage.setAlignmentX(CENTER_ALIGNMENT);
		gameWinPercentage.setBorder(new EmptyBorder(new Insets(0, 0, 10, 0)));
		infoPanel.add(gameWinPercentage);

		gameAverageTime = new JLabel("Average Time: ");
		gameAverageTime.setAlignmentX(CENTER_ALIGNMENT);
		infoPanel.add(gameAverageTime);

		gameBestTime = new JLabel("Best Time: ");
		gameBestTime.setAlignmentX(CENTER_ALIGNMENT);
		infoPanel.add(gameBestTime);

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
			File file = new File(gameName + ".txt");
			Scanner infile = new Scanner(file);
			
			int totalGames = infile.nextInt();
			int wins = infile.nextInt();
			int totalTime = infile.nextInt();
			int bestTime = infile.nextInt();

			//Set values of each item
			gameTitle.setText(gameName);
			gameTotal.setText("Total Games: " + totalGames);
			gameWins.setText("Wins: " + wins);
			int timeTaken;
			if(totalGames > 0) {
				gameWinPercentage.setText("Wins (%): " + ((double) wins*100)/totalGames);
				timeTaken = totalTime/totalGames;
			}
			else {
				gameWinPercentage.setText("Wins (%): N/A");
				timeTaken = 0;
			}

			gameAverageTime.setText("Average Time: " + (timeTaken/60) + "m " + (timeTaken%60) + "s");
			gameBestTime.setText("Best Time: " + (bestTime/60) + "m " + (bestTime%60) + "s");

			infile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//Variable defining how long a game has lasted
	private static long timeStarted = 0;
	
	/**
	 * This set of methods adds to the current statistics of a certain game
	 * and then, if the statistics tab is open, reloads it automatically
	 */
	public static void startGame(String gameName) {
		int wins = 0, games = 0;
		long totalTime = 0, bestTime = 0;

		//If a game was started before consider all this time taken as time played.
		if(timeStarted > 0) {
			totalTime = (System.currentTimeMillis() - timeStarted)/1000;
		}

		timeStarted = System.currentTimeMillis();

		try
		{
			//Open file
			File file = new File(gameName + ".txt");
			Scanner infile = new Scanner(file);
			
			games = infile.nextInt();
			wins = infile.nextInt();
			totalTime += infile.nextInt();
			bestTime = infile.nextInt();
			infile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		games++;

		//Rewrite file
		writeFile(gameName, games, wins, totalTime, bestTime);

		//If statsPanel is open, reload stats
		if(statsPanel != null) {
			statsPanel.reloadStatistics(gameName);
		}
	}

	public static void winGame(String gameName) {
		int wins = 0, games = 0;
		long totalTime = 0, bestTime = 0;
		try
		{
			//Open file
			File file = new File(System.getProperty("user.dir"), gameName + ".txt");
			Scanner infile = new Scanner(file);
			
			games = infile.nextInt();
			wins = infile.nextInt();
			totalTime = infile.nextInt();
			bestTime = infile.nextInt();
			infile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		wins++;
		if(timeStarted > 0) {
			long timeTaken = (System.currentTimeMillis() - timeStarted)/1000;
			totalTime += timeTaken;
			if(timeTaken < bestTime || bestTime <= 0) {
				bestTime = timeTaken;
			}
		}
		timeStarted = 0;

		//Rewrite file
		writeFile(gameName, games, wins, totalTime, bestTime);

		//If statsPanel is open, reload stats
		if(statsPanel != null) {
			statsPanel.reloadStatistics(gameName);
		}
	}

	public static void leaveGame(String gameName) {
		int wins = 0, games = 0;
		long totalTime = 0, bestTime = 0;
		try
		{
			//Open file
			File file = new File(System.getProperty("user.dir"), gameName + ".txt");
			Scanner infile = new Scanner(file);
			
			games = infile.nextInt();
			wins = infile.nextInt();
			totalTime = infile.nextInt();
			bestTime = infile.nextInt();
			infile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(timeStarted > 0) {
			long timeTaken = (System.currentTimeMillis() - timeStarted)/1000;
			totalTime += timeTaken;
		}
		timeStarted = 0;

		//Rewrite file
		writeFile(gameName, games, wins, totalTime, bestTime);

		//If statsPanel is open, reload stats
		if(statsPanel != null) {
			statsPanel.reloadStatistics(gameName);
		}
	}

	public static void writeFile(String gameName, int games, int wins, long totalTime, long bestTime) {
		try
		{
			//Open File
			File file = new File(gameName + ".txt");
			FileWriter fileWriter = new FileWriter(file);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			//Write game data
			printWriter.println(games);
			printWriter.println(wins);
			printWriter.println(totalTime);
			printWriter.println(bestTime);
			//Clean up
			printWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
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
			frame.setSize(175, 208);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else {
			frame.setVisible(true);
			statsPanel.reloadStatistics(gameName);
		}
	}
}