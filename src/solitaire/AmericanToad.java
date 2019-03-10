package solitaire;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import card.Card;
import card.Foundation;
import card.HoldingCell;
import card.StackOfCards;
import card.Tableau;
import dataStructures.Stack;

/**
 * @ American Toad
 * Description: AT is a solitaire game using two decks of playing cards. 
 * This game is similar to Canfield except that the Tableau builds down in suit, 
 * and a partial Tableau Stack cannot be moved (only the top card or entire stack 
 * can be moved). The object of the game is to move all cards to the Foundations. 
 * 
 * Layout: American Toad has eight Tableau Stacks. Each Tableau Stack contains 
 * one card and builds down in suit wrapping from Ace to King. The game includes
 * one Reserve Pile with twenty cards that can be played onto the Tableau or Foundations. 
 * There is a deck usually at the bottom right that turns up one card at a time.
 *
 * Rules: One card is dealt onto the first Foundation. This rank will be used 
 * as a base for the other Foundations. The Foundations build up in suit, wrapping 
 * from King to Ace as necessary. Cards in the Tableau can be moved to a Foundation 
 * or onto another Tableau Stack. The Tableau builds down in suit wrapping 
 * from Ace to King. Empty spaces are filled automatically from the Reserve. 
 * Once the Reserve is empty, spaces in the Tableau can be filled with a card 
 * from the Deck, but NOT from another Tableau Pile. When moving Tableau Piles, 
 * you must either move the whole pile or only the top card.
 */
public class AmericanToad extends Klondike{
	
	//Constuctor Initialization
	public AmericanToad(Container container) {
		super(container);
	}
	
	//Initilize all of the stacks
	@Override
	protected void init() {
		StackOfCards deck = new StackOfCards();
		StackOfCards deck2 = new StackOfCards();
		//fill deck & deck2 twice to hold 104 cards & shuffle
		deck.fillBySuit(); deck.fillBySuit(); deck.shuffle();
		deck2.fillBySuit(); deck2.shuffle();
		//Build the game board
		initTableaux(deck,new int[]{1,1,1,1,1,1,1,1,1,1});
		initFoundations(8);
		initStockAndWaste(deck);

		initialized = true;
		container.repaint();
	}
	
	
	//Lay the Tableus on the gameboard(Should be 8 columns aligned with the 8 foundations).
		protected void initTableaux(StackOfCards source) {
			tableaux = new Tableau[9];
		}
			

	/**
	 * Just initializes the stock as the waste isn't used.
	 * Pre: the tableaux have been initialized.
	 */
	@Override
	protected void initStockAndWaste(StackOfCards deck){
		//Stock Deck
		stock = new StackOfCards(tableaux[0].getX(), yCoord, cardWidth, 0, 0);
		stock.appendStack(deck); //The stock contains all of its cards.
		stock.peek().setHidden(true); //So that the stock is hidden.
		//Waste Deck
		waste = new StackOfCards(tableaux[1].getX(), yCoord, cardWidth, 0, 0);
		
		
	}
	
	/**
	 * Performs the action associated with stock when clicked. If the stock is not
	 * empty, 3 cards will be flipped from the stock to the waste, otherwise, the
	 * waste will be emptied onto the stock. In this method, the number of moves 
	 * is incremented if the action is performed.
	 * <p>
	 * The action will only be performed if the given mouse click coordinates
	 * are contained in the stock.
	 * @param x		The x coordinate of a mouse click.
	 * @param y		The y coordinate.
	 * @return <code>true</code> if the action was performed, 
	 * 			else <code>false</code>
	 */
	protected boolean stockPressedAction(int x, int y){
		if(stock.contains(x, y)){
			//If the stock was clicked:
			waste.push(stock.pop());	waste.push(stock.pop());	
			waste.push(stock.pop());	//Moves the top 3 cards from stock to waste.
			waste.peek().setHidden(false);//And show it.

			if(!stock.isEmpty())
				stock.peek().setHidden(true);//Hides the new top card of the stack.
			moves++; //This counts as a move.
			container.repaint();
			return true; //The action was performed.

		} else if(stock.shapeOfNextCard().contains(x, y)){
			//else if the mouse clicked the empty stock's area:
			//Turn over all cards from the waste to the stock,
			stock.appendStack(waste.reverseCopy());
			waste.clear(); //and clear the waste.

			if(!stock.isEmpty()){
				stock.peek().setHidden(true); //So that stock is turned form
				moves++;					  //the user.
			}
			container.repaint();
			return true; //The action was performed.
		}
		return false; //The action was not performed.
	}
	
	
	
	
	
}
