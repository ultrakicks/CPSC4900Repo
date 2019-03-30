package solitaire;

import card.Card;
import card.Foundation;
import card.StackOfCards;
import card.Tableau;
import dataStructures.Queue;
import dataStructures.Stack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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

public class AmericanToad extends Klondike {
	protected StackOfCards stock2;
	protected Card baseCard;
	
	/** Do nothing constructor.												*/
	public AmericanToad(){}

	/**
	 * Instantiates the game with a {@link Container}.
	 * @param container The Container (such as window or applet) in which the
	 * 					game will be played.
	 */
	public AmericanToad(Container container){
		this.container = container;
		container.addMouseListener(this); 		//To respond to clicks
		container.addMouseMotionListener(this); //and dragging.
		container.setBackground(new Color(50, 35, 150)); //A green color.
		container.setSize(790, 720);
		container.setPreferredSize(container.getSize());

		setCoord(container);
		cardWidth = 60;
		offset = cardWidth/2;
		//Instantiates the in use stack and animation queue.
		inUse = new StackOfCards(0, 0, cardWidth, 0, offset * 3/2);
		animationQueue = new Queue<StackOfCards>();

		init(); //Initializes all of the stacks.
	}

	/*
	 * Sets the default yCoord (y of this row of tableaus)
	 * Can be overriden by games that decide the tableaus should be moved down
	 */
	protected void setCoord(Container container) {
		yCoord = container.getHeight()/12;
	}

	/**
	 * Initializes all of the stacks of cards either directly or from a helper
	 * method (except for <code>inUse</code>). The <code>tableaux</code> aren't
	 * initialized here but the parameters for calling <code>initTableaux</code>
	 * are.
	 */

	protected void init(){
		//The initial decks.
		StackOfCards deck = new StackOfCards();
		StackOfCards deck2 = new StackOfCards();
		//fill deck & deck2 twice to hold 104 cards & shuffle
		deck.fillBySuit(); deck.fillBySuit(); deck.shuffle();
		deck2.fillBySuit(); deck2.shuffle();
		baseCard = deck.pop();
		//Calls initTableaux with the random deck and an anonymous array that
		//holds the initial tableau sizes.
		initTableaux(deck,new int[]{1,1,1,1,1,1,1,1});
		initStockAndWaste(deck,deck2); //Initializes the stocks and waste
		initFoundations(8,deck);		      //and foundations
		pushFoundation(foundations,baseCard);
		initialized = true; 		 	  //Everything is initialized,
		container.repaint();  //So we repaint.
	}

	/**
	 * Initializes the size, location, and number tableaux and the cards in each
	 * tableau.
	 *
	 * @param source	The source deck where all of the cards will be dealt from.
	 * 					Cards will be removed from the source stack.
	 * @param initialTableauxSizes 	An array whose length equals the number of
	 * 								tableaux and each element holds
	 * 								the number of cards in each tableau.
	 */
	@Override
	protected void initTableaux(StackOfCards source, int[] initialTableauxSizes){
		//Sets the number of tableau columns.
		tableaux = new Tableau[initialTableauxSizes.length];
		int increment = 3;
		//Initializes each tableau
		for(int i = 0; i < tableaux.length; i++){
			tableaux[i] = new Tableau(increment*(cardWidth+10)+20,yCoord+cardWidth*2,cardWidth,offset);
			increment++;
		
			for(int j = 0; j < initialTableauxSizes[i]; j++){ //Moves cards from
				tableaux[i].push(source.pop());          //source to tableau
				tableaux[i].peek().setHidden(initiallyHidden);
			}
		}
		for(StackOfCards stack : tableaux){ //For each tableau,
			stack.peek().setHidden(false); //we show the top card.
		}
	}

