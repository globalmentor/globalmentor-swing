package com.garretwilson.swing;

import java.util.*;
import javax.swing.*;

/**A list model suitable for a <code>javax.swing.JList</code> component that
	delegates to a provided <code>java.util.List</code> object.
@author Garret Wilson
@see javax.swing.JList
@see java.util.List
*/
public class ListListModel extends AbstractListModel implements List	//G*** make sure we don't have to override the iterators---can we assume they will call back to these methods for removal and such?
{
	/**The list this class proxies.*/
	protected final List list;

	/**List constructor.
	@param list The list this list should proxy.
	*/
	public ListListModel(final List list)
	{
		this.list=list;	//save the list
	}

		//ListModel methods

	/** 
	 * Returns the length of the list.
	 * @return the length of the list
	 */
	public int getSize() {return list.size();}

	/**
	 * Returns the value at the specified index.  
	 * @param index the requested index
	 * @return the value at <code>index</code>
	 */
	public Object getElementAt(int index) {return list.get(index);}

		//Collection methods

	/**
	 * Returns the number of elements in this collection.  If this collection
	 * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * 
	 * @return the number of elements in this collection
	 */
	public int size() {return list.size();}

	/**
	 * Returns <tt>true</tt> if this collection contains no elements.
	 *
	 * @return <tt>true</tt> if this collection contains no elements
	 */
	public boolean isEmpty() {return list.isEmpty();}

	/**
	 * Returns <tt>true</tt> if this collection contains the specified
	 * element.  More formally, returns <tt>true</tt> if and only if this
	 * collection contains at least one element <tt>e</tt> such that
	 * <tt>(o==null ? e==null : o.equals(e))</tt>.
	 *
	 * @param o element whose presence in this collection is to be tested.
	 * @return <tt>true</tt> if this collection contains the specified
	 *         element
	 * @throws ClassCastException if the type of the specified element
	 * 	       is incompatible with this collection (optional).
	 * @throws NullPointerException if the specified element is null and this
	 *         collection does not support null elements (optional).
	 */
	public boolean contains(Object o) {return list.contains(o);}

	/**
	 * Returns an iterator over the elements in this collection.  There are no
	 * guarantees concerning the order in which the elements are returned
	 * (unless this collection is an instance of some class that provides a
	 * guarantee).
	 * 
	 * @return an <tt>Iterator</tt> over the elements in this collection
	 */
	public Iterator iterator() {return list.iterator();}

	/**
	 * Returns an array containing all of the elements in this collection.  If
	 * the collection makes any guarantees as to what order its elements are
	 * returned by its iterator, this method must return the elements in the
	 * same order.<p>
	 *
	 * The returned array will be "safe" in that no references to it are
	 * maintained by this collection.  (In other words, this method must
	 * allocate a new array even if this collection is backed by an array).
	 * The caller is thus free to modify the returned array.<p>
	 *
	 * This method acts as bridge between array-based and collection-based
	 * APIs.
	 *
	 * @return an array containing all of the elements in this collection
	 */
	public Object[] toArray() {return list.toArray();} 
	
