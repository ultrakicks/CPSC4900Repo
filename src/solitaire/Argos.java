package solitaire;

import java.awt.*;
import java.awt.event.*;
import java.util.Collections;

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
 * The game is won when three rows of tableaux are completed, and is lost if three rows are not completed before the stock is exhausted
 * @author Team1
 */
public class Argos extends Klondike {
    StackOfCards playDeck = new StackOfCards();
    StackOfCards stockDeck = new StackOfCards();
    boolean hintOn = false;

	/**
	 * Instantiates the game and the panel.
	 * @param container The container for the game.
	 */
	public Argos(Container container){
        this.container = container;

        container.addMouseListener(this); 		//To respond to clicks
        container.addMouseMotionListener(this); //and dragging.
	container.setBackground(new Color(0, 60, 0));

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
                int whiteSpace = (container.getWidth()-(cardWidth*17))/2;


                StackOfCards copyStock = new StackOfCards();
                copyStock.appendStack(stockDeck.copy());

                StackOfCards tempStack = new StackOfCards();

                for (int i=0; i<13; i++){
                    for (int j=0; j<4; j++){
                        tableaux[i+13*j].setSize(cardWidth);
                        tableaux[i+13*j].setLocation((whiteSpace + cardWidth + (cardWidth + offset/2) * i),
                                (int) (yCoord + (j*(2.25*cardWidth))));
                        tableaux[i+13*j].setOffset(0, offset);
                    }
                }

                stock.setLocation((whiteSpace + cardWidth + (cardWidth + offset/2) * 5), 9*cardWidth+yCoord);
                stock.setSize(cardWidth);

                waste.setSize(cardWidth);
                waste.setLocation((whiteSpace + cardWidth + (cardWidth + offset/2) * 7), 9*cardWidth+yCoord);

                inUse.setSize(cardWidth);

                container.repaint();
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

		//Init stacks
		initTableaux(playDeck);
		initStockAndWaste(stockDeck);

		//Automatically draw the first card
		stockPressedAction(stock.getX(),stock.getY());
		initialized = true;

		//Update screen
		container.repaint();
	}

    /**
     * Set up the grid of 52 play tableaux in 4 columns of 13
     * Last 4 cards on source stack must be kings
     * @param source - 52 cards used to set up the play tableaux
     */
	private void initTableaux(StackOfCards source) {
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

    /**
     * Argos has no foundations, so don't take action
     * @param x		The x coordinate of a mouse click.
     * @param y		The y coordinate.
     * @return false
     */
	@Override
	protected boolean foundationsReleasedAction(int x, int y) { return false;}

	/**
	 * Just initializes the stock as the waste isn't used.
	 * @param deck - cards in the stock deck
	 */
	@Override
	protected void initStockAndWaste(StackOfCards deck){
		stock = new StackOfCards((cardWidth + (cardWidth + offset/2) * 5), 9*cardWidth+yCoord, cardWidth, 0, 0);
		stock.appendStack(deck); //The stock contains all of its cards.
		stock.peek().setHidden(true); //So that the stock is hidden.

		waste = new StackOfCards((cardWidth + (cardWidth + offset/2) * 7), (yCoord+(cardWidth*9)),cardWidth,0,0);
	}

	/**
	 * Adds a card to to the waste stack if the stock deck
	 * has cards and contains the given point.
	 * @return  true if a card is dealt from the stock deck
     *          false if no card was dealt
	 */
	@Override
	protected boolean stockPressedAction(int x, int y){
        //If the stock was clicked, and it still contains cards:
        if(stock.contains(x, y)&&!stock.isEmpty()) {
                //Part of hint functionality
                hintOn = false;                     //The user dealt another card, so turn off hints for the last card dealt

                //Core stock deck function
                waste.pop();                        //Remove any present card if user deals another
                waste.push(stock.pop());            //Move the top card from stock to waste.
                waste.peek().setHidden(false);      //And show it.
                if (!stock.isEmpty())
                    stock.peek().setHidden(true);   //Hides the new top card of the stack, if there is one
                container.repaint();                //Update the screen with new cards

                //Check for loss
                if (hasLost())
                    onLoss();

                //Unnecessary to check for wins b/c they will never occur directly after a new card is dealt

                return true; //The action was performed
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

				//if there is exactly one card on the tableau, and we are only attempting to put one card on it
				if (tableau.size()==1 && inUse.size()==1) {

					//Card value must be 2x tableau value, or 2*tableau value - 13
					if ((inUse.peek().getValue()==(2*tableau.peek().getValue()))||(inUse.peek().getValue()==(2*tableau.peek().getValue()-13))) {
						try {
							tableau.appendStack(inUse);
							//This code is not executed if an exception was thrown.
							inUse.clear();
							flipLastStack();

							//If this card placement makes three complete rows of cards, they've won
							if (hasWon())
								onWin();

							//Has to be below win-checking so that no unnecessary moves are generated if they've won
                            //If we emptied the waste stack, deal another card automatically
                            if (waste.isEmpty()) {
                                //Use short-circuiting to only check for loss if we cannot deal another card
                                if (!stockPressedAction(stock.getX(), stock.getY())&&hasLost())
                                    onLoss();
                            }
							return true;
						} catch (IllegalArgumentException ex) {
						}
					}
				}
			}
		}

		return false;//If we have reached this point, then no action was performed
	}

