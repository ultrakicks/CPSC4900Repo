package dataStructures;

/**
 * @author Warren Godone-Maresca<p>
 * 
 * An implementation of a binary tree with two subnodes per node.
 * It has operations for standard stack operations, push, pop, and peek,
 * as well as some additional methods for to check the size, copy, reverse,
 * and clear the stack.
 * Popping and peeking is done by the select(___) method that will choose an
 * element to be the currently selected object that can be popped.
 *
 * @param <T> The data type of objects to be held in this stack.
 */
public class BinaryStack<T> implements StackADT<T> {
	// NOTE: THIS IS A BINARY QUEUE ACTING AS A STACK. TREAT IS AS SUCH.
	// FEEL FREE TO ADD OTHER QUEUE FUNCTIONALITY, BUT WE CAN ASSUME THAT VALUES
	// THAT AREN'T ON TOP OF THE STACK WON'T BE SELECTED

	/** The array of elements that make up this queue.						*/
	protected Object[] queue;

	/** Holds the number of elements in the stack.							*/
	protected int size = 0;

	/** Holds the index of the selected item. 0 is no item					*/
	protected int selected = 0;

	public BinaryStack(){} //There is nothing to initialize.
	/**
	 * Instantiates the stack.
	 */
	public BinaryStack(int maxRows){
		queue = new Object[(maxRows*(maxRows+1)/2)+1];
	}

	/**
	 * Adds the given value to the tree in queue order.
	 */
	public void push(T value){
		size++;
		queue[size] = value;
	}

	/**
	 * Selects an active element to be affected by pop and peek
	 * starting at the head
	 * 
	 * @param  value  the value item to be selected
	 * @return 	If the element is in the stack, true. If not, false
	 */
	public boolean select(T value) {
		return select(value, 1);
	}

	/**
	 * Selects an active element to be affected by pop and peek,
	 * starting at the subtree specified by the index parameter
	 * 
	 * @param  value  the value item to be selected
	 * @param  index  head of the subtree being inspected
	 * @return 	If the element is in the stack, true. If not, false
	 */
	public boolean select(T value, int index) {
		//Check if we're out of bounds
		if(index > size || value == null) {
			return false;
		}
		//Check if we've found our value
		if(queue[index].equals(value)) {
			selected = index;
			return true;
		}
		//Otherwise, check left and right subtrees
		boolean found = select(value, index*2);
		if(found) {
			return true;
		} else {
			found = select(value, index*2+1);
			if(found)
				return true;
		}
		return false;
	}

	/**
	 * Returns and removes the top (most recently added) element of the stack if
	 * such an element exists.
	 * @return 	If this stack is not empty, the element at the top of the stack,
	 * 			otherwise, null.
	 */
	@SuppressWarnings("unchecked")
	public T pop(){
		if(isEmpty()) { //Then there is nothing to pop.
			return null;
		}
		if(selected == 0) { //There is no selected value
			return null;
		}
		if(selected > size) { //Out of bounds
			return null;
		}
		T temp = (T)queue[selected];
		queue[selected] = null;
		selected = 0;
		size--;
		return temp;
	}

	/**
	 * Returns the top element of the stack if such an element exists.
	 * @return 	If this stack is not empty, the element at the top of the stack,
	 * 			otherwise, null.
	 */
	@SuppressWarnings("unchecked")
	public T peek(){
		if(isEmpty()){ //Then there is nothing to look at.
			return null; 
		}
		if(selected == 0) { //There is no selected value
			return null;
		}
		if(selected > size) { //Out of bounds
			return null;
		}
		return (T)queue[selected];
	}

	/**
	 * Determines whether or not the stack is empty.
	 * @return 	<code>true</code> if the stack has no elements, otherwise
	 * 			<code>false</code>
	 */
	public boolean isEmpty(){
		return (size <= 0);
	}

	/**
	 * Removes all elements from the stack.
	 */
	public void clear(){
		size = 0;
	}

	/**
	 * Returns the number of elements in the stack.
	 */
	public int size(){
		return size;
	}

	/**
	 * Reverses the order of all elements in the stack. This stack will be
	 * modified as a result.
	 */
	@SuppressWarnings("unchecked")
	public void reverse() {
		for(int i=0; i<size/2; i++) {
			T temp = (T)queue[i];
			queue[i] = queue[size-1-i];
			queue[size-1-i] = temp;
		}
	}

	/**
	 * Returns a shallow copy of this stack with the elements in reversed order.
	 */
	@SuppressWarnings("unchecked")
	public BinaryStack<T> reverseCopy() {
		BinaryStack<T> temp = new BinaryStack<T>(); //The reversed copy.
		for(int node=0; node<size/2; node++) {
			temp.push((T)queue[node]);
		}
		return temp;
	}

	/**
	 * Returns a shallow copy of this stack in which the order of the elements is
	 * preserved.
	 */
	public BinaryStack<T> copy(){
		BinaryStack<T> temp = reverseCopy();
		temp.reverse(); //reverse the reversed copy to put it in order.
		return temp;
	}

	/**
	 * Appends a given stack to this stack. The top element of the given stack
	 * will be the top element in this stack after this method is called. The order
	 * of all elements of the given stack will be preserved in this stack. The 
	 * bottom element of the given stack will be on top of the previouse
	 * top element of this stack.<p>
	 * 
	 * Additionally, the given stack object will be unmodified. It will still exists
	 * as a shallow copy of the top elements of this stack. The objects will not
	 * be copied and shall be contained in both stacks.
	 * 
	 * @param stack The stack to be appended to this stack.
	 */
	public void appendStack(Stack<T> stack){
		if(stack == null || stack.isEmpty()){
			return;
		}
		//So that the given stack is unmodified. It is reversed so that the
		//the elements are added in the proper order.
		Stack<T> temp = stack.reverseCopy();
		while(!temp.isEmpty()){
			push(temp.pop()); //We append each element of the
		}
	}

	/**
	 * Appends a given binary stack to this stack. The top element of the given stack
	 * will be the top element in this stack after this method is called. The order
	 * of all elements of the given stack will be preserved in this stack. The 
	 * bottom element of the given stack will be on top of the previouse
	 * top element of this stack.<p>
	 * 
	 * Additionally, the given stack object will be unmodified. It will still exists
	 * as a shallow copy of the top elements of this stack. The objects will not
	 * be copied and shall be contained in both stacks.
	 * 
	 * @param stack The stack to be appended to this stack.
	 */
	public void appendStack(BinaryStack<T> stack){
		if(stack == null || stack.isEmpty()){
			return;
		}
		//So that the given stack is unmodified. It is reversed so that the
		//the elements are added in the proper order.
		BinaryStack<T> temp = stack.reverseCopy();
		while(!temp.isEmpty()){
			push(temp.pop()); //We append each element of the
		}
	}
}