	/**
	 * Returns an array containing all of the elements in this collection; 
	 * the runtime type of the returned array is that of the specified array.  
	 * If the collection fits in the specified array, it is returned therein.  
	 * Otherwise, a new array is allocated with the runtime type of the 
	 * specified array and the size of this collection.<p>
	 *
	 * If this collection fits in the specified array with room to spare
	 * (i.e., the array has more elements than this collection), the element
	 * in the array immediately following the end of the collection is set to
	 * <tt>null</tt>.  This is useful in determining the length of this
	 * collection <i>only</i> if the caller knows that this collection does
	 * not contain any <tt>null</tt> elements.)<p>
	 *
	 * If this collection makes any guarantees as to what order its elements
	 * are returned by its iterator, this method must return the elements in
	 * the same order.<p>
	 *
	 * Like the <tt>toArray</tt> method, this method acts as bridge between
	 * array-based and collection-based APIs.  Further, this method allows
	 * precise control over the runtime type of the output array, and may,
	 * under certain circumstances, be used to save allocation costs<p>
	 *
	 * Suppose <tt>l</tt> is a <tt>List</tt> known to contain only strings.
	 * The following code can be used to dump the list into a newly allocated
	 * array of <tt>String</tt>:
	 *
	 * <pre>
	 *     String[] x = (String[]) v.toArray(new String[0]);
	 * </pre><p>
	 *
	 * Note that <tt>toArray(new Object[0])</tt> is identical in function to
	 * <tt>toArray()</tt>.
	 *
	 * @param a the array into which the elements of this collection are to be
	 *        stored, if it is big enough; otherwise, a new array of the same
	 *        runtime type is allocated for this purpose.
	 * @return an array containing the elements of this collection
	 * 
	 * @throws ArrayStoreException the runtime type of the specified array is
	 *         not a supertype of the runtime type of every element in this
	 *         collection.
	 * @throws NullPointerException if the specified array is <tt>null</tt>.
	 */
    
	public Object[] toArray(Object a[]) {return list.toArray(a);}