	/**
	 * Initializes the stock and waste. The stock will contain all of the given
	 * deck.
	 * @param deck The source of cards for the stock.
	 */
	protected void initStockAndWaste(StackOfCards deck,StackOfCards deck2){
		stock = new StackOfCards(cardWidth + 10, yCoord, cardWidth, 0, 0);
		stock.appendStack(deck); //The stock contains all of its cards.
		stock.peek().setHidden(true); //So that the stock is hidden.
		
		// THIS IS WHERE I TRIED TO DECLARE THE SECOND STOCK AND PLACE IT DIRECTLY UNDER THE MAIN STOCK
		stock2 = new StackOfCards(cardWidth+10,yCoord+cardWidth*2,cardWidth,0,0);
		for(int i=0;i<20;i++) {
			stock2.push(deck2.pop());
		}
		stock2.peek().setHidden(false);
		
		waste = new StackOfCards(2*(stock.getX()), yCoord, cardWidth, 0, 0);
	}

	/**
	 * Initializes the size and location of foundation stacks which are initially empty.
	 */
	protected void initFoundations(int numOfFoundations,StackOfCards source){
		int increment = 3;
		foundations = new Foundation[numOfFoundations];
		for(int i = 0; i < foundations.length; i++){
			foundations[i] = new Foundation(increment*(stock.getX())+20,yCoord,cardWidth);
			increment++;
		}
	}
	protected void pushFoundation(Foundation[] foundations, Card card) {
		foundations[0].pushBase(baseCard);
		container.repaint();
	}
	
	/**
	 * If one foundation in {@link #foundations} contains the given coordinates,
	 * and only one card is in {@link #inUse}, and either:
	 * <ul>
	 * <li>that foundation is empty and that card is an ace, or
	 * <li>that foundation is not empty and that card's value is one more than the
	 * 		value of the top card of the foundation and of the same suit,
	 * </ul>
	 * then that card is added to the foundation and <code>true</code> is returned.
	 * Otherwise nothing is performed and the method returns <code>false</code>.
	 * 
	 * @param x		The x coordinate of a mouse click.
	 * @param y		The y coordinate.
	 * @return <code>true</code> if the action above was performed, 
	 * 			else <code>false</code>
	 */
	protected boolean foundationsReleasedAction(int x, int y){
		if(inUse.isEmpty() || inUse.size() != 1){ //Only 1 card can be added to
			return false;						  //a foundation at a time.
		}
		for(Foundation foundation : foundations){
			//If the foundation was clicked.
			if(foundation.contains(x, y) || (foundation.isEmpty()
					&& foundation.shapeOfNextCard().contains(x, y))){
				try {
					//Peek is used in case the card is not appended.
					foundation.push2(inUse.peek(),baseCard);
					//if an exception was not thrown:
					inUse.pop(); //we pop.
					flipLastStack();
					return true; //The action was performed
				} catch(IllegalArgumentException ex){ //If an exception was thrown,
					return false; //we return false as nothing was done.
				}
			}
		}
		return false;
	}
	
	/**
	 * Performs the action associated with stock when clicked. If the stock is not
	 * empty, a card will be flipped from the stock to the waste, otherwise, the
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
			waste.push(stock.pop());waste.push(stock.pop());waste.push(stock.pop());	// Burn 3 cards.  
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

	/**
	 * Paints all of the stacks. This should be placed in the container's paint
	 * method.
	 */
	public void paint(Graphics pane){
		if(initialized){
			for(StackOfCards tableau : tableaux){
				tableau.draw(pane);
			}
			for(StackOfCards foundation : foundations){
				foundation.draw(pane);
			}
			if(stock != null && !stock.isEmpty())
				stock.peek().draw(pane);
			if(stock2 !=null&& !stock2.isEmpty()) 
				stock2.peek().draw(pane);
			if(waste != null && !waste.isEmpty())
				waste.peek().draw(pane);
			if(inUse != null && !inUse.isEmpty())
				inUse.draw(pane);

			updateAnimationQueue();
			for(StackOfCards stack : animationQueue){
				if(!stack.isEmpty()){
					stack.draw(pane);
				}
			}
		}
	}	
}

