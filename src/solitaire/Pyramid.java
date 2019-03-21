package solitaire;

import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.Graphics;

import card.Card;
import card.StackOfCards;
import card.Tableau;
import card.PyramidOfCards;
import dataStructures.StackADT;

/**
 * A form of Solitaire that is similar to {@link Klondike}. Unlike Klondike,
 * there isn't a stock and all cards are dealt into the tableaux. Additionally,
 * cards do not have to be in sequence with alternating colors to be removed
 * from a tableaux. Moreover, only the bottom card in use and the top card of a
 * tableau is relevant for appending the cards in use to said tableau column.
 * 
 * @author Warren Godone-Maresca
 *
 */
public class Pyramid extends Klondike {

	/**A reference to the stack of cards card that can be used to remove the 
	 * top card. Can be null if no card is selected */
	protected StackADT<Card> selectedStack;
	 //NOTE THAT THE PyramidOfCards CLASS WILL HAVE A SELECT( ) METHOD TO SELECT
	 //WHICH CARD IS HIGHLIGHTED. THIS MUST BE DONE WHENEVER A CARD FROM THE PYRAMID
	 //IS PLACED IN THIS INSTANCE VARIABLE

	/** Holds the pyramid of cards.											*/
	protected PyramidOfCards pyramid;

	/** The x coordinate for the pyramid.									*/
	protected int xCoord;

	/** Do nothing constructor.												*/
	public Pyramid(){}

	/**
	 * Instantiates the game with a {@link Container}.
	 * @param container The Container (such as window or applet) in which the 
	 * 					game will be played.
	 */
	public Pyramid(Container container){
		super(container);
	}

	/*
	 * Sets the default yCoord (y of this row of tableaus)
	 * Can be overriden by games that decide the tableaus should be moved down
	 */
	protected void setCoord(Container container) {
		yCoord = container.getHeight()/2;
		xCoord = container.getWidth()/2;
	}

	/**
	 * Initializes the game's stacks.
	 */
	@Override
	protected void init(){
		//The initial deck.
		StackOfCards deck = StackOfCards.randomDeck();

		//Calls initTableaux with the random deck and an anonymous array that
		//holds the initial tableau sizes.
		initTableaux(deck, new int[] {4, 4, 4, 4, 4, 4});
		initPyramid(deck, 7);

		initialized = true; //Everything is initialized,
		container.repaint();//So we repaint.
	}

	/**
	 * Initializes the tableaux.
	 */
	@Override
	protected void initTableaux(StackOfCards source, int[] initialTableauxSizes){
		//Sets the number of tableau columns.
		tableaux = new Tableau[initialTableauxSizes.length];

		//Initializes each tableau
		for(int i = 0; i < tableaux.length; i++){
			//Instantiates each tableau
			tableaux[i] = new Tableau(
					(cardWidth+10)*(i+1), yCoord + cardWidth*2, cardWidth, offset);

			for(int j = 0; j < initialTableauxSizes[i]; j++){ //Moves cards from
				tableaux[i].push(source.pop());          	  //source to tableau

				if(j > initialTableauxSizes[i] - 6){ //We show the top 6 cards
					tableaux[i].peek().setHidden(false);
				} else {
					tableaux[i].peek().setHidden(true);
				}
			}
		}
	}

	/**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
	 * Initializes the pyramid.
	 */
	protected void initPyramid(StackOfCards source, int size) {

		pyramid = new PyramidOfCards(xCoord, cardWidth/2,
			cardWidth, cardWidth/2 + 2, offset, size);

		//Fill pyramid
		for(int i=1; i <= size; i++) {
			for(int j=1; j<=i; j++) {
				pyramid.push(source.pop());
				pyramid.peek().setHidden(false);
			}
		}
	}

