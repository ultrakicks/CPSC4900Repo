package solitaire;

import java.awt.geom.RoundRectangle2D;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JLabel;

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

	/** Holds the free card slot.											*/
	protected StackOfCards freeSlot;

	/** Holds the free card slot.											*/
	protected JLabel scoreText;

	/** The x coordinate for the pyramid.									*/
	protected int xCoord;

	/** Keeps track of time between frames to remove some score every second.*/
	protected int lastSecondStamp;

	/** Score bonus for winning. Decreases as time goes on					*/
	protected int timeBonus;

	/** Score bonus for matches made. Increases for each match				*/
	protected int moveScore;

	/** Do nothing constructor.												*/
	public Pyramid(){}

	/**
	 * Instantiates the game with a {@link Container}.
	 * @param container The Container (such as window or applet) in which the 
	 * 					game will be played.
	 */
	public Pyramid(Container container){
		super(container);
		container.setBackground(new Color(138, 43, 226)); //A green color.
	}

	/*
	 * Sets the default yCoord (y of this row of tableaus)
	 * Can be overriden by games that decide the tableaus should be moved down
	 */
	protected void setCoord(Container container) {
		yCoord = container.getHeight()/2 + cardWidth*2;
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
		//Create a freeSlot that can be collided with using the empty space of the stack
		freeSlot = new StackOfCards(container.getWidth() - (cardWidth+10), yCoord, cardWidth, 0, offset) {
			public boolean contains(int x, int y){

				if(new RoundRectangle2D.Double(this.x - cardWidth/2 - offsetX*size,
					this.y - cardWidth*3/4 + offsetY*size, cardWidth,
					cardWidth*3/2, cardWidth/10, cardWidth/10).contains(x,y)) {return true;}
						
				return false;
			}
		};

		//Create score text
		scoreText = new JLabel("0", JLabel.LEFT);

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
					(cardWidth+10)*(i+1), yCoord, cardWidth, offset);

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
				if(pyramid.peek() != null)
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
					removeHighlight();
					//Check if current highlighted card is 13 minus selected card
					if(selectedStack != null
						&& selectedStack.peek().getValue() + tableau.peek().getValue() == 13) {
						//Remove both
						tableau.pop();
						selectedStack.pop();
						setSelected(null);
					} else {
						if(tableau.peek().getValue() == 13) {
							tableau.pop();
							setSelected(null);
						} else {
							//Set this tableau as the new selectedStack
							setSelected(tableau);
						}
					}
					container.repaint();
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
			//Attempt to obtain the card
			Card selectedCard = pyramid.getCard(x,y);
			//If the card is obtainable (not covered), select it
			if(selectedCard != null) {
				removeHighlight();
				//Possibly removing this card and the selected card
				if(selectedStack != null
					&& selectedStack.peek().getValue() + selectedCard.getValue() == 13) {
					//Remove both
					selectedStack.pop();
					pyramid.selectCard(x, y);
					pyramid.pop();
					setSelected(null);
				} else {
					//Removing Kings
					if(selectedCard.getValue() == 13) {
						pyramid.selectCard(x, y);
						pyramid.pop();
						setSelected(null);
					} else {
						//Set the pyramid as the new selectedStack
						pyramid.selectCard(x, y);
						setSelected(pyramid);
					}
				}
				container.repaint();
				return true;
			}
		}
		return false; //No tableau was clicked.
	}

	/**
	 * Performs the action associated with the free slot. If the free slot
	 * contains a card, that card will either cross itself out with the selected
	 * card, or become the new selected card.
	 * If not, the selected card will be placed in the free slot
	 * @param x		The x coordinate of a mouse click.
	 * @param y		The y coordinate.
	 * @return <code>true</code> if the action was successfully performed, 
	 * 			else <code>false</code>
	 */
	protected boolean freeSlotPressedAction(int x, int y){
		if(freeSlot.contains(x, y)) {
			removeHighlight();
			//Check if there is already a card in the free slot
			if(freeSlot.peek() != null) {
				//Check if current highlighted card is 13 minus selected card
				if(selectedStack != null
					&& selectedStack.peek().getValue() + freeSlot.peek().getValue() == 13) {
					//Remove both
					freeSlot.pop();
					selectedStack.pop();
					setSelected(null);
				} else {
					//Select what's in the free slot
					setSelected(freeSlot);
				}
			} else {
				//There is no free card in the slot.
				if(selectedStack != null) {
					freeSlot.push(selectedStack.pop());
					setSelected(null);
				}
			}
			container.repaint();
			return true; //Inactive card was clicked
		}
		return false;
	}

	/**
	 * Removes highlight from the current selected card
	 */
	protected void removeHighlight(){
		//Remove highlight from old card (if it still exists)
		if(selectedStack != null && selectedStack.peek() != null) {
			selectedStack.peek().setHighlighted(false);
		}
	}

	/**
	 * Highlights the selected card of the given stack
	 * 
	 * @param highlightedCard		newly selected card.
	 * 
	 */
	protected void setSelected(StackADT<Card> highlightedStack){
		//If the parameter is not null, highlight new card
		if(highlightedStack != null) {
			highlightedStack.peek().setHighlighted(true);
		}

		//Set new stack
		selectedStack = highlightedStack;
	}

	/**
	 * Checks if the user has won, and if they have'nt, then performs
	 * the tableaux pressed action.
	 */
	@Override
	public void mousePressed(MouseEvent e){
		if(hasWon()){				//If the user has won,
			return;
		}

		int x = e.getX(), y = e.getY();
		
		if(!freeSlotPressedAction(x,y) && !pyramidPressedAction(x,y) && !tableauxPressedAction(x, y)){
		} else {
			//If any of the above actions returned true, that's an action
			moves++;
		}

		if(hasWon()){				//If the user has won,
			container.repaint();	//repaint and
			onWin();				//perform the on win action
			return;
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

			freeSlot.draw(pane);
			/*
			if(stock != null && !stock.isEmpty())
				stock.peek().draw(pane);
			if(waste != null && !waste.isEmpty())
				waste.peek().draw(pane);
			if(inUse != null && !inUse.isEmpty())
				inUse.draw(pane);
			*/
			/*
			updateAnimationQueue();
			for(StackOfCards stack : animationQueue){
				if(!stack.isEmpty()){
					stack.draw(pane);
				}
			}*/
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

	public String getName() {
		return "AztecPyramid";
	}
	
	@Override
	public void mouseReleased(MouseEvent e){}
	@Override
	public void mouseDragged(MouseEvent e){}
}