	//Cards cannot be picked up from the tableaux
	@Override
	protected boolean removableFromTableaux(Stack<Card> cards) { return false;}

	/**
	 * Show card placement hint if they right-click
     * Perform appropriate action if they left click on a stack of cards
	 */
	@Override
	public void mousePressed(MouseEvent e){
		int x = e.getX(), y = e.getY();

		if (SwingUtilities.isRightMouseButton(e)) {
            startHint();
        }
        if(SwingUtilities.isLeftMouseButton(e) && inUse.isEmpty() && !stockPressedAction(x, y) && !wastePressedAction(x,y)){
			//Do the tableaux action if the stock action wasn't done.
			tableauxPressedAction(x, y);
		}

	}

	/**
	 * Start showing card placement hints
	 */
	private void startHint(){
	    hintOn = true;
	    container.repaint();
    }

    /**
     * check to see if three tableeu rows are complete
     * @return true if so, false otherwise
     */
    @Override
	public boolean hasWon(){
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
		return rowsComplete>=3;
	}

    /**
     * Display a message that the user has to acknowledge
     * telling them that they won, and # moves
     * Then send them to the main menu
     */
	@Override
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
                Solitaire.switchScreens(Solitaire.games.MENU);
            }
        }).start();
    }

    /**
     * @return true if there is no way the user could generate three complete rows with the present cards
     *          false otherwise
     */
    private boolean hasLost(){
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
        //If waste is empty, stock and waste are both empty, so only check for row completion
        if (waste.isEmpty())
            return rowsComplete<3;
        return rowsComplete<3 && stock.isEmpty() && getPlacements(waste.peek()).size()==0;
    }


    /**
     * Show loss dialog
     * Return user to main menu
     */
    private void onLoss() {
        Statistics.leaveGame("Argos");


        new Thread(new Runnable(){
            public void run() {
                JOptionPane.showMessageDialog(container,"Sorry, you lost. Better luck next time!");
                Solitaire.switchScreens(Solitaire.games.MENU);
            }
        }).start();
    }

    /**
     * Get the list of tableaux a card can be placed on
     * @param card - the card we are attempting to place
     * @return list of possible placements
     */
    private java.util.List<Tableau> getPlacements(Card card){
	    java.util.List placements = new java.util.LinkedList<Tableau>();
	    for (Tableau tab : tableaux) {
	        if (tab.size()==1){
	            if (card.getValue() == 2*tab.peek().getValue())
	                placements.add(tab);
	            if (card.getValue() == (2*tab.peek().getValue()-13))
	                placements.add(tab);
            }
        }
	    return placements;
    }


	/**
	 * Paints all of the stacks. This should be placed in the container's paint
	 * method.
	 * Draws stock/waste deck and play tableaus of Argos
	 */
	@Override
	public void paint(Graphics pane){
		if(initialized){

            Card card = null;
            if (!waste.isEmpty())
                card = waste.peek();
            else if (!inUse.isEmpty())
                card = inUse.peek();


            if (!hintOn || card == null) {}
            else {
                pane.setColor(new Color(121, 185, 232));

                for (Tableau tab : getPlacements(card)) {
                    pane.fillRoundRect(tab.getX()-(cardWidth/2)-4, tab.getY()-(3*cardWidth/4)-4, cardWidth + 8, (int) (cardWidth * 1.5) + 8, cardWidth / 10, (int) (cardWidth * .15));
                }
            }

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

    /**
     * Used for file read/write, other methods that operate on multiple games
     * @return "Argos"
     */
    @Override
	public String getName() { return "Argos";	}
}