	/**
	 * Performs the action associated with the tableaux. If one tableau contains
	 * the coordinates and the card chosen is on top, that card will be highlighted
	 * @param x		The x coordinate of a mouse click.
	 * @param y		The y coordinate.
	 * @return <code>true</code> if the action was successfully performed, 
	 * 			else <code>false</code>
	 */
	protected boolean tableauxPressedAction(int x, int y){
		for(Tableau tableau : tableaux){ //Check each tableau,
			if(tableau.contains(x, y)){  //and if the mouse clicked a tableau,

				//Check mouse is colliding with top card.
				if(!tableau.isEmpty() && tableau.peek().colliding(x, y)) {
					//Check if current highlighted card is 13 minus selected card
					if(selectedStack != null
						&& selectedStack.peek().getValue() + tableau.peek().getValue() == 13) {
						//Remove both
						tableau.pop();
						selectedStack.pop();
						setSelected(null);
					} else {
						//Set this tableau as the new selectedStack
						setSelected(tableau);
					}
					return true;
				}
				return false; //Inactive card was clicked
			}
		}
		return false; //No tableau was clicked.
	}

	/**
	 * Performs the action associated with the pyramid. Finds, and selects
	 * the top card being collided with. That card will be highlighted
	 * @param x		The x coordinate of a mouse click.
	 * @param y		The y coordinate.
	 * @return <code>true</code> if the action was successfully performed, 
	 * 			else <code>false</code>
	 */
	protected boolean pyramidPressedAction(int x, int y){
		if(pyramid.contains(x, y)){  //if the mouse clicked the pyramid,
			//If the card was actually obtainable (not covered), select it
			if(pyramid.selectCard(x, y)) {
				if(selectedStack != null
					&& selectedStack.peek().getValue() + pyramid.peek().getValue() == 13) {
					//Remove both
					pyramid.pop();
					selectedStack.pop();
					setSelected(null);
				} else {
					//Set the pyramid as the new selectedStack
					setSelected(pyramid);
				}
				return true;
			}
		}
		return false; //No tableau was clicked.
	}

	/**
	 * Highlights the given card and removes the highlight from the last card
	 * 
	 * @param highlightedCard		newly selected card.
	 * @param y						newly selected stack.
	 * @return <code>true</code> if the action was successfully performed, 
	 * 			else <code>false</code>
	 */
	protected void setSelected(StackADT<Card> highlightedStack){
		//Remove highlight from old card (if it still exists)
		if(selectedStack.peek().isHighlighted()) {

		}

		//If the parameter is not null, highlight new card
		if(highlightedStack) {
			highlightedStack.peek().highlight();
		}

		//Set new stack
		selectedStack = highlightedCard;
	}

	/**
	 * Checks if the user has won, and if they have'nt, then performs
	 * the tableaux pressed action.
	 */
	@Override
	public void mousePressed(MouseEvent e){
		if(hasWon()){				//If the user has won,
			container.repaint();	//repaint and
			onWin();				//perform the on win action
			return;
		}

		int x = e.getX(), y = e.getY();
		
		if(!pyramidPressedAction(x,y)){
			tableauxPressedAction(x, y);
		}
	}

	/**
	 * Paints all of the stacks. This should be placed in the container's paint
	 * method.
	 */
	@Override
	public void paint(Graphics pane){
		if(initialized){
			for(StackOfCards tableau : tableaux){
				tableau.draw(pane);
			}

			if(pyramid != null && !pyramid.isEmpty())
				pyramid.draw(pane);
			/*
			if(stock != null && !stock.isEmpty())
				stock.peek().draw(pane);
			if(waste != null && !waste.isEmpty())
				waste.peek().draw(pane);
			if(inUse != null && !inUse.isEmpty())
				inUse.draw(pane);
			*/
			
			updateAnimationQueue();
			for(StackOfCards stack : animationQueue){
				if(!stack.isEmpty()){
					stack.draw(pane);
				}
			}
		}
	}

	/**
	 * Determines if the user has won.
	 * @return 	<code>true</code> if each foundation has at least one card and
	 * 			4 or fewer tableaux have cards and those cards are sorted and
	 * 			not hidden.
	 */
	@Override
	protected boolean hasWon(){
		if(pyramid.isEmpty())
			return true;
		return false;
	}
}