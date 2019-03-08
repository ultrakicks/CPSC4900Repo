package solitaire;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class MainMenu extends JPanel 
{

	/**
	 * buttons have been made global
	 */
	public static JButton argosBtn = new JButton("Argos");
	public static JButton annoBtn = new JButton("Anno Domini");
	public static JButton americanBtn = new JButton("American Toad");
	public static JButton aztecBtn = new JButton("Aztec Pyramids");
	public static JButton klondikeBtn = new JButton("Klondike");
	public static JButton spiderEasyBtn = new JButton("Spider (Easy)");
	public static JButton spiderHardBtn = new JButton("Spider (Hard)");
	public static JButton yukonBtn = new JButton("Yukon");

    public MainMenu() 
    {
    	//Setting up responsive grid layout
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20,20,20,20);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        
        //Adds title
        add(new JLabel("<html><h1><strong><i>Solitaire Pack</i></strong></h1><hr></html>"), gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

		//Add action listener to button to have action happen on click
		/**
		 * button listener for argos
		 */
		argosBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				//Switches from menu screen to game screen
				//Note that 1 - 4 are the different games, while 0 goes back to main menu
				Solitaire.switchScreens(Solitaire.games.ARGOS);
			}
		});
		add(argosBtn, gbc);

        //Repeat for all games
		/**
		 * button listener for anno domini
		 */
		annoBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Solitaire.switchScreens(Solitaire.games.ANNO_DOMINI);
			}
		});

		add(annoBtn, gbc);

		/**
		 * button listener for american toad
		 */
		americanBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Solitaire.switchScreens(Solitaire.games.AMERICAN_TOAD);
			}
		});

		add(americanBtn, gbc);

		/**
	 * button listener for aztec
	 */
		aztecBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Solitaire.switchScreens(Solitaire.games.AZTEC_PYRAMIDS);
			}
		});
		add(aztecBtn, gbc);

		/**
		 * button listener for aztec
		 */
		klondikeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Solitaire.switchScreens(Solitaire.games.KLONDIKE);
			}
		});
		add(klondikeBtn, gbc);

		/**
		 * button listener for aztec
		 */
		spiderEasyBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Solitaire.switchScreens(Solitaire.games.SPIDER_EASY);
			}
		});
		add(spiderEasyBtn, gbc);

		/**
		 * button listener for aztec
		 */
		spiderHardBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Solitaire.switchScreens(Solitaire.games.SPIDER_HARD);
			}
		});
		add(spiderHardBtn, gbc);

		/**
		 * button listener for aztec
		 */
		yukonBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Solitaire.switchScreens(Solitaire.games.YUKON);
			}
		});
		add(yukonBtn, gbc);
    }
}