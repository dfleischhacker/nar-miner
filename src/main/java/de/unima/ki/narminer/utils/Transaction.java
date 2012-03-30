package de.unima.ki.narminer.utils;

import java.util.Set;
import java.util.SortedSet;

/**
 * Represents a transaction which contains an arbitrary number of items
 */
public class Transaction extends Itemset {
    public Transaction() {
    }

    public Transaction(Set<Integer> integers) {
        super(integers);
    }

    public Transaction(SortedSet<Integer> integers) {
        super(integers);
    }
}
