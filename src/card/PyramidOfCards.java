package card;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import dataStructures.BinaryStack;

/**
 * A {@link Stack} of {@link Card}s. This class has all of the functionality of
 * a typical binary stack ADT (push, pop, etc.) with some additional features for cards.
 * <p>
 * A <code>StackOfCards</code> object has coordinates for the bottom card in the
 * stack and offset value to set the location of a card relative the card below
 * it in the stack. Additionally, all cards in the stack will have the same size.
 * <p>
 * An empty stack can be instantiated with a constructor. Additionally,
 * the static method <code>randomDeck()</code> can be used to return a new 
 * <code>StackOfCards</code> object filled with 52 cards in random order. Although
 * all cards in that stack will have no size and be located at the origin. However,
 * these values can be set later with appropriate set methods.
 * 
 * @author Connor
 */
public class PyramidOfCards extends BinaryStack<Card> {

	/** The coordinates of the center of the top card.						*/
	protected int x, y;

	/** The width of all cards in the stack.								*/
	protected int cardWidth;

	/** The difference in x and y coordinates respectively of a card in the stack
	 * with the card below it. The bottom card will have coordinates (x,y).	 */
	protected int offsetX, offsetY;

	/**
	 * Instantiates an empty <code>StackOfCards</code> where all cards will have
	 * no size and be  positioned at the origin.
	 */
	public PyramidOfCards(){}

