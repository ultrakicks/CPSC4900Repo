package solitaire;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseEvent;

import card.Card;
import card.Foundation;
import card.StackOfCards;
import card.Tableau;
import dataStructures.Stack;

/**
 *
 * Argos is a solitaire game unlike most others
 * It consists of stock and waste tableaux and 52 play tableaux arranged in a grid, each with one card
 * The right-most column is reserved for kings
 * A tableau is completed when a card that is double its value (excess 13) is placed on it
 * The game is won when three rows of tableaux are completed.
 * @author Team1
 */
public class Argos extends Klondike {

	/**
	 * Instantiates the game and the panel.
	 * @param container The container for the game.
	 */
	public Argos(Container container){
		super(container);
		container.setBackground(new Color(0, 7, 255));

	}

	/**
	 * Initializes all of the stacks.
	 */
	@Override
	protected void init(){
		StackOfCards deck = new StackOfCards();
		deck.fillBySuit();
		deck.fillBySuit(); //Holds 104 cards.
		deck.shuffle();

		initTableaux(deck, new int[] {6, 6, 6, 6, 5, 5, 5, 5, 5, 5});
		initFoundations(0);
		initStockAndWaste(deck);

		initialized = true;
		container.repaint();
	}

	/**
	 * Just initializes the stock as the waste isn't used.
	 * Pre: the tableaux have been initialized.
	 */
	@Override
	protected void initStockAndWaste(StackOfCards deck){
		stock = new StackOfCards(tableaux[0].getX(), yCoord, cardWidth, 0, 0);
		stock.appendStack(deck); //The stock contains all of its cards.
		stock.peek().setHidden(true); //So that the stock is hidden.
	}

	/**
	 * Adds a card to each of the tableaux from the stock as long as the stock
	 * has cards and if the stock contains the given location.
	 * @return 	<code>true</code> if the stock contains the given location, else
	 * 			<code>false</code>.
	 */
	@Override
	protected boolean stockPressedAction(int x, int y){
		if(!stock.contains(x, y)){
			return false;
		}

		for(Tableau tableau : tableaux){
			if(stock.isEmpty()) //If the stock is empty, then there are no cards
				break;			//to move to a tableau.

			stock.peek().setHidden(false);
			super.animateTopCardOf(stock, tableau);
		}
		if(!stock.isEmpty()){
			stock.peek().setHidden(true);
		}
		container.repaint();
		return true;
	}

	/**
	 * If a tableau in {@link #tableaux} contains the given coordinates and the
	 * cards in {@link #inUse} is suitable to be appended, then
	 * the cards inUse will be appended to the tableau and inUse will be cleared.
	 *
	 * @param x		The x coordinate of a mouse click.
	 * @param y		The y coordinate.
	 * @return <code>true</code> if the action above was performed,
	 * 			else <code>false</code>
	 */
	protected boolean tableauxReleasedAction(int x, int y){
			return super.tableauxReleasedAction(x, y);
	}

	/**
	 * Performs the actions associated with the stack that is pressed.
	 */
	@Override
	public void mousePressed(MouseEvent e){
		int x = e.getX(), y = e.getY();

		if(inUse.isEmpty() && !stockPressedAction(x, y)){
			//Do the tableaux action if the stock action wasn't done.
			tableauxPressedAction(x, y);
		}
	}

	/**
	 * Appends the cards in use to first available foundation if the cards in use
	 * are in sequence from ace to king.
	 * Note: The mouse can be released anywhere, the parameters were inherited
	 *       but are unused here.
	 *
	 * @return 	<code>true</code> if the cards were in sequence and added to a
	 * 			foundation, otherwise <code>false</code>.
	 */
	@Override
	protected boolean foundationsReleasedAction(int x, int y){
		if(inUse.size() < 13 || inUse.peek().getValue() != 1){
			//Then the cards are not in sequence from ace to king.
			return false;
		} else {
			for(Foundation foundation : foundations){

				if(foundation.isEmpty()){ //If that foundation is empty,
					//Then we append each card in use to it.
					while(!inUse.isEmpty()){
						animateTopCardOf(inUse, foundation);
					}
					flipLastStack(); //Flips the top card of the last stack.
					return true; //The action was performed.
				}
			}
		}
		return false; //The action was not performed.
	}

	/**
	 * Determines if the user has won (if all foundations are nonempty).
	 */
	public  boolean hasWon(){
		for(Foundation foundation : foundations){
			if(foundation.isEmpty()){ //Then there exists an empty foundation.
				return false;
			}
		}
		return true; //Then no foundation is empty.
	}
}