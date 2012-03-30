package de.unima.ki.narminer.utils;

import java.util.Set;

/**
 * Represents an association rule consisting of antecedent and consequent
 */
public class AssociationRule {
    private Itemset antecedent;
    private Itemset consequent;

    private boolean negateAntecedent;
    private boolean negateConsequent;

    private Itemset union;
    private Itemset sect;

    private double confidence;
    private double support;

    /**
     * Initializes the association rule with the given antecedent and the given consequent
     * @param antecedent antecedent of this association rule
     * @param consequent consequent of this association rule
     */
    public AssociationRule(Itemset antecedent, Itemset consequent) {
        this.antecedent = antecedent;
        this.consequent = consequent;

        this.union = this.antecedent.union(this.consequent);

        this.sect = this.antecedent.intersect(this.consequent);
    }

    /**
     *
     */
    public AssociationRule(Itemset antecedent, boolean negateAntecedent, Itemset consequent, boolean negateConsequent) {
        this(antecedent, consequent);
        this.negateAntecedent = negateAntecedent;
        this.negateConsequent = negateConsequent;
    }

    public AssociationRule(Set<Integer> x, Set<Integer> y) {
        this(new Itemset(x), new Itemset(y));
    }

    /**
     * Returns the unmodifiable set of items the antecedent consists of
     * @return items of antecedent
     */
    public Itemset getAntecedent() {
        return antecedent;
    }

    /**
     * Returns the unmodifiable set of items the consequent consists of
     * @return items of consequent
     */
    public Itemset getConsequent() {
        return consequent;
    }

    /**
     * Returns the set containing all items from consequent and antecedent.
     *
     * @return union of items from antecedent and consequent
     */
    public Itemset getUnion() {
        return union;
    }

    /**
     * Returns the set of items contained in both antecedent and consequent.
     *
     * @return intersection of antecedent and consequent itemsets
     */
    public Itemset getSect() {
        return sect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssociationRule that = (AssociationRule) o;

        if (negateAntecedent != that.negateAntecedent) {
            return false;
        }
        if (negateConsequent != that.negateConsequent) {
            return false;
        }
        if (antecedent != null ? !antecedent.equals(that.antecedent) : that.antecedent != null) {
            return false;
        }
        if (consequent != null ? !consequent.equals(that.consequent) : that.consequent != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = antecedent != null ? antecedent.hashCode() : 0;
        result = 31 * result + (consequent != null ? consequent.hashCode() : 0);
        result = 31 * result + (negateAntecedent ? 1 : 0);
        result = 31 * result + (negateConsequent ? 1 : 0);
        return result;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public double getSupport() {
        return support;
    }

    public void setSupport(double support) {
        this.support = support;
    }

    /**
     * Returns if the antecedent is negated
     * @return true if the antecedent is negated, otherwise false
     */
    public boolean isAntecedentNegated() {
        return negateAntecedent;
    }

    /**
     * Returns if the consequent is negated
     * @return true if the consequent is negated, otherwise false
     */
    public boolean isConsequentNegated() {
        return negateConsequent;
    }

    /**
     * Returns the string representation of the association rule
     *
     * @return string representation of this association rule
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (negateAntecedent) {
            sb.append('￢');
        }
        sb.append(antecedent);
        sb.append(" --> ");
        if (negateConsequent) {
            sb.append('￢');
        }
        sb.append(consequent);

        return sb.toString();
    }

    /**
     * Returns the string representation of the association rule
     *
     * @param resolver resolver to resolve ids to names
     * @return string representation of this association rule
     */
    public String toString(IDResolver resolver) {
        StringBuilder sb = new StringBuilder();
        if (negateAntecedent) {
            sb.append('￢');
        }
        sb.append(antecedent.toString(resolver));
        sb.append(" --> ");
        if (negateConsequent) {
            sb.append('￢');
        }
        sb.append(consequent.toString(resolver));

        sb.append(" (conf: ").append(confidence).append(", supp: ").append(support).append(")");

        return sb.toString();
    }
}
