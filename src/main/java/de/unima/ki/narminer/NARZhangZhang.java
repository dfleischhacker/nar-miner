package de.unima.ki.narminer;

import de.unima.ki.narminer.utils.AssociationRule;
import de.unima.ki.narminer.utils.IDResolver;
import de.unima.ki.narminer.utils.Itemset;
import de.unima.ki.narminer.utils.TransactionDatabase;
import org.apache.log4j.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements the negative association rule mining algorithm which has been proposed by Zhang and Zhang in "Association
 * Rule Mining: Models and Algorithms"
 */
public class NARZhangZhang {
    public static final Logger log = Logger.getLogger(NARZhangZhang.class);

    /**
     * Returns the set of interesting itemsets contained in the given transaction database.
     *
     * @param tdb         transaction database to determine itemsets on
     * @param minsupp     minimum support
     * @param mininterest minimum interest
     * @return array containing positive and negative interesting itemset sets
     */
    public Set<de.unima.ki.narminer.utils.Itemset>[] getInterestItemsets(TransactionDatabase tdb, double minsupp, double mininterest) {
        minsupp = getActualSupport(tdb, minsupp);
        Set<Itemset> positiveInterest = new HashSet<Itemset>();
        Set<Itemset> negativeInterest = new HashSet<Itemset>();

        ArrayList<Set<Itemset>> frequent = new ArrayList<Set<Itemset>>();

        // create list of all
        Set<Itemset> prevFrequent = tdb.getFrequent1Itemsets(minsupp);
        frequent.add(0, prevFrequent);

        positiveInterest.addAll(prevFrequent);

        int k = 2;

        Set<Itemset> prevSk;
        Set<Itemset> prevLk = new HashSet<Itemset>(prevFrequent);

        do {
            Set<Itemset> curLk = new HashSet<Itemset>();
            Set<Itemset> curSk = new HashSet<Itemset>();
            Set<Itemset> curFrequent = new HashSet<Itemset>();
            Set<Itemset> doneList = new HashSet<Itemset>();

            for (int firstI = 0; firstI < frequent.size(); firstI++) {
                for (int secondI = 0; secondI <= firstI; secondI++) {
                    for (Itemset f1 : frequent.get(firstI)) {
                        for (Itemset f2 : frequent.get(secondI)) {
                            Itemset union = f1.union(f2);

                            if (union.size() == k) {
                                if (doneList.contains(union)) {
                                    continue;
                                }
                                doneList.add(union);
                                // only consider sets which have at least a subset contained in Lk-1
                                boolean considerUnion = false;
                                for (Itemset subset : prevLk) {
                                    if (union.containsAll(subset)) {
                                        considerUnion = true;
                                        break;
                                    }
                                }

                                if (!considerUnion) {
                                    continue;
                                }

                                // sort in lk and nk
                                double supp = tdb.getSupport(union);
                                if (supp >= minsupp) {
                                    log.debug("Pos: " + toString(union) + " -- " + supp);
                                    curFrequent.add(union);
                                    // is itemset interesting?
                                    if (tdb.isInteresting(union, mininterest)) {
                                        curLk.add(union);
                                    }
                                }
                                else {
                                    log.debug("Neg: " + toString(union) + " -- " + supp);
                                    // is itemset interesting
                                    if (tdb.isInteresting(union, mininterest)) {
                                        curSk.add(union);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            positiveInterest.addAll(curLk);
            negativeInterest.addAll(curSk);
            prevLk = curLk;
            prevSk = curSk;
            frequent.add(curFrequent);
            log.info("Content Lk for k = " + k);
            for (Itemset set : prevLk) {
                log.info(set.toString(IDResolver.getResolver()));
            }
            log.info("Content Sk for k = " + k);
            for (Itemset set : prevSk) {
                log.info(set.toString(IDResolver.getResolver()));
            }

            k++;
        } while (!prevLk.isEmpty() && !prevSk.isEmpty());

        Set<Itemset>[] res = (Set<Itemset>[]) Array.newInstance(Set.class, 2);
        res[0] = positiveInterest;
        res[1] = negativeInterest;

        return res;
    }

    /**
     * Applies the RP model algorithm for harvesting association rules which has been introduced by Zhang and Zhang
     *
     * @param tdb         transaction database
     * @param minsupp     minimum support value
     * @param minconf     minimum confidence value
     * @param mininterest minimum interest value
     * @return array of sets of positive and negative association rules
     */
    public Set<AssociationRule>[] performPRModel(TransactionDatabase tdb, double minsupp, double minconf, double mininterest) {
        minsupp = getActualSupport(tdb, minsupp);
        Set<Itemset>[] interestItemsets = getInterestItemsets(tdb, minsupp, mininterest);

        HashSet<AssociationRule> positiveRules = new HashSet<AssociationRule>();
        HashSet<AssociationRule> negativeRules = new HashSet<AssociationRule>();

        Set<Itemset> pl = interestItemsets[0];
        Set<Itemset> nl = interestItemsets[1];

        /*
         * Generate positive association rules
         */
        for (Itemset a : pl) {
            if (a.size() <= 1) {
                continue;
            }
            Iterator<Itemset[]> subsetIt = a.getSubsetIterator();
            double supportVal = tdb.getSupport(a);

            while (subsetIt.hasNext()) {
                Itemset[] subsets = subsetIt.next();
                log.debug("Subset: ");
                log.debug(a.toString(IDResolver.getResolver()) + "[" +
                          subsets[0].toString(IDResolver.getResolver()) + ", " +
                          subsets[1].toString(IDResolver.getResolver()) + "]");
                Itemset x = subsets[0];
                Itemset y = subsets[1];

                double interestVal = tdb.getInterestValue(a, x, y);

                if (interestVal < mininterest) {
                    continue;
                }

                double confVal = computePR(tdb, a, y, x);
                if (confVal >= minconf) {
                    AssociationRule rule = new AssociationRule(x, y);
                    rule.setConfidence(confVal);
                    rule.setSupport(supportVal);

                    positiveRules.add(rule);
                }

                //noinspection SuspiciousNameCombination
                confVal = computePR(tdb, a, x, y);
                if (confVal >= minconf) {
                    AssociationRule rule = new AssociationRule(y, x);
                    rule.setConfidence(confVal);
                    rule.setSupport(supportVal);

                    positiveRules.add(rule);
                }
            }
        }

        /*
         * Generate negative association rules
         */
        for (Itemset a : nl) {
            Iterator<Itemset[]> subsetIt = a.getSubsetIterator();


            while (subsetIt.hasNext()) {
                Itemset[] subsets = subsetIt.next();
                Itemset x = subsets[0];
                Itemset y = subsets[1];

                double suppX = tdb.getSupport(x);
                double suppY = tdb.getSupport(y);

                if (suppX < minsupp || suppY < minsupp) {
                    continue;
                }

                log.debug("---------------------------------");
                log.debug("X: " + x.toString(IDResolver.getResolver()) + ", Y: " +
                          y.toString(IDResolver.getResolver()));
                log.debug("not X --> Y or Y --> not X");
                // generate rules not X --> Y and Y --> not X
                double supportVal = tdb.getSupport(x, true, y, false);
                log.debug("Support: " + supportVal);
                if (supportVal >= minsupp) {
                    double interest = tdb.getInterest(x, true, y, false);
                    log.debug("Interest: " + interest);
                    if (interest >= mininterest) {
                        double confidenceVal = computePR(tdb, a, y, false, x, true);
                        log.debug("Confidence: " + confidenceVal);
                        if (confidenceVal >= minconf) {
                            AssociationRule rule = new AssociationRule(x, true, y, false);
                            rule.setConfidence(confidenceVal);

                            rule.setSupport(tdb.getConditionalSupport(x, true, y, false));
                            negativeRules.add(rule);
                        }
                        confidenceVal = computePR(tdb, a, y, true, x, false);
                        log.debug("Confidence: " + confidenceVal);
                        if (confidenceVal >= minconf) {
                            AssociationRule rule = new AssociationRule(x, false, y, true);
                            rule.setConfidence(confidenceVal);
                            rule.setSupport(supportVal);
                            negativeRules.add(rule);
                        }
                    }
                }

                // generate rules not X --> not Y and not Y --> not X
                log.debug("not X --> not Y or not Y --> not X");
                supportVal = tdb.getSupport(x, true, y, true);
                log.debug("Support: " + supportVal);
                if (supportVal >= minsupp) {
                    double interestVal = tdb.getInterest(x, true, y, true);
                    log.debug("Interest: " + interestVal);
                    if (interestVal >= mininterest) {
                        double confidenceVal = computePR(tdb, a, y, true, x, true);
                        log.debug("Confidence: " + confidenceVal);
                        if (confidenceVal >= minconf) {
                            AssociationRule rule = new AssociationRule(y, true, x, true);
                            rule.setSupport(tdb.getConditionalSupport(x, true, y, true));
                            rule.setConfidence(confidenceVal);
                            negativeRules.add(rule);
                        }

                        confidenceVal = computePR(tdb, a, x, false, y, false);
                        log.debug("Confidence: " + confidenceVal);
                        if (confidenceVal >= minconf) {
                            AssociationRule rule = new AssociationRule(x, true, y, true);
                            rule.setConfidence(confidenceVal);
                            rule.setSupport(supportVal);
                            negativeRules.add(rule);
                        }
                    }
                }
            }
        }

        HashSet<AssociationRule>[] arr = (HashSet<AssociationRule>[]) Array.newInstance(HashSet.class, 2);
        arr[0] = positiveRules;
        arr[1] = negativeRules;
        return arr;
    }

    /**
     * Computes the probability ratio PR(Y|X) for the given itemsets X and Y.
     *
     * @param y
     * @param x
     * @return
     */
    private double computePR(TransactionDatabase tdb, Itemset union, Itemset y, Itemset x) {
        double suppY = tdb.getSupport(y);
        double suppX = tdb.getSupport(x);
        double suppUnion = tdb.getSupport(union);

        if ((suppUnion >= suppX * suppY) && (suppX * (1 - suppY) != 0)) {
            return (suppUnion - suppX * suppY) / (suppX * (1 - suppY));
        }
        else if ((suppUnion < suppX * suppY) && (suppX * suppY != 0)) {
            return (suppUnion - suppX * suppY) / (suppX * suppY);
        }

        throw new RuntimeException("computePR: neither case matches!");
    }

    /**
     * Computes the probability ratio PR(Y|X) considering negation.
     *
     * @param tdb
     * @param union
     * @param y
     * @param negatedY
     * @param x
     * @param negatedX
     * @return
     */
    private double computePR(TransactionDatabase tdb, Itemset union, Itemset y, boolean negatedY, Itemset x, boolean negatedX) {
        double suppY = tdb.getSupport(y);

        if (negatedY) {
            suppY = 1 - suppY;
        }

        double suppX = tdb.getSupport(x);

        if (negatedX) {
            suppX = 1 - suppX;
        }

        double suppUnion = tdb.getSupport(y, negatedY, x, negatedX);

        if ((suppUnion >= suppX * suppY) && (suppX * (1 - suppY) != 0)) {
            return (suppUnion - suppX * suppY) / (suppX * (1 - suppY));
        }
        else if ((suppUnion < suppX * suppY) && (suppX * suppY != 0)) {
            return (suppUnion - suppX * suppY) / (suppX * suppY);
        }

        throw new RuntimeException("computePR: neither case matches!");
    }

    /**
     * Converts the given support value to the actual support value which means that negative values are treated as
     * absolute numbers of instances and converted to the corresponding relative values
     *
     * @param tdb          transaction database to use
     * @param supportInput support value given by the user, negative for being interpreted as absolute value
     * @return actual support value to use in the algorithm
     */
    public double getActualSupport(TransactionDatabase tdb, double supportInput) {
        if (supportInput >= 0) {
            System.out.println("Setting support to " + supportInput);
            return supportInput;
        }
        System.out.println("Setting support to " + -supportInput / tdb.getNumberOfTransactions());
        return -supportInput / tdb.getNumberOfTransactions();
    }

    public static String toString(Iterable<Integer> set) {
        IDResolver resolver = IDResolver.getResolver();
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for (int i : set) {
            sb.append(resolver.resolve(i)).append(", ");
        }

        sb.append("}");

        return sb.toString();
    }

}
