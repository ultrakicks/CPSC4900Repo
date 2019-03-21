package solitaire;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JOptionPane;

import card.Card;
import card.Foundation;
import card.StackOfCards;
import card.Tableau;
import dataStructures.Queue;
import dataStructures.Stack;

/**
 * A game of Klondike Solitaire. Klondike is one of the most
 * common forms of Solitaire and is sometimes referred to simply as "Solitaire".
 * This class facilitates the movement of cards between the various stacks and 
 * alerts the user that they have won if the cards are in a winning arrangement.
 * <p>
 * In Klondike Solitaire, 28 cards are dealt into the tableaux and the rest are
 * put in the stock. The user must fill the foundation by suit in sorted order.
 * The user can accomplish this by moving sub-stacks of cards between 
 * tableaux. A substack can only be moved if all of the values of adjacent cards
 * in the substack differ by one and the colors of adjacent cards are different
 * Also for that substack to placed onto a tableau, the value of the  
 * bottom card of the substack must be one less than the top card of the tableau
 * and differ in color. Users may also turn cards from the stock to the waste and
 * use the top card of the waste.
 * 
 * @author Warren Godone-Maresca
 */
public class AnnoDomini extends Klondike 
{
	/** Do nothing constructor.												*/
	public AnnoDomini(){}

	/** The x coordinate for the top foundations.									*/
	protected int xCoord;
	
