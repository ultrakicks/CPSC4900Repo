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
	protected StackOfCards reserve;
	protected Card baseCard;

	public void line() {
		System.out.println("________________________________________");
		//System.out.println();
	}

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
		container.setBackground(new Color(0, 60, 0)); //A green color.
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
		StackOfCards reserve = new StackOfCards();
		//fill deck twice to hold 104 cards & shuffle
		deck.fillBySuit(); deck.fillBySuit(); deck.shuffle();
		//take 20 cards from deck to make reserve
		for(int i=0;i<20;i++) {
			reserve.push(deck.pop());
		}
		baseCard = deck.pop();
		//Calls initTableaux with the random deck and an anonymous array that
		//holds the initial tableau sizes.
		initTableaux(deck,new int[]{1,1,1,1,1,1,1,1});
		initStockAndWaste(deck,reserve); //Initializes the stocks and waste
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
	protected void initStockAndWaste(StackOfCards deck,StackOfCards partialdeck){
		stock = new StackOfCards(cardWidth + 10, yCoord, cardWidth, 0, 0);
		stock.appendStack(deck); //The stock contains all of its cards.
		stock.peek().setHidden(true); //So that the stock is hidden.
		
		// THIS IS WHERE I TRIED TO DECLARE THE SECOND STOCK AND PLACE IT DIRECTLY UNDER THE MAIN STOCK
		reserve = new StackOfCards(cardWidth+10,yCoord+cardWidth*2,cardWidth,0,0);
		reserve.appendStack(partialdeck);
		reserve.peek().setHidden(false);
		
		waste = new StackOfCards(2*(stock.getX()), yCoord, cardWidth, 0, 0);
	}

	/**
	 * Initializes the size and location of foundation stacks which are initially empty.
	 */
	protected void initFoundations(int numOfFoundations,StackOfCards source){
		int increment = 3;
		foundations = new Foundation[numOfFoundations];
		for(int i = 0; i < foundations.length; i++){
			foundations[i] = new Foundation(increment*(stock.getX())+20,yCoord,cardWidth,0);
			increment++;
		}
	}
	protected void pushFoundation(Foundation[] foundations, Card card) {
		foundations[0].pushBase(baseCard);
		container.repaint();
	}

	// MOUSE PRESSED & RELEASED FUNCTIONS
	/**
	 * Performs the pressed action methods.
	 */
	public void mousePressed(MouseEvent e){
		System.out.println("mousePressed function called...");
		if(hasWon()){					// If the user has won,
			container.repaint();			// repaint and
			onWin();						// perform the on win action
			return;
		}

		int x = e.getX(), y = e.getY();
		System.out.println("X = "+x+" Y =  " +y);

		//Short circuit evaluation is used to perform each action if the
		//previous action was not done.
		if(inUse.isEmpty() && !stockPressedAction(x,y) && !wastePressedAction(x,y)){
			System.out.println("Mouse not in stock or waste & Empty, deafult tableauxPressed"); line();
			tableauxPressedAction(x, y);

		}

	}

	/**
	 * Sets the location of the stack {@link #inUse} to the MouseEvent's location.
	 */
	public void mouseDragged(MouseEvent e){
		System.out.println("mouseDragged function being called...");
		if(inUse != null){//Just move the cards inUse When the mouse is dragged
			inUse.setLocation(e.getX() - deltaX, e.getY() - deltaY);
			container.repaint();                   //and repaint.
		}
	}

	/**
	 * Calls all of the release action methods. But if no action is performed,
	 * then the cards in {@link #inUse} are returned to {@link #lastStack}
	 */
	public void mouseReleased(MouseEvent e){
		System.out.println("MouseReleased function called...");
		if(inUse.isEmpty()){			//Then there is nothing to do when the mouse
			System.out.println( "final inUse check; empty? "+inUse.isEmpty()); line();
			return;					 //is released.
		}

		int x = e.getX(), y = e.getY(); //The mouse's location.
		System.out.println("Moused Released at: X = "+x+" Y = "+y);
		line();
		if(!tableauxReleasedAction(x, y) && !foundationsReleasedAction(x, y)){
			//Then no action was performed, so we return the cards to the
			System.out.println("Return inUse card(s) to last stock Called");
			System.out.println("mouseRelease call returnToLastStock...");
			returnToLastStack();		//last stack.
			//Returned from function call returnToLastStack()
			for(Tableau tableau: tableaux) {
				if(tableau.isEmpty()) {
					System.out.println("Triggered if check on tableEmpty from mouseRelease... is it?");
					System.out.println("Tableau should not be empty...");
				}
			}
		} else {
			moves++; //A move was made
			System.out.println("Return from releaseAction function...");
			System.out.println("final inUse Check; empty? "+inUse.isEmpty());
			for(Tableau tableau: tableaux) {
				if(tableau.isEmpty()) {
					System.out.println("Triggered if check on tableEmpty from mouseRelease... is it?");
					System.out.println("Tableau should be empty...");
					tableau.push(reserve.pop()); // Pop top reserve card into empty tableau
					System.out.println("Tableau should be filled now...");
				}
			}
			System.out.println("Tableau's are not empty..."); line();
		}
	}