	// Modification Operations

	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation).  Returns <tt>true</tt> if this collection changed as a
	 * result of the call.  (Returns <tt>false</tt> if this collection does
	 * not permit duplicates and already contains the specified element.)<p>
	 *
	 * Collections that support this operation may place limitations on what
	 * elements may be added to this collection.  In particular, some
	 * collections will refuse to add <tt>null</tt> elements, and others will
	 * impose restrictions on the type of elements that may be added.
	 * Collection classes should clearly specify in their documentation any
	 * restrictions on what elements may be added.<p>
	 *
	 * If a collection refuses to add a particular element for any reason
	 * other than that it already contains the element, it <i>must</i> throw
	 * an exception (rather than returning <tt>false</tt>).  This preserves
	 * the invariant that a collection always contains the specified element
	 * after this call returns.
	 *
	 * @param o element whose presence in this collection is to be ensured.
	 * @return <tt>true</tt> if this collection changed as a result of the
	 *         call
	 * 
	 * @throws UnsupportedOperationException <tt>add</tt> is not supported by
	 *         this collection.
	 * @throws ClassCastException class of the specified element prevents it
	 *         from being added to this collection.
	 * @throws NullPointerException if the specified element is null and this
	 *         collection does not support null elements.
	 * @throws IllegalArgumentException some aspect of this element prevents
	 *         it from being added to this collection.
	 */
	public boolean add(Object o)
	{
		final boolean changed=list.add(o);
		if(changed)	//if the list changed
		{
			fireIntervalAdded(this, list.size(), list.size());
		}
		return changed;
	}

	/**
	 * Removes a single instance of the specified element from this
	 * collection, if it is present (optional operation).  More formally,
	 * removes an element <tt>e</tt> such that <tt>(o==null ?  e==null :
	 * o.equals(e))</tt>, if this collection contains one or more such
	 * elements.  Returns true if this collection contained the specified
	 * element (or equivalently, if this collection changed as a result of the
	 * call).
	 *
	 * @param o element to be removed from this collection, if present.
	 * @return <tt>true</tt> if this collection changed as a result of the
	 *         call
	 * 
	 * @throws ClassCastException if the type of the specified element
	 * 	       is incompatible with this collection (optional).
	 * @throws NullPointerException if the specified element is null and this
	 *         collection does not support null elements (optional).
	 * @throws UnsupportedOperationException remove is not supported by this
	 *         collection.
	 */
	public boolean remove(Object o)
	{
		final int index=list.indexOf(o);
		final boolean changed=list.remove(o);
		if(changed)	//if the list changed
		{
			fireIntervalRemoved(this, index, index);
		}
		return changed;
	}

	// Bulk Operations

	/**
	 * Returns <tt>true</tt> if this collection contains all of the elements
	 * in the specified collection.
	 *
	 * @param  c collection to be checked for containment in this collection.
	 * @return <tt>true</tt> if this collection contains all of the elements
	 *	       in the specified collection
	 * @throws ClassCastException if the types of one or more elements
	 *         in the specified collection are incompatible with this
	 *         collection (optional).
	 * @throws NullPointerException if the specified collection contains one
	 *         or more null elements and this collection does not support null
	 *         elements (optional).
	 * @throws NullPointerException if the specified collection is
	 *         <tt>null</tt>.
	 * @see    #contains(Object)
	 */
	public boolean containsAll(Collection c) {return list.containsAll(c);}

	/**
	 * Adds all of the elements in the specified collection to this collection
	 * (optional operation).  The behavior of this operation is undefined if
	 * the specified collection is modified while the operation is in progress.
	 * (This implies that the behavior of this call is undefined if the
	 * specified collection is this collection, and this collection is
	 * nonempty.)
	 *
	 * @param c elements to be inserted into this collection.
	 * @return <tt>true</tt> if this collection changed as a result of the
	 *         call
	 * 
	 * @throws UnsupportedOperationException if this collection does not
	 *         support the <tt>addAll</tt> method.
	 * @throws ClassCastException if the class of an element of the specified
	 * 	       collection prevents it from being added to this collection.
	 * @throws NullPointerException if the specified collection contains one
	 *         or more null elements and this collection does not support null
	 *         elements, or if the specified collection is <tt>null</tt>.
	 * @throws IllegalArgumentException some aspect of an element of the
	 *	       specified collection prevents it from being added to this
	 *	       collection.
	 * @see #add(Object)
	 */
	public boolean addAll(Collection c)
	{
		final int index=list.size();
		final boolean changed=list.addAll(c);
		if(changed)	//if the list changed
		{
			fireIntervalAdded(this, index, index+c.size()-1);
		}
		return changed;
	}

	/**
	 * 
	 * Removes all this collection's elements that are also contained in the
	 * specified collection (optional operation).  After this call returns,
	 * this collection will contain no elements in common with the specified
	 * collection.
	 *
	 * @param c elements to be removed from this collection.
	 * @return <tt>true</tt> if this collection changed as a result of the
	 *         call
	 * 
	 * @throws UnsupportedOperationException if the <tt>removeAll</tt> method
	 * 	       is not supported by this collection.
	 * @throws ClassCastException if the types of one or more elements
	 *         in this collection are incompatible with the specified
	 *         collection (optional).
	 * @throws NullPointerException if this collection contains one or more
	 *         null elements and the specified collection does not support
	 *         null elements (optional).
	 * @throws NullPointerException if the specified collection is
	 *         <tt>null</tt>.
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	public boolean removeAll(Collection c)
	{
		final int oldSize=list.size();	//see how big the list was to begin with
		final boolean changed=list.removeAll(c);
		if(changed)	//if the list changed
		{
			fireIntervalRemoved(this, list.size(), oldSize-1);	//there's no way to tell which ones changed--assume the last ones were removed
			fireContentsChanged(this, 0, list.size()-1);	//assume the first ones changed
		}
		return changed;
	}

	/**
	 * Retains only the elements in this collection that are contained in the
	 * specified collection (optional operation).  In other words, removes from
	 * this collection all of its elements that are not contained in the
	 * specified collection.
	 *
	 * @param c elements to be retained in this collection.
	 * @return <tt>true</tt> if this collection changed as a result of the
	 *         call
	 * 
	 * @throws UnsupportedOperationException if the <tt>retainAll</tt> method
	 * 	       is not supported by this Collection.
	 * @throws ClassCastException if the types of one or more elements
	 *         in this collection are incompatible with the specified
	 *         collection (optional).
	 * @throws NullPointerException if this collection contains one or more
	 *         null elements and the specified collection does not support null 
	 *         elements (optional).
	 * @throws NullPointerException if the specified collection is
	 *         <tt>null</tt>.
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	public boolean retainAll(Collection c)
	{
		final int oldSize=list.size();	//see how big the list was to begin with
		final boolean changed=list.retainAll(c);
		if(changed)	//if the list changed
		{
			fireIntervalRemoved(this, list.size(), oldSize-1);	//there's no way to tell which ones changed--assume the last ones were removed
			fireContentsChanged(this, 0, list.size()-1);	//assume the first ones changed
		}
		return changed;
	}

	/**
	 * Removes all of the elements from this collection (optional operation).
	 * This collection will be empty after this method returns unless it
	 * throws an exception.
	 *
	 * @throws UnsupportedOperationException if the <tt>clear</tt> method is
	 *         not supported by this collection.
	 */
	public void clear()
	{
		final int oldSize=list.size();
		list.clear();
		if(oldSize>0)	//if anything was removed
			fireIntervalRemoved(this, 0, oldSize-1);	//show that everything was removed
	}

	// Comparison and hashing

	/**
	 * Compares the specified object with this collection for equality. <p>
	 *
	 * While the <tt>Collection</tt> interface adds no stipulations to the
	 * general contract for the <tt>Object.equals</tt>, programmers who
	 * implement the <tt>Collection</tt> interface "directly" (in other words,
	 * create a class that is a <tt>Collection</tt> but is not a <tt>Set</tt>
	 * or a <tt>List</tt>) must exercise care if they choose to override the
	 * <tt>Object.equals</tt>.  It is not necessary to do so, and the simplest
	 * course of action is to rely on <tt>Object</tt>'s implementation, but
	 * the implementer may wish to implement a "value comparison" in place of
	 * the default "reference comparison."  (The <tt>List</tt> and
	 * <tt>Set</tt> interfaces mandate such value comparisons.)<p>
	 *
	 * The general contract for the <tt>Object.equals</tt> method states that
	 * equals must be symmetric (in other words, <tt>a.equals(b)</tt> if and
	 * only if <tt>b.equals(a)</tt>).  The contracts for <tt>List.equals</tt>
	 * and <tt>Set.equals</tt> state that lists are only equal to other lists,
	 * and sets to other sets.  Thus, a custom <tt>equals</tt> method for a
	 * collection class that implements neither the <tt>List</tt> nor
	 * <tt>Set</tt> interface must return <tt>false</tt> when this collection
	 * is compared to any list or set.  (By the same logic, it is not possible
	 * to write a class that correctly implements both the <tt>Set</tt> and
	 * <tt>List</tt> interfaces.)
	 *
	 * @param o Object to be compared for equality with this collection.
	 * @return <tt>true</tt> if the specified object is equal to this
	 * collection
	 * 
	 * @see Object#equals(Object)
	 * @see Set#equals(Object)
	 * @see List#equals(Object)
	 */
	public boolean equals(Object o) {return list.equals(o);}	

	/**
	 * Returns the hash code value for this collection.  While the
	 * <tt>Collection</tt> interface adds no stipulations to the general
	 * contract for the <tt>Object.hashCode</tt> method, programmers should
	 * take note that any class that overrides the <tt>Object.equals</tt>
	 * method must also override the <tt>Object.hashCode</tt> method in order
	 * to satisfy the general contract for the <tt>Object.hashCode</tt>method.
	 * In particular, <tt>c1.equals(c2)</tt> implies that
	 * <tt>c1.hashCode()==c2.hashCode()</tt>.
	 *
	 * @return the hash code value for this collection
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 */
	public int hashCode() {return list.hashCode();}

	// Bulk Modification Operations

	/**
	 * Inserts all of the elements in the specified collection into this
	 * list at the specified position (optional operation).  Shifts the
	 * element currently at that position (if any) and any subsequent
	 * elements to the right (increases their indices).  The new elements
	 * will appear in this list in the order that they are returned by the
	 * specified collection's iterator.  The behavior of this operation is
	 * unspecified if the specified collection is modified while the
	 * operation is in progress.  (Note that this will occur if the specified
	 * collection is this list, and it's nonempty.)
	 *
	 * @param index index at which to insert first element from the specified
	 *	            collection.
	 * @param c elements to be inserted into this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * 
	 * @throws UnsupportedOperationException if the <tt>addAll</tt> method is
	 *		  not supported by this list.
	 * @throws ClassCastException if the class of one of elements of the
	 * 		  specified collection prevents it from being added to this
	 * 		  list.
	 * @throws NullPointerException if the specified collection contains one
	 *           or more null elements and this list does not support null
	 *           elements, or if the specified collection is <tt>null</tt>.
	 * @throws IllegalArgumentException if some aspect of one of elements of
	 *		  the specified collection prevents it from being added to
	 *		  this list.
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 *		  &lt; 0 || index &gt; size()).
	 */
	public boolean addAll(int index, Collection c)
	{
		final boolean changed=list.addAll(index, c);
		if(changed)	//if the list changed
		{
			fireIntervalAdded(this, index, index+c.size()-1);
		}
		return changed;
	}

	// Positional Access Operations

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index index of element to return.
	 * @return the element at the specified position in this list.
	 * 
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 * 		  &lt; 0 || index &gt;= size()).
	 */
	public Object get(int index) {return list.get(index);}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element (optional operation).
	 *
	 * @param index index of element to replace.
	 * @param element element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * 
	 * @throws UnsupportedOperationException if the <tt>set</tt> method is not
	 *		  supported by this list.
	 * @throws    ClassCastException if the class of the specified element
	 * 		  prevents it from being added to this list.
	 * @throws    NullPointerException if the specified element is null and
	 *            this list does not support null elements.
	 * @throws    IllegalArgumentException if some aspect of the specified
	 *		  element prevents it from being added to this list.
	 * @throws    IndexOutOfBoundsException if the index is out of range
	 *		  (index &lt; 0 || index &gt;= size()).
	 */
	public Object set(int index, Object element)
	{
		final Object oldObject=list.set(index, element);
		fireContentsChanged(this, index, index);
		return oldObject;
	}

	/**
	 * Inserts the specified element at the specified position in this list
	 * (optional operation).  Shifts the element currently at that position
	 * (if any) and any subsequent elements to the right (adds one to their
	 * indices).
	 *
	 * @param index index at which the specified element is to be inserted.
	 * @param element element to be inserted.
	 * 
	 * @throws UnsupportedOperationException if the <tt>add</tt> method is not
	 *		  supported by this list.
	 * @throws    ClassCastException if the class of the specified element
	 * 		  prevents it from being added to this list.
	 * @throws    NullPointerException if the specified element is null and
	 *            this list does not support null elements.
	 * @throws    IllegalArgumentException if some aspect of the specified
	 *		  element prevents it from being added to this list.
	 * @throws    IndexOutOfBoundsException if the index is out of range
	 *		  (index &lt; 0 || index &gt; size()).
	 */
	public void add(int index, Object element)
	{
		list.add(index, element);
		fireIntervalAdded(this, index, index);
	}

	/**
	 * Removes the element at the specified position in this list (optional
	 * operation).  Shifts any subsequent elements to the left (subtracts one
	 * from their indices).  Returns the element that was removed from the
	 * list.
	 *
	 * @param index the index of the element to removed.
	 * @return the element previously at the specified position.
	 * 
	 * @throws UnsupportedOperationException if the <tt>remove</tt> method is
	 *		  not supported by this list.
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 *            &lt; 0 || index &gt;= size()).
	 */
	public Object remove(int index)
	{
		final Object oldObject=list.remove(index);
		fireIntervalRemoved(this, index, index);
		return oldObject;
	}


	// Search Operations

	/**
	 * Returns the index in this list of the first occurrence of the specified
	 * element, or -1 if this list does not contain this element.
	 * More formally, returns the lowest index <tt>i</tt> such that
	 * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 *
	 * @param o element to search for.
	 * @return the index in this list of the first occurrence of the specified
	 * 	       element, or -1 if this list does not contain this element.
	 * @throws ClassCastException if the type of the specified element
	 * 	       is incompatible with this list (optional).
	 * @throws NullPointerException if the specified element is null and this
	 *         list does not support null elements (optional).
	 */
	public int indexOf(Object o) {return list.indexOf(o);}

	/**
	 * Returns the index in this list of the last occurrence of the specified
	 * element, or -1 if this list does not contain this element.
	 * More formally, returns the highest index <tt>i</tt> such that
	 * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 *
	 * @param o element to search for.
	 * @return the index in this list of the last occurrence of the specified
	 * 	       element, or -1 if this list does not contain this element.
	 * @throws ClassCastException if the type of the specified element
	 * 	       is incompatible with this list (optional).
	 * @throws NullPointerException if the specified element is null and this
	 *         list does not support null elements (optional).
	 */
	public int lastIndexOf(Object o) {return list.lastIndexOf(o);}


	// List Iterators

	/**
	 * Returns a list iterator of the elements in this list (in proper
	 * sequence).
	 *
	 * @return a list iterator of the elements in this list (in proper
	 * 	       sequence).
	 */
	public ListIterator listIterator() {return list.listIterator();}

	/**
	 * Returns a list iterator of the elements in this list (in proper
	 * sequence), starting at the specified position in this list.  The
	 * specified index indicates the first element that would be returned by
	 * an initial call to the <tt>next</tt> method.  An initial call to
	 * the <tt>previous</tt> method would return the element with the
	 * specified index minus one.
	 *
	 * @param index index of first element to be returned from the
	 *		    list iterator (by a call to the <tt>next</tt> method).
	 * @return a list iterator of the elements in this list (in proper
	 * 	       sequence), starting at the specified position in this list.
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 *         &lt; 0 || index &gt; size()).
	 */
	public ListIterator listIterator(int index) {return list.listIterator(index);}

	// View

	/**
	 * Returns a view of the portion of this list between the specified
	 * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
	 * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
	 * empty.)  The returned list is backed by this list, so non-structural
	 * changes in the returned list are reflected in this list, and vice-versa.
	 * The returned list supports all of the optional list operations supported
	 * by this list.<p>
	 *
	 * This method eliminates the need for explicit range operations (of
	 * the sort that commonly exist for arrays).   Any operation that expects
	 * a list can be used as a range operation by passing a subList view
	 * instead of a whole list.  For example, the following idiom
	 * removes a range of elements from a list:
	 * <pre>
	 *	    list.subList(from, to).clear();
	 * </pre>
	 * Similar idioms may be constructed for <tt>indexOf</tt> and
	 * <tt>lastIndexOf</tt>, and all of the algorithms in the
	 * <tt>Collections</tt> class can be applied to a subList.<p>
	 *
	 * The semantics of the list returned by this method become undefined if
	 * the backing list (i.e., this list) is <i>structurally modified</i> in
	 * any way other than via the returned list.  (Structural modifications are
	 * those that change the size of this list, or otherwise perturb it in such
	 * a fashion that iterations in progress may yield incorrect results.)
	 *
	 * @param fromIndex low endpoint (inclusive) of the subList.
	 * @param toIndex high endpoint (exclusive) of the subList.
	 * @return a view of the specified range within this list.
	 * 
	 * @throws IndexOutOfBoundsException for an illegal endpoint index value
	 *     (fromIndex &lt; 0 || toIndex &gt; size || fromIndex &gt; toIndex).
	 */
	public List subList(int fromIndex, int toIndex) {return list.subList(fromIndex, toIndex);}

}
