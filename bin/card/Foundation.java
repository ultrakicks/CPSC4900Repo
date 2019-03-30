package card;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A foundation is a {@link StackOfCards} in which all cards must be of the same
 * suit and each card's value is 1 more than that of the card below it. The bottom
 * card must be an ace.
 * @author Warren Godone-Maresca
 */
public class Foundation extends StackOfCards
{
	/** int for keeping track of value for year for Anno Domini game*/
	private int digit;

	/** char array that stores digits of the current year*/
	String yearNum = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	char[] yearArray = yearNum.toCharArray();

	/** Array that stores all suites*/
	public static List<String> suitsUsed = new ArrayList();

	/**
	 * Instantiates an empty <code>Foundation</code> where all cards will have
	 * no size and positioned at the origin.
	 */
	public Foundation(){}

	/**
	 * Instantiates an empty foundation with given values.<p>
	 *
	 * Note: the height of the cards will be based on the width of cards and the
	 * dimensions of a standard card.
	 *
	 * @param x			The x coordinate for the center of the card on the
	 * 					bottom of the stack.
	 * @param y			The y coordinate for the center of the card on the
	 * 					bottom of the stack.
	 * @param cardWidth	The width of each card in the stack.
	 */


	public Foundation(int x, int y, int cardWidth, int digit){
		super(x, y, cardWidth, 0, 0);
		this.digit = digit;
	}

	/**
	 * Adds a card to the top of the stack. If this stack was previously empty,
	 * then <code>card</code> must be an ace (have a value of 1), otherwise
	 * <code>card</code> must be 1 greater than the value of the card already on
	 * the top of this stack and have the same suit.
	 * @throws IllegalArgumentException if <code>card</code> does not meet the
	 * 			above conditions.
	 */
	/**
	 * Adds a card to the top of the stack. If this stack was previously empty,
	 * then <code>card</code> must be an ace (have a value of 1), otherwise
	 * <code>card</code> must be 1 greater than the value of the card already on
	 * the top of this stack and have the same suit.
	 * @throws IllegalArgumentException if <code>card</code> does not meet the
	 * 			above conditions.
	 */
	@Override
	public void push(Card card)
	{
		if(isEmpty())
		{                //If this is empty,
			if(card.getValue() == 1)
			{ //then the card must be an ace to be pushed.
				super.push(card);
			}
			else
			{
				throw new IllegalArgumentException();//TODO ""
			}
		}
		else
		{
			//Otherwise the card's value must be 1 greater than the top card
			//and be of the same suit.
			if(card.getValue() == peek().getValue() + 1
					&& card.getSuit() == peek().getSuit())
			{
				super.push(card);
			}
			else
			{
				throw new IllegalArgumentException();
			}
		}
	}

	public void americanPush(Card card, Card base) {
		if(isEmpty()){                //If this is empty,
			if(card.getValue() == base.getValue()){ //then the card must be an ace to be pushed.
				super.push(card);
			} else {
				throw new IllegalArgumentException();//TODO ""
			}
		} else {
			//Otherwise the card's value must be 1 greater than the top card
			//and be of the same suit.
			if(card.getValue() == peek().getValue() + 1
					&& card.getSuit() == peek().getSuit()){
				super.push(card);
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	public void annoPush(Card card)
	{
		boolean suitCanAdd = true;
		if(isEmpty())
		{                //If this is empty, check to see if the card values align with the year digits
			for (int i = 0; i < suitsUsed.size(); i++)
			{
				if (suitsUsed.get(i).equals(card.getSuit().name()))
				{
					suitCanAdd = false;
				}
			}

			if(
					(
							card.getValue() == (Character.getNumericValue(yearArray[digit]) + 1)
									&& suitCanAdd == true
					)
							||
							(
									card.getValue() == 11
											&& Character.getNumericValue(yearArray[digit]) == 0
											&& suitCanAdd == true
							)
			)
			{ //then the card must correspond to the digit of the year to be placed
				suitsUsed.add(card.getSuit().name());
				super.push(card);
			}
			else
			{
				throw new IllegalArgumentException();//TODO ""
			}
		}
		else
		{
			//Otherwise the card's value must be 1 greater than the top card
			//and be of the same suit.
			//OR it can be an ace being placed on a king.
			if((card.getValue() == peek().getValue() + 1
					&& card.getSuit() == peek().getSuit())
					|| (card.getValue() == 1
					&& peek().getValue() ==  13
					&& card.getSuit() == peek().getSuit()))
			{
				super.push(card);
			}
			else
			{
				throw new IllegalArgumentException();
			}
		}
	}


	public void pushBase(Card card) {
		super.push(card);
	}
}