// PRESSED ACTION FUNCTIONS

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
		System.out.println("stockPressedAction called");
		if(stock.contains(x, y)){
			//If the stock was clicked:
			System.out.println("Mouse was clicked on main Stock...");
			waste.push(stock.pop());waste.push(stock.pop());waste.push(stock.pop());	// Burn 3 cards.
			if(stock.size() < 3)
			{
				if(stock.size() < 2)
				{
					waste.push(stock.pop());
				}
				waste.push(stock.pop());waste.push(stock.pop());
			}
			System.out.println(waste.peek().isHidden());
			if(waste.peek().isHidden())
			{
				waste.peek().setHidden(false);
			}
			waste.peek().setHidden(false);//And show it.
			System.out.println("Stock was pushed, 3 cards burned");

			if(!stock.isEmpty()) {
				System.out.println("Stock not empty");
				stock.peek().setHidden(true);//Hides the new top card of the stack.
				moves++; //This counts as a move.
				container.repaint();
				return true; //The action was performed.
			}
		}
		if(reserve.contains(x, y)) {
			//If the reserve stock was clicked
			System.out.println("Mouse was clicked on reserve stock...");
			inUse.push(reserve.pop()); //Push top card from reserve into inUse
			lastStack = reserve;		  //Set last stack to reserve.
			moves++;					  //Count as a move
			container.repaint();
			return true;
		}
		else if(stock.shapeOfNextCard().contains(x, y)){
			System.out.println("main Stock is empty, re-pile from waste");
			//else if the mouse clicked the empty stock's area:
			//Turn over all cards from the waste to the stock,
			waste.shuffle();
			stock.appendStack(waste.reverseCopy());
			waste.clear(); //and clear the waste.

			if(!stock.isEmpty()){
				stock.peek().setHidden(true); //So that stock is turned form
				moves++;					  //the user.
			}
			return true; //The action was performed.
		}

		System.out.println("Mouse was not pressed on the stock..."); line();
		return false; //The action was not performed.

	}

	/**
	 * Performs the action associated with the waste if the waste contains
	 * the given coordinates. The action is to pop a card from the waste and put
	 * it inUse. In this method, the number of moves is incremented if the action
	 * is performed.
	 * @param x		The x coordinate of a mouse click.
	 * @param y		The y coordinate.
	 * @return <code>true</code> if the action was performed,
	 * 			else <code>false</code>
	 */
	protected boolean wastePressedAction(int x, int y){
		System.out.println("wastePressedAction function called...");
		//If the waste has cards and the mouse clicked the waste,
		if(waste.contains(x, y)){
			System.out.println("waste was clicked on...");
			inUse.push(waste.pop());			//then the top card from the waste is put inUse
			lastStack = waste;  				//and the waste becomes the last stack to be used
			moves++;
			System.out.println("inUse pushed top waste card, lastStack = waste... return"); line();
			return true; 					//The action was performed.
		}
		if(!inUse.isEmpty()) {
			System.out.println("Waste card is being dragged...");
		}
		else {
			System.out.println("Mouse was not pressed on waste...."); line();
		}
		return false; 						//The waste was not clicked.
	}


	/**
	 * Performs the action associated with the tableaux. If one tableau contains
	 * the coordinates, all cards below the mouse click will be put inUse and
	 * removed from the tableau. The action will be deemed successful if cards
	 * are put inUse
	 * @param x		The x coordinate of a mouse click.
	 * @param y		The y coordinate.
	 * @return <code>true</code> if the action was successfully performed,
	 * 			else <code>false</code>
	 */
	protected boolean tableauxPressedAction(int x, int y){
		System.out.println("tableauPressedAction called");

		for(Tableau tableau : tableaux){ //Check each tableau,
			if(tableau.contains(x, y)){  //and if the mouse clicked a tableau,
				System.out.println("Tableau was clicked");

				//The cards to be put inUse.
				Stack<Card> cards = tableau.popCardsBelow(y);
				if(tableau.isEmpty()) System.out.println("tableau is now empty...");

				if(!removableFromTableaux(cards)){
					System.out.println("Cards not removable, return back");
					//the cards are not removable so we put them back.
					tableau.appendStack(cards);
					return false; //The action was not performed.
				}

				//The y coordinate of the bottom card that was popped.
				int cardsY = cards.reverseCopy().peek().getY();

				deltaX = x - tableau.getX(); //How off center the click was
				deltaY = y - cardsY;		//relative to the card.

				//Then put all cards below the click in use, if they are suitable.
				inUse.appendStack(cards);
				lastStack = tableau; //And the the tableau becomes the last stack.
				System.out.println("inUse was appended: inUse empty? "+inUse.isEmpty());
				System.out.println("lastStack = tableau");
			}
		}
		if(!inUse.isEmpty()) {
			System.out.println("tableau card(s) is being dragged..."); line();
		} else {
			System.out.println("Mouse was not pressed on a tableau..."); line();
		}
		return false; //No tableau was clicked.
	}


