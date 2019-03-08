package solitaire;

import java.awt.*;
import java.awt.event.MouseEvent;

import card.Card;
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

	//TODO: Use these max values to set screen to full size, scale cards
	//Maximum width and height of screen
	private int maxHeight;
	private int maxWidth;
	private int screenWidth;
	private int screenHeight;
	{
		//Get screen size. We base all of our positioning on screen size so everything scales
		Toolkit sys = Toolkit.getDefaultToolkit();
		maxHeight = (int) sys.getScreenSize().getHeight();
		maxWidth = (int) sys.getScreenSize().getWidth();
	}
	/**
	 * Instantiates the game and the panel.
	 * @param container The container for the game.
	 */
	public Argos(Container container){
		super(container);

		//TODO: figure out how to set size of game window - this method doesn't work
		//container.setSize(maxWidth, maxHeight);
		//container.setPreferredSize(container.getSize());

		this.container = container;
		screenHeight = container.getHeight();
		screenWidth  = container.getWidth();

		yCoord = (int) (screenHeight * 0.1);
		cardWidth = (int) (screenWidth * 0.05);
		offset = cardWidth/2;
	}

	/**
	 * Initializes all of the stacks.
	 */
	@Override
	protected void init(){
		//TODO: Split into two decks, make sure four kings are in each
		//Two standard decks, shuffled together, less the kings
		StackOfCards deck = new StackOfCards();
		//48 random cards and four kings for play tableaux
		StackOfCards playDeck = new StackOfCards();

		//48 random cards and four kings for stock deck
		StackOfCards stockDeck = new StackOfCards();

		//Add two of each value/suit combination except kings to the deck (96 cards)
		for (card.Suit suit: card.Suit.values()) {
			for (int i=1; i<13; i++) {
				deck.push(new Card(suit,i,deck.getX(),deck.getY(),cardWidth,false));
				deck.push(new Card(suit,i,deck.getX(),deck.getY(),cardWidth,false));
			}
		}

		//Shuffle the cards
		deck.shuffle();

		//Split into stock and play decks (still no kings yet)
		for (int i=0; i<48; i++){
			stockDeck.push(deck.pop());
			playDeck.push(deck.pop());
		}

		//Add one king of each suit to each deck
		for (card.Suit suit : card.Suit.values()) {
			stockDeck.push(new Card(suit,13,deck.getX(),deck.getY(),cardWidth,false));
			playDeck.push(new Card(suit,13,deck.getX(),deck.getY(),cardWidth,false));
		}

		//Shuffle kings in to stock deck
		stockDeck.shuffle();

		initTableaux(playDeck);
		initStockAndWaste(stockDeck);

		//Automatically draw the first card
		stockPressedAction(stock.getX(),stock.getY());
		initialized = true;
		container.repaint();
	}

	protected void initTableaux(StackOfCards source) {
		//Sets the number of tableau columns.
		tableaux = new Tableau[52];

		//Initialize the thirteenth column, with kings
		for (int k = 0; k<4; k++) {
			tableaux[k] = new Tableau(
					(cardWidth * 5 + (cardWidth + offset/2) * 12),
					(int) (yCoord + (k*(2.25*cardWidth))), cardWidth, offset);

			tableaux[k].push(source.pop());
		}

		//Initializes each tableau except for the thirteenth column with remaining random cards
		for(int j = 0; j <  4; j++){
			for (int i = 1; i < 13; i++) {
				//Instantiates each tableau
				tableaux[(i-1)+(12*j)+4] = new Tableau(
						(cardWidth * 5 + (cardWidth + offset/2) * (i-1)),
						(int) (yCoord + (j*(2.25*cardWidth))), cardWidth, offset);

				tableaux[(i-1)+(12*j)+4].push(source.pop());          //source to tableau
				tableaux[(i-1)+(12*j)+4].peek().setHidden(false);
			}
		}
	}

	@Override
	protected boolean foundationsReleasedAction(int x, int y) {
		return false;
	}

	/**
	 * Just initializes the stock as the waste isn't used.
	 * Pre: the tableaux have been initialized.
	 */
	@Override
	protected void initStockAndWaste(StackOfCards deck){
		stock = new StackOfCards((int) (cardWidth*1.5), yCoord, cardWidth, 0, 0);
		stock.appendStack(deck); //The stock contains all of its cards.
		stock.peek().setHidden(true); //So that the stock is hidden.

		waste = new StackOfCards(stock.getX(), (int) (yCoord+(cardWidth*6.75)),cardWidth,0,0);
	}

	/**
	 * Adds a card to each of the tableaux from the stock as long as the stock
	 * has cards and if the stock contains the given location.
	 * @return 	<code>true</code> if the stock contains the given location, else
	 * 			<code>false</code>.
	 */
	@Override
	protected boolean stockPressedAction(int x, int y){
		if(stock.contains(x, y)) {
			//If the stock was clicked:
			waste.push(stock.pop());     //Move the top card from stock to waste.
			waste.peek().setHidden(false);//And show it.

			if (!stock.isEmpty())
				stock.peek().setHidden(true);//Hides the new top card of the stack.
			moves++; //This counts as a move.
			container.repaint();
			return true; //The action was performed.
		}
		return false;
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
	@Override
	protected boolean tableauxReleasedAction(int x, int y){
		for(Tableau tableau : tableaux){ //Check each of the tableaux
			if(tableau.contains(x, y) || tableau.shapeOfNextCard().contains(x, y)){
				//Then we check if the inUse stack can be appended to the
				//tableau per the rules of solitaire.
				if (tableau.size()==1 && inUse.size()==1) {
					try {
						tableau.appendStack(inUse);
						//This code is not executed if an exception was thrown.
						inUse.clear();
						flipLastStack();

						//If we emptied the waste stack, deal another card automatically
						if (waste.isEmpty()) {
							stockPressedAction(stock.getX(),stock.getY());
						}

						return true;
					} catch (IllegalArgumentException ex) {}
				}
			}
		}
		return false;//If we have reached this point, then no action was performed
	}

	//Cards cannot be picked up from the tableaux except for the last move
	@Override
	protected boolean removableFromTableaux(Stack<Card> cards) { return false;}

	/**
	 * Performs the actions associated with the stack that is pressed.
	 */
	@Override
	public void mousePressed(MouseEvent e){
		int x = e.getX(), y = e.getY();

		if(inUse.isEmpty() && !stockPressedAction(x, y) && !wastePressedAction(x,y)){
			//Do the tableaux action if the stock action wasn't done.
			tableauxPressedAction(x, y);
		}
	}

	/**
	 * TODO: Set up win condition
	 */
	public  boolean hasWon(){
		return false;
	}

	/**
	 * Paints all of the stacks. This should be placed in the container's paint
	 * method.
	 * Same paint method as Klondike, without foundations
	 */
	public void paint(Graphics pane){
		if(initialized){
			for(StackOfCards tableau : tableaux){
				tableau.draw(pane);
			}
			if(stock != null && !stock.isEmpty())
				stock.peek().draw(pane);
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