	/**
	 * Instantiates an empty stackOfCards with given values.<p>
	 * 
	 * Note: the height of the cards will be based on the width of cards and the
	 * dimensions of a standard card.
	 * 
	 * @param x			The x coordinate for the center of the card on the 
	 * 					bottom of the stack.
	 * @param y			The y coordinate for the center of the card on the 
	 * 					bottom of the stack.
	 * @param cardWidth	The width of each card in the stack.
	 * @param offsetX	The difference in x positions of a card in the stack and
	 * 					the card below it. If offsetX is passed as 0, then all 
	 * 					cards will be placed directly on top of each other. If >0,
	 * 					then the cards will be placed to right of the previous 
	 * 					card.
	 * @param offsetY	The difference in y coordinates of a card in the stack
	 * 					with that of the card below it.
	 */
	public PyramidOfCards(int x, int y, int cardWidth, int offsetX, int offsetY, int numRows){
		super(numRows);
		this.x = x;
		this.y = y;
		this.cardWidth = cardWidth;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	/**
	 * Returns a new <code>StackOfCards</code> object with 52 cards where all cards
	 * are at the origin with no size and are not hidden.
	 */
	public static StackOfCards randomDeck(){
		StackOfCards deck = new StackOfCards();
		deck.fillBySuit();
		deck.shuffle();           //then shuffled.
		return deck;
	}

	/**
	 * Adds <code>card</code> to this stack. The card's x coordinate will be
	 * <code>x</code> + (<code>offsetX</code>) * (the previous size). The y
	 * coordinate will be set similarly (with <code>offsetY</code> used
	 * instead). The size of <code>card</code> will be set according to 
	 * <code>cardWidth</code>.
	 */
	public void push(Card card){
		//Note: size has not been incremented at this point, it will be incremented
		//in the super method.
		//The location of the card is changed to match the stack.
		int offx = 0; int offy = 0;
		int remsize = size+1; int level = 0;
		while(remsize > 0) {
			level++;
			if(remsize-level <= 0) {
				int cardsOff = remsize*2-(level+1);
				offx = offsetX * cardsOff;
			}
			remsize -= level;
		}
		offy = offsetY * level;

		card.setLocation(x + offx, y + offy);
		card.setSize(cardWidth); //And so is the size.
		super.push(card);

		select(card);
	}

	/**
	 * Adds 52 cards by suit.
	 */
	public void fillBySuit(){
		for(Suit suit : Suit.values()){
			for(int i = 1; i < 14; i++){
				push(new Card(suit, i, cardWidth, x, y, false));
			}
		}
	}
	
	
	/**
	 * Reverses the stack and sets each of the card's location accordingly. The
	 * new bottom card will be located at (x, y).
	 */
	public void reverse(){
		super.reverse();
		setLocation(x, y); //The location of each card is updated.
	}

	/**
	 * Reorders all of the cards randomly. The positions of the cards will be
	 * changed accordingly.
	 */
	public void shuffle(){
		//First merge shuffle is performed and the Knuth/Fisher-Yates shuffle is.
		//done. After several tests, these shuffles in this order had the most
		//uniform odds of some permutation of the cards being selected.
		knuthShuffle();
		setLocation(x, y);
	}

	/**
	 * Shuffles the deck by randomly by performing the Knuth/Fisher-Yates shuffle.
	 * 
	 * @return The index of the root; the head.
	 */
	private void knuthShuffle(){
		if(size < 2) //Then no shuffling needs to be performed.
			return;

		int tempSize = size;//The number of nodes that still need to be shuffled.
		
		//For each node in the list:
		for(int node = 1; node <= size; node++){
			
			//The position of the random node relative to node.
			int numOfIterations = (int)(Math.random() * tempSize--);
			
			//Advances the random node to its position.
			int randomNode = node + numOfIterations;
			swap(node, randomNode); //And swap node with randomNode
		}
	}
	
	
	/**
	 * Swaps the data in node1 and node2 between each other.
	 */
	private void swap(int node1, int node2){
		Card tempCard = (Card) queue[node1];	
		queue[node1] = (Card) queue[node2];
		queue[node2] = tempCard;
	}


	/**
	 * Sets the location of all cards in the stack according to the existing offset
	 * and the given (x,y) coordinates.
	 * @param x The x coordinate of the center of the bottom card.
	 * @param y The y coordinate of the center of the bottom card.
	 */
	public void setLocation(int x, int y){
		this.x = x;
		this.y = y;
		//Then the location of all elements are updated.
		for(int node = 1; node <= size; node++){
			if(queue[node] != null) {
				
				int offx = 0; int offy = 0;
				int remsize = size+1; int level = 0;
				while(remsize > 0) {
					level++;
					if(remsize-level <= 0) {
						int cardsOff = remsize*2-(level+1);
						offx = offsetX * cardsOff;
					}
					remsize -= level;
				}
				offy = offsetY * level;
				((Card)queue[node]).setLocation(x + offx, y + offy);

			}
		}
	}

	/**
	 * Sets the offset between a card in the stack and the card below in both
	 * dimensions. An offset of 0 will give all cards the same x and/or y 
	 * coordinate. An offset >0 will have a card located to the right and/or lower
	 * than the card underneath it in the stack (depending on x and y respectively).
	 * An offset <0 will do the reverse, the card will be to the left and/or above.
	 * 
	 * @param offsetX	The offset in the x direction.
	 * @param offsetY	The offset in the y direction.
	 */
	public void setOffset(int offsetX, int offsetY){
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		setLocation(x, y); //The location is update to match the new offset.
	}

	/**
	 * Determines whether a given point is within the stack.
	 * @param x The point's x coordinate.
	 * @param y The point's y coordinate.
	 * @return <code>true</code> if at least one card contains the point, else
	 *			<code>false</code>.
	 */
	public boolean contains(int x, int y){
		//Each card is checked to see if it contains the given location.
		for(int node = 1; node <= size; node++){
			if(queue[node] != null && ((Card) queue[node]).contains(x, y))
				return true;
		}
		return false;
	}

	/**
	 * Returns whether the card at position x,y has no cards on top of it
	 * @param x The point's x coordinate.
	 * @param y The point's y coordinate.
	 * @return <code>true</code> if at least one card without cards on top
	 * 			of it contains the point, else <code>false</code>.
	 */
	public boolean selectCard(int x, int y){
		//Each card is checked to see if it contains the given location.
		selected = findCard(x, y);
		if(selected != 0)
			return true;
		return false;
	}

	/**
	 * Returns a card at position x,y that has no cards on top of it
	 * @param x The point's x coordinate.
	 * @param y The point's y coordinate.
	 * @return <code>card at (x,y)</code> if at least one card without cards on top
	 * 			of it contains the point, else <code>false</code>.
	 */
	public Card getCard(int x, int y){
		//Each card is checked to see if it contains the given location.
		int node = findCard(x, y);
		if(node > 0) {
			return (Card) queue[node];
		}
		return null;
	}

	/**
	 * Finds a card at position x,y that has no cards on top of it
	 * @param x The point's x coordinate.
	 * @param y The point's y coordinate.
	 * @return The integer node value of the card, or 0 if no such card exists
	 */
	private int findCard(int x, int y){
		//Each card is checked to see if it contains the given location.
		for(int node = 1; node <= size; node++) {
			if(queue[node] != null && ((Card) queue[node]).contains(x, y)) {
				int row = 0; int tempNode = node;
				while(tempNode > 0) {
					row++;
					tempNode -= row;
				}
				if((node+row >= size || queue[node+row] == null) && (node+row+1 >= size || queue[node+row+1] == null)) {
					return node;
				}
			}
		}
		return 0;
	}

	/**
	 * Draws all of the cards in order of the stack. A card will be drawn
	 * on top of all cards below it in the stack.
	 * But if the stack is empty, the shape of a card will be drawn where a card
	 * would be located if added and with the corresponding size.
	 */
	public void draw(Graphics pane){/*
		if(isEmpty()){
			drawOutlineOfNextCard(pane);
		} else {
			draw(pane, 1);
		}*/
		for(int index=0; index <= size; index++) {
			if(queue[index] != null)
				((Card) queue[index]).draw(pane);
		}
	}

	/**
	 * Draws all cards below a given node containing a card.
	 *//*
	private void draw(Graphics pane, int val){
		if(val <= size && queue[val] != null){
			draw(pane, val*2);
			draw(pane, val*2+1);
			((Card) queue[val]).draw(pane);
		}
	}*/

	/**
	 * Returns the shape of a card where a card will be located if added
	 * and with the dimensions of this stack's cards.
	 * @return A {@link RoundRectangle2D} in the shape and location of a
	 * 			card.
	 */
	public RoundRectangle2D.Double shapeOfNextCard(){
		int offx = 0; int offy = 0;
		int remsize = size+1; int level = 0;
		while(remsize > 0) {
			level++;
			if(remsize-level <= 0) {
				int cardsOff = remsize*2-(level+1);
				offx = offsetX * cardsOff;
			}
			remsize -= level;
		}
		offy = offsetY * level;
		return new RoundRectangle2D.Double(
				x - cardWidth/2 + offx, y - cardWidth*3/4  + offy, 
				cardWidth, cardWidth*3/2, cardWidth/10, cardWidth/10);
	}

	/**
	 * Draws the {@link #shapeOfNextCard()} in light gray.
	 */
	public void drawOutlineOfNextCard(Graphics pane){
		pane.setColor(Color.LIGHT_GRAY);
		((Graphics2D)pane).fill(shapeOfNextCard());
	}

	/**
	 * Returns the x coordinate of where the center bottom card would be.
	 */
	public int getX(){
		return x;
	}

	/**
	 * Returns the y coordinate of where the center bottom card would be.
	 */
	public int getY(){
		return y;
	}
}