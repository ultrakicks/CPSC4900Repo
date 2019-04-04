package solitaire;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import card.Card;
import card.StackOfCards;
import card.Tableau;
import dataStructures.Queue;
import dataStructures.Stack;

import javax.swing.*;

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
    StackOfCards playDeck = new StackOfCards();
    StackOfCards stockDeck = new StackOfCards();
    int numDraws = 0;
	/**
	 * Instantiates the game and the panel.
	 * @param container The container for the game.
	 */
	public Argos(Container container){
        this.container = container;

        container.addMouseListener(this); 		//To respond to clicks
        container.addMouseMotionListener(this); //and dragging.
        container.setBackground(new Color(0, 100, 0)); //A green color.
        container.setSize(790, 720);
        container.setPreferredSize(container.getSize());

        setCoord(container);

		yCoord = (int) (container.getHeight() * 0.1);
		cardWidth = (container.getWidth()/17);
		offset = cardWidth/2;

        //Instantiates the in use stack and animation queue.
        inUse = new StackOfCards(0, 0, cardWidth, 0, offset * 3/2);
        animationQueue = new Queue<StackOfCards>();

        init();

        container.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                yCoord = (int) (container.getHeight() * 0.1);
                cardWidth = (container.getWidth()/17);
                //If card width would make cards too tall...
                if (cardWidth > container.getHeight()/12)
                {
                    //Limit it to maximum width possible based on height
                    cardWidth = (container.getHeight()/12);
                }
                offset = cardWidth/2;

                StackOfCards copyPlay = new StackOfCards();
                copyPlay.appendStack(playDeck.copy());
                StackOfCards copyStock = new StackOfCards();
                copyStock.appendStack(stockDeck.copy());
                initTableaux(copyPlay);
                initStockAndWaste(copyStock);
                int temp = numDraws;
                for (int i=0; i<temp; i++)
                    stockPressedAction(stock.getX(), stock.getY());
                numDraws = temp;
            }
        });

    }

	/**
	 * Initializes all of the stacks.
	 */
	@Override
	protected void init(){
		//Two standard decks, shuffled together, less the kings
		StackOfCards deck = new StackOfCards();

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

        StackOfCards copyPlay = new StackOfCards();
        copyPlay.appendStack(playDeck.copy());
        StackOfCards copyStock = new StackOfCards();
        copyStock.appendStack(stockDeck.copy());

		initTableaux(copyPlay);
		initStockAndWaste(copyStock);

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
			tableaux[12+(k*13)] = new Tableau(
                    cardWidth+(cardWidth+offset/2)*12,
					(int) (yCoord + (k*(2.25*cardWidth))), cardWidth, offset);

			tableaux[12+(k*13)].push(source.pop());
		}

		//Initializes each tableau except for the thirteenth column with remaining random cards
		for(int j = 0; j <  4; j++){
			for (int i = 0; i < 12; i++) {
				//Instantiates each tableau
				tableaux[i+(13*j)] = new Tableau(
						(cardWidth + (cardWidth + offset/2) * i),
						(int) (yCoord + (j*(2.25*cardWidth))), cardWidth, offset);

				tableaux[i+(13*j)].push(source.pop());          //source to tableau
				tableaux[i+(13*j)].peek().setHidden(false);
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
		stock = new StackOfCards((cardWidth + (cardWidth + offset/2) * 5), 9*cardWidth+yCoord, cardWidth, 0, 0);
		stock.appendStack(deck); //The stock contains all of its cards.
		stock.peek().setHidden(true); //So that the stock is hidden.

		waste = new StackOfCards((cardWidth + (cardWidth + offset/2) * 7), (yCoord+(cardWidth*9)),cardWidth,0,0);
	}

	/**
	 * Adds a card to each of the tableaux from the stock as long as the stock
	 * has cards and if the stock contains the given location.
	 * @return 	<code>true</code> if the stock contains the given location, else
	 * 			<code>false</code>.
	 */
	@Override
	protected boolean stockPressedAction(int x, int y){
		if(stock.contains(x, y)&&!stock.isEmpty()) {
			//If the stock was clicked, and it still contains cards:
				waste.push(stock.pop());        //Move the top card from stock to waste.
				waste.peek().setHidden(false);  //And show it.
				stock.peek().setHidden(true);   //Hides the new top card of the stack.
				moves++; //This counts as a move.
				container.repaint();
                numDraws++;
				return true; //The action was performed.
		}
		if (hasWon())
			onWin();
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
				//if there is exactly one card on the tableau, and we are only attempting to put one card on it
				if (tableau.size()==1 && inUse.size()==1) {
					//Card value must be 2x tableau value, or 2*tableau value - 13
					if ((inUse.peek().getValue()==(2*tableau.peek().getValue()))||(inUse.peek().getValue()==(2*tableau.peek().getValue()-13))) {
						try {
							tableau.appendStack(inUse);
							//This code is not executed if an exception was thrown.
							inUse.clear();
							flipLastStack();


							//If we emptied the waste stack, deal another card automatically
							if (waste.isEmpty()) {
								stockPressedAction(stock.getX(), stock.getY());
							}
							if (hasWon())
								onWin();
							return true;
						} catch (IllegalArgumentException ex) {
						}
					}
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
		//Iterate through rows, then columns
		boolean completeRow;
		int rowsComplete = 0;
		for (int i=0; i<4; i++) {
			completeRow=true;
			for (int j=0; j<13; j++) {
				completeRow &= tableaux[j+(i*13)].size()==2;
			}
			if (completeRow)
				rowsComplete++;
		}
		System.out.println("Complete Rows: "+rowsComplete);
		return rowsComplete>=3;
	}

	public void onWin() {
		Statistics.winGame("Argos");
		//Then we show a dialog box to alert the user of the fact.
		//We start another anonymous thread to show the dialog box because
		//the dialog will pause all threads if it is in the main thread.
		//Then we show a dialog box to alert the user of the fact.
		//We start another anonymous thread to show the dialog box because
		//the dialog will pause all threads if it is in the main thread.



		new Thread(new Runnable(){
			public void run() {
				JOptionPane.showMessageDialog(container,"Congratulations, you won in " + moves + " moves!");
			}
		}).start();
		Solitaire.switchScreens(Solitaire.games.MENU);
	}



	/**
	 * Paints all of the stacks. This should be placed in the container's paint
	 * method.
	 * Draws stock/waste deck and play tableaus of Argos
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

	public String getName() {
		return "Argos";
	}
}
