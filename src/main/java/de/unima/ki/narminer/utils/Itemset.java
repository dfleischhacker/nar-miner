package de.unima.ki.narminer.utils;

import java.util.*;

/**
 * Itemset trying to reach higher performance than the original HashSet based implementation
 */
public class Itemset implements Iterable<Integer> {
    private ArrayList<Integer> content;


    /**
     * Creates an empty itemset
     */
    public Itemset() {
        content = new ArrayList<Integer>();
    }

    /**
     * Initializes the itemset with the given set of items
     *
     * @param set set containing items to initialize itemset with
     */
    public Itemset(Set<Integer> set) {
        content = new ArrayList<Integer>(set);
        Collections.sort(content);
    }

    /**
     * Initializes the itemset using the given list
     *
     * @param list list to use internally
     */
    private Itemset(ArrayList<Integer> list) {
        content = list;
    }

    /**
     * Creates a new itemset only containing the given values
     *
     * @param val value to add to newly created itemset
     * @return itemset containing only the given value
     */
    public static Itemset singleton(int val) {
        Itemset res = new Itemset();
        res.add(val);
        return res;
    }

    /**
     * Adds the given value to this itemset ensuring the ordering and uniqueness of values.
     * <p/>
     * Sorting is based on insertion sort
     *
     * @param val value to add to itemset
     */
    public void add(Integer val) {
        int pos = 0;

        while (pos < content.size() && content.get(pos) <= val) {
            // element already contained in itemset
            if (content.get(pos).equals(val)) {
                return;
            }

            pos++;
        }

        content.add(pos, val);
    }

    /**
     * Returns the size of this itemset
     *
     * @return size of this itemset
     */
    public int size() {
        return content.size();
    }

    /**
     * Returns a new itemset being the union of this and the given itemset
     *
     * @param other second itemset
     * @return return new itemset being the union of this and the given one
     */
    public Itemset union(Itemset other) {
        ArrayList<Integer> thisContent = this.content;
        ArrayList<Integer> otherContent = other.content;

        ArrayList<Integer> result = new ArrayList<Integer>();

        int thisPos = 0;
        int otherPos = 0;

        while (otherPos < otherContent.size() && thisPos < thisContent.size()) {
            int largerVal = thisContent.get(thisPos);
            int smallerVal = otherContent.get(otherPos);
            if (smallerVal < largerVal) {
                result.add(smallerVal);
                otherPos++;
            }
            else if (smallerVal > largerVal) {
                result.add(largerVal);
                thisPos++;
            }
            else {
                result.add(largerVal);
                otherPos++;
                thisPos++;
            }
        }

        while (otherPos < otherContent.size()) {
            result.add(otherContent.get(otherPos++));
        }

        while (thisPos < thisContent.size()) {
            result.add(thisContent.get(thisPos++));
        }

        return new Itemset(result);
    }

    /**
     * Returns true if the given itemset contains all items this itemset contains
     *
     * @param other itemset to compare this one to
     * @return true if the itemsets are equal otherwise false
     */
    public boolean containsAll(Itemset other) {
        ArrayList<Integer> thisContent = this.content;
        ArrayList<Integer> otherContent = other.content;

        int thisPos = 0;
        int otherPos = 0;

        while (otherPos < otherContent.size() && thisPos < thisContent.size()) {
            int thisVal = thisContent.get(thisPos);
            int otherVal = otherContent.get(otherPos);
            if (otherVal < thisVal) {
                return false;
            }
            else if (otherVal > thisVal) {
                thisPos++;
            }
            else {
                thisPos++;
                otherPos++;
            }
        }

        if (otherPos < other.size()) {
            return false;
        }

        return true;
    }

    /**
     * Returns the intersection of this itemsset with the given itemset
     *
     * @param other itemset to return intersection
     * @return intersection of this itemset with given other one
     */
    public Itemset intersect(Itemset other) {
        ArrayList<Integer> thisContent = this.content;
        ArrayList<Integer> otherContent = other.content;

        ArrayList<Integer> result = new ArrayList<Integer>();

        int thisPos = 0;
        int otherPos = 0;

        while (otherPos < otherContent.size() && thisPos < thisContent.size()) {
            int largerVal = thisContent.get(thisPos);
            int smallerVal = otherContent.get(otherPos);
            if (smallerVal < largerVal) {
                otherPos++;
            }
            else if (smallerVal > largerVal) {
                thisPos++;
            }
            else {
                result.add(largerVal);
                otherPos++;
                thisPos++;
            }
        }

        return new Itemset(result);
    }

    /**
     * Returns the string representation of this itemset
     *
     * @return string representation of this itemset
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (Integer val : content) {
            sb.append(val).append(", ");
        }

        sb.append("}");

        return sb.toString();
    }

    /**
     * Removes all items contained in the given itemset from this itemset and returns the resulting itemset
     *
     * @param other itemset with items to remove from this itemset
     * @return resulting itemset
     */
    public Itemset minus(Itemset other) {
        ArrayList<Integer> thisContent = this.content;
        ArrayList<Integer> otherContent = other.content;

        ArrayList<Integer> result = new ArrayList<Integer>();

        int thisPos = 0;
        int otherPos = 0;

        while (otherPos < otherContent.size() && thisPos < thisContent.size()) {
            int thisVal = thisContent.get(thisPos);
            int otherVal = otherContent.get(otherPos);
            if (otherVal < thisVal) {
                otherPos++;
            }
            else if (otherVal > thisVal) {
                result.add(thisVal);
                thisPos++;
            }
            else {
                otherPos++;
                thisPos++;
            }
        }

        return new Itemset(result);
    }

    /**
     * Returns all elements contained in this itemset with the IDs replaced by the names get from the resolver.
     *
     * @param resolver resolver used to resolve the IDs
     * @return contents of this itemset
     */
    public String toString(IDResolver resolver) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (Integer val : content) {
            sb.append(resolver.resolve(val)).append(", ");
        }

        sb.append("}");

        return sb.toString();
    }

    /**
     * Returns if the given item is contained in this itemset
     *
     * @param item item to check for
     * @return true if item contained in itemset
     */
    public boolean contains(Integer item) {
        for (Integer curItem : content) {
            if (curItem == item) {
                return true;
            }
            if (curItem < item) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Itemset itemsetHP = (Itemset) o;

        if (content != null ? !content.equals(itemsetHP.content) : itemsetHP.content != null) {
            return false;
        }

        return true;
    }

    /**
     * Returns the internal array list
     *
     * @return internal array list
     */
    public List<Integer> getContent() {
        return Collections.unmodifiableList(content);
    }

    /**
     * Returns an iterator over all possible subsets A and B with A disjoint-union B = itemset
     *
     * @return iterator over 
     */
    public Iterator<Itemset[]> getSubsetIterator() {
        return new SubsetIterator(this);
    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }

    @Override
    public Iterator<Integer> iterator() {
        return content.iterator();
    }
}
