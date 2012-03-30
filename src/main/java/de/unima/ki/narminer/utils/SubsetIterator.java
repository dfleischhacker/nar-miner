package de.unima.ki.narminer.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator producing all possible subsets A and B with A disjoint-union B = itemset
 */
public class SubsetIterator implements Iterator<Itemset[]>{
    private Integer[] elemList;
    private boolean[] partition;
    int numOfPossibilities;
    private int subsetCounter = 0;

    private Itemset nextSetA;
    private Itemset nextSetB;

    public SubsetIterator(Itemset itemset) {
        elemList = itemset.getContent().toArray(new Integer[itemset.size()]);

        partition = new boolean[elemList.length - 1];

        numOfPossibilities = (int) Math.ceil(Math.pow(2, elemList.length - 1));

        initNextItemsets();
    }


    private void initNextItemsets() {
        nextSetA = nextSetB = null;
        while (subsetCounter < numOfPossibilities) {
            nextSetA = new Itemset();
            nextSetA.add(elemList[elemList.length - 1]);
            nextSetB = new Itemset();

            boolean bHasContent = false;
            for (int j = 0; j < partition.length; j++) {
                if (!partition[j]) {
                    nextSetA.add(elemList[j]);
                }
                else {
                    bHasContent = true;
                    nextSetB.add(elemList[j]);
                }
            }

            increment(partition);
            subsetCounter++;
            if (bHasContent) {
                return;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return nextSetA != null;
    }

    @Override
    public Itemset[] next() {
        if (nextSetA == null) {
            throw new NoSuchElementException();
        }

        Itemset[] res = new Itemset[]{nextSetA, nextSetB};
        initNextItemsets();
        return res;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Increments the binary number represented by the given int array by 1.
     *
     * @param number array representing the number to increment
     */
    private static void increment(boolean[] number) {
        int idx = 0;
        boolean carry = true;

        while (idx < number.length) {
            boolean val = number[idx];
            number[idx] = val ^ carry;
            carry = val && carry;
            idx++;
        }
    }
}
