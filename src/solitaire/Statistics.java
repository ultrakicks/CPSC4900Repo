package solitaire;

import java.awt.Container;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * A JPanel that displays Solitaire game statistics. This class contains a createStatistic
 * method that will open a new JFrame with this JPanel. The window contains a menu for user to
 * select differing games to get statistics on.
 * 
 * @author Connor McPherson
 */

public class Statistics extends JPanel implements ActionListener {

	/** The Solitaire game.													*/
	static JPanel infoPanel;

	/** Points to menu items in the game panel							*/
	private JMenuItem ArgosItem, AnnoItem, 
					  AmericanItem, AztecItem;
	
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
			reloadStatistics("Anno");
		} else if(e.getSource() == AmericanItem){
			reloadStatistics("American");
		} else if(e.getSource() == AztecItem){
			reloadStatistics("Aztec");
		}
		
		repaint();
	}

	private void reloadStatistics(String gameName) {
		frame.setTitle(gameName + "Statistics");

	}

	//Creation of several high-level GUI components
	//I made these public, so other methods could modify them
	public static JFrame frame = new JFrame();
	public static Container contentPane = frame.getContentPane();
	public static Statistics statsPanel = new Statistics();
	
	public static void openStatistics(String gameName)
	{
		//More GUI creation
		contentPane.add(statsPanel);

		frame.setTitle(gameName + "Statistics");
		frame.setLocation(10, 10);
		frame.setJMenuBar(statsPanel.makeMenuBar());
		frame.setSize(statsPanel.getPreferredSize());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}