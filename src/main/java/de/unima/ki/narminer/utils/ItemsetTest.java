package de.unima.ki.narminer.utils;

import org.junit.Test;

public class ItemsetTest {
    @Test
    public void testUnion() throws Exception {
        Itemset i1 = new Itemset();
        i1.add(1);
        i1.add(5);
        i1.add(4);
        i1.add(6);
        i1.add(13);
        i1.add(9);

        for (int i : i1) {
            System.out.println(i);
        }

        System.out.println("-----");

        Itemset i2 = new Itemset();
        i2.add(1);
        i2.add(5);
        i2.add(4);
        i2.add(6);
        i2.add(0);
        i2.add(9);
        i2.add(9);

        for (int i : i2) {
            System.out.println(i);
        }

        System.out.println("-----");

        Itemset union = i1.union(i2);

        for (int i : union) {
            System.out.println(i);
        }
    }
}
