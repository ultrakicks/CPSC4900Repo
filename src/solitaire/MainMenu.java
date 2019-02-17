package solitaire;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class MainMenu extends JPanel 
{
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
        
        //Change these booleans with settings to exclude buttons
        //TODO hook these booleans up with the checkboxes in the settings menu
        boolean argosShow = true;
        boolean annoShow = true;
        boolean americanShow = true;
        boolean aztecShow = true;
        boolean volumeToggle = false;

        //Checks to see if buttons can be shown on menu
        if (argosShow == true)
        {
        	//Button can be shown, so create it
        	JButton argosBtn = new JButton("Argos");
        	//Add action listener to button to have action happen on click
        	argosBtn.addActionListener(new ActionListener() 
        	{
        		  public void actionPerformed(ActionEvent event) 
        		  {
        			  //Switches from menu screen to game screen
        			  //Note that 1 - 4 are the different games, while 0 goes back to main menu
        			  Solitaire.switchScreens(1);
        		  }
        	});
        	
        	add(argosBtn, gbc);
        }
        //Repeat for all games
        if (annoShow == true)
        {
        	JButton annoBtn = new JButton("Anno Domini");
        	annoBtn.addActionListener(new ActionListener() 
        	{
        		  public void actionPerformed(ActionEvent event) 
        		  {
        			  Solitaire.switchScreens(2);
        		  }
        	});
        	
        	add(annoBtn, gbc);
        }
        
        if (americanShow == true)
        {
        	JButton americanBtn = new JButton("American Toad");
        	americanBtn.addActionListener(new ActionListener() 
        	{
        		  public void actionPerformed(ActionEvent event) 
        		  {
        			  Solitaire.switchScreens(3);
        		  }
        	});
        	
        	add(americanBtn, gbc);
        }
        
        if (aztecShow == true)
        {
        	JButton aztecBtn = new JButton("Aztec Pyramids");
        	aztecBtn.addActionListener(new ActionListener() 
        	{
        		  public void actionPerformed(ActionEvent event) 
        		  {
        			  Solitaire.switchScreens(4);
        		  }
        	});
        	
        	add(aztecBtn, gbc);
        }
    }
}