	/**
	 * Instantiates the game with a {@link Container}.
	 * @param container The Container (such as window or applet) in which the 
	 * 					game will be played.
	 */
	public AnnoDomini(Container container)
	{
		this.container = container;
		container.addMouseListener(this); 		//To respond to clicks
		container.addMouseMotionListener(this); //and dragging.
		container.setBackground(new Color(0, 100, 100)); //A blue color.
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

	protected void setCoord(Container container) 
	{
		yCoord = container.getHeight()/12;
		xCoord = container.getWidth()/4;
	}

	/**
	 * Initializes all of the stacks of cards either directly or from a helper
	 * method (except for <code>inUse</code>). The <code>tableaux</code> aren't 
	 * initialized here but the parameters for calling <code>initTableaux</code>
	 * are.
	 */
	protected void init()
	{
		//The initial deck.
		StackOfCards deck = StackOfCards.randomDeck();

		//Calls initTableaux with the random deck and an anonymous array that
		//holds the initial tableau sizes.
		initTableaux(deck, new int[] {1, 1, 1, 1});
		initStockAndWaste(deck); //Initializes the stock and waste
		initFoundations(4);		//and foundations
		initialized = true; //Everything is initialized,
		container.repaint();//So we repaint.
	}
	
	/**
	 * Initializes the size and location of foundation stacks which are initially empty.
	 */
	protected void initFoundations(int numOfFoundations)
	{
		foundations = new Foundation[numOfFoundations];
		for(int i = 0; i < foundations.length; i++)
		{
			foundations[i] = new Foundation(tableaux[tableaux.length - i - 1].getX() + xCoord,
					yCoord, cardWidth);
		}
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
					foundation.push(inUse.peek());
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
	 * Calls all of the release action methods. But if no action is performed,
	 * then the cards in {@link #inUse} are returned to {@link #lastStack}
	 */
	@Override
	public void mouseReleased(MouseEvent e){
		if(inUse.isEmpty()){//Then there is nothing to do when the mouse
			return;					 //is released.
		}
		int x = e.getX(), y = e.getY(); //The mouse's location.
		if(!tableauxReleasedAction(x, y) && !foundationsReleasedAction(x, y)){
			//Then no action was performed, so we return the cards to the
			returnToLastStack();	//last stack.
		} else {
			moves++; //A move was made
		}
	}

	/**
	 * Return the cards that are in use to the last stack that was clicked.
	 */
	protected void returnToLastStack(){
		new StackOfCardsAnimator(inUse, lastStack, container);
	}

	/**
	 * Flips the top card of {@link #lastStack} if it is not empty.
	 */
	protected void flipLastStack(){
		if(!lastStack.isEmpty()){ //We unhide the top card
			lastStack.peek().setHidden(false); //of the last stack.
		}
		container.repaint();
	}
	
	/**
	 * Sets the location of the stack {@link #inUse} to the MouseEvent's location.
	 */
	@Override
	public void mouseDragged(MouseEvent e){
		if(inUse != null){//Just move the cards inUse When the mouse is dragged
			inUse.setLocation(e.getX() - deltaX, e.getY() - deltaY);
			container.repaint();                   //and repaint.
		}
	}

	/**
	 * Removes empty elements from the animation queue.
	 */
	protected void updateAnimationQueue(){
		while(!animationQueue.isEmpty()){ //While it has elements.
			if(animationQueue.peek().isEmpty()){//If the front element is empty,
				animationQueue.dequeue();		//remove it.
			} else {							//else it is not empty,
				return;							//so we are done.
			}
		}
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
		return numOfNonEmptyTableaux <= 4 && stock.isEmpty() && waste.isEmpty();
	}

	/**
	 * Alerts the user that s/he has won and plays the winning animation.
	 * Pre. <code>hasWon()</code> returns <code>true</code>.
	 */
	protected void onWin(){
		//We start a new anonymous thread with and anonymous runnable object
		//to play the winning animation.
		new Thread(new Runnable(){
			public void run(){
				winningAnimation();
			}
		}).start();
		
		//Then we show a dialog box to alert the user of the fact.
		//We start another anonymous thread to show the dialog box because
		//the dialog will pause all threads if it is in the main thread.
		new Thread(new Runnable(){
			public void run(){
				JOptionPane.showMessageDialog(container,
						"Congratulations, you won in " + moves + " moves!.");
			}
		}).start();
	}

	/**
	 * Plays the winning animation.
	 * Pre. <code>hasWon()</code> returns <code>true</code>.
	 */
	protected void winningAnimation(){
		//We calculate the number of cards in all of the foundations.
		int sizeOfFoundations = 0;
		for(Foundation f : foundations){
			sizeOfFoundations += f.size();
		}

		while(sizeOfFoundations < 52){ //until all cards are in the foundations.
			//If the animation queue has more than 5 cards, then we wait so as to
			//prevent the program from crashing by creating too many threads at
			if(animationQueue.size() > 6){						//the same time.
				try {
					Thread.sleep(100);
				} catch (InterruptedException e){}
				continue; //try again.
			}
			for(Foundation foundation : foundations){ //For each foundation:
				Card temp = foundation.peek(); //For comparisons.

				for(Tableau tableau : tableaux){
					//If the tableau:
					//-is not empty
					//-its top card's value is one greater than temp
					//-and it has the same suit as temp, then:
					if(!tableau.isEmpty() &&
							temp.compareTo(tableau.peek()) == -1
							&& temp.getSuit() == tableau.peek().getSuit()){

						//move the top card to the foundation and animate it.
						animateTopCardOf(tableau, foundation);
						sizeOfFoundations++;//One more card is in a foundation.
						
						break; //We don't need to look in another tableau.
					}
				}	
			}
		}
	}

	/**
	 * Moves the top card of a source stack to the destination and animates it.
	 * @param source		The stack whose top card is to be moved.
	 * @param destination	The stack to receive the card.
	 */
	protected void animateTopCardOf(StackOfCards source, StackOfCards destination){
		//Holds one of the cards in use for animation.
		StackOfCards temp = new StackOfCards(
				source.getX(), source.peek().getY(), 
				cardWidth, 0, 0);

		temp.push(source.pop()); //Moves a card to the temp.
		animationQueue.enqueue(temp); //and add temp to the queue.
		//Performs the animation.
		new StackOfCardsAnimator(temp, destination, container);
	}

	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
}