// RELEASED ACTION FUNCTIONS

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
		//TRACKING & TESTING CONSOLE INFORMATION
		System.out.println("foundationsReleasedAction called...");
		System.out.println("Mouse X-Coord: "+x+", Mouse Y-Coord: "+y);
		System.out.println("Card attempting to be pushed--> Suit: "+ inUse.peek().getSuit()+" | Value: "+ inUse.peek().getValue());

		if(inUse.isEmpty() || inUse.size() != 1){ //Only 1 card can be added to
			return false;						  //a foundation at a time.
		}
		for(Foundation foundation : foundations){
			//If the foundation was clicked.
			if(foundation.contains(x, y) || (foundation.isEmpty()
					&& foundation.shapeOfNextCard().contains(x, y))){
				System.out.println("foundation was clicked/released on...");
				try {
					//Peek is used in case the card is not appended.
					foundation.americanPush(inUse.peek(),baseCard);
					System.out.println("Card was pushed on foundation..");
					//if an exception was not thrown:
					inUse.pop(); //we pop.
					System.out.println("inUse was popped, inUse cleared? "+inUse.isEmpty()+", returning...");
					line();
					flipLastStack();
					return true; //The action was performed
				} catch(IllegalArgumentException ex){ //If an exception was thrown,
					System.out.println("Invalid operation on foundation...");
					return false; //we return false as nothing was done.
				}
			}
		}
		System.out.println("Mouse was not released on a foundation..."); line();
		return false;
	}

	/**
	 * If a tableau in {@link #tableaux} contains the given coordinates and the
	 * cards in {@link #inUse} increment in value and alternated in color, then
	 * the cards inUse will be appended to the tableau and inUse will be cleared.
	 *
	 * @param x		The x coordinate of a mouse click.
	 * @param y		The y coordinate.
	 * @return <code>true</code> if the action above was performed,
	 * 			else <code>false</code>
	 */
	protected boolean tableauxReleasedAction(int x, int y){
		System.out.println("tableauReleasedAction called");
		System.out.println("Mouse X-Coord: "+x+", Mouse Y-Coord: "+y);

		for(Tableau tableau : tableaux){ //Check each of the tableaux
			if(tableau.contains(x, y) || tableau.shapeOfNextCard().contains(x, y)){
				System.out.println("Mouse Released on tableau..");
				//Then we check if the inUse stack can be appended to the
				//tableau per the rules of solitaire.
				try {
					tableau.americanAppend(inUse);
					System.out.println("Cards were appended to tableau column..");
					inUse.clear();
					flipLastStack();
					System.out.println("inUse Cleared? "+ inUse.isEmpty()+", return...");
					return true;
				} catch(IllegalArgumentException ex){
					System.out.println("Invalid operation...");
				}
				return false;
			}

		}
		System.out.println("Mouse was not released on a tableau column.."); line();
		return false;//If we have reached this point, then no action was performed
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
			if(reserve !=null&& !reserve.isEmpty())
				reserve.peek().draw(pane);
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

	/**
	 * Determines whether the following winning condition has been met:
	 * <ul>
	 * <li>The stock and waste are both empty.
	 * <li>All cards in the tableaux are not hidden.
	 * <li>Only four or fewer of the tableaux are not empty.
	 * <li>All foundations have at least one card.
	 * </ul>
	 * When these conditions are met, the user has won because all that is done
	 * is to move cards to the foundation without any transfers among the stock,
	 * waste, and tableaux.
	 * @return <code>true</code> if the above condition has been met, else
	 * 			<code>false</code>.
	 */
	protected boolean hasWon(){
		for(Foundation f : foundations){
			if(f.isEmpty()){
				return false; //a foundation is empty so the user hasn't won.
			}
		}

		int numOfNonEmptyTableaux = 0; //To check how many tableaux have cards
		for(Tableau tableau : tableaux){ //Checks each tableau if it is suitable.

			if(!Tableau.isSuitable(tableau)){ //If any tableaux is not suitable,
				return false;				  //the user has not won.
			} else if(tableau.size() != 0){
				numOfNonEmptyTableaux++;
			}
		}
		//If there are fewer than 4 nonempty tableaux, and the stock and waste
		//are empty, then the user has effectively won.
		return numOfNonEmptyTableaux <= 8 && stock.isEmpty() && waste.isEmpty() && reserve.isEmpty();
	}



	public String getName() {
		return "AmericanToad";
	}
}

