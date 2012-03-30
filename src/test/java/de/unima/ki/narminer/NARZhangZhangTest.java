package de.unima.ki.narminer;

import de.unima.ki.narminer.utils.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

public class NARZhangZhangTest {
    @Ignore
    @Test
    public void testGetInterestItemsets() throws Exception {
        TransactionDatabase tdb = new TransactionDatabase(
            new FileInputStream(
                "/home/daniel/Repositories/negation/utilities/src/de/unima/ki/daniel/de.unima.ki.narminer/tests/transactiondb1.txt"));
        NARZhangZhang nzz = new NARZhangZhang();
        Set<Itemset>[] res = nzz.getInterestItemsets(tdb, 0.3, 0.07);

        Set<Itemset> pl = res[0];
        Set<Itemset> nl = res[1];

        System.out.println("Positive interesting ------------------------");

        for (Itemset i : pl) {
            System.out.println(i.toString(IDResolver.getResolver()));
        }

        System.out.println("Negative interesting ------------------------");

        for (Itemset i : nl) {
            System.out.println(i.toString(IDResolver.getResolver()));
        }
    }

    @Ignore
    @Test
    public void testPerformPRModel() throws IOException, SQLException {
        TransactionDatabase tdb = new TransactionDatabase(
            new FileInputStream(
                "/home/daniel/dbpedia_instanceof.txt"));
//                "/home/daniel/Repositories/negation/utilities/src/de/unima/ki/daniel/narminer/tests/transactiondb1.txt"));
        NARZhangZhang nzz = new NARZhangZhang();

        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/daniel/association-rules-05conf.txt"));

        IDResolver.setResolver(new IDLookup(IDLookup.EDE_DBSTRING));

        Set<AssociationRule>[] rules = nzz.performPRModel(tdb, -10, 0.5, 0);

        writer.write("Transaction database");
        writer.newLine();
        writer.write(tdb.getStatistics());
        writer.newLine();
        writer.write("Positive rules");
        writer.newLine();
        for (AssociationRule rule : rules[0]) {
            writer.write(rule.toString(IDResolver.getResolver()));
            writer.newLine();
        }

        writer.write("Negative rules");
        writer.newLine();
        for (AssociationRule rule : rules[1]) {
            writer.write(rule.toString(IDResolver.getResolver()));
            writer.newLine();
        }

        writer.close();
    }
}
