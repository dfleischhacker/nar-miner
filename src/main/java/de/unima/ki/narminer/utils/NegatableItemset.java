package de.unima.ki.narminer.utils;

import java.util.Set;

public class NegatableItemset extends Itemset {
    private boolean isNegated = false;

    public NegatableItemset(boolean negated) {
        isNegated = negated;
    }

    public NegatableItemset(Set<Integer> set, boolean negated) {
        super(set);
        isNegated = negated;
    }
}
