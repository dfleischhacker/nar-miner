package de.unima.ki.narminer;

import de.unima.ki.narminer.utils.AssociationRule;
import de.unima.ki.narminer.utils.IDLookup;
import de.unima.ki.narminer.utils.IDResolver;
import de.unima.ki.narminer.utils.TransactionDatabase;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 * Command line starter class for NAR-miner
 */
public class CommandLine {
    public static final HashMap<String, Class> AVAILABLE_ALGORITHMS = new HashMap<String, Class>();

    static {
        AVAILABLE_ALGORITHMS.put("zhangzhang", NARZhangZhang.class);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Provide properties file with configuration as parameter.");
            System.exit(1);
        }
        Properties properties = new Properties();
        FileReader propertiesReader = null;
        try {
            propertiesReader = new FileReader(args[0]);
        }
        catch (FileNotFoundException e) {
            System.err.println("No properties file \"" + args[0] + "\" found!");
            System.exit(2);
            return;
        }
        try {
            properties.load(propertiesReader);
        }
        catch (IOException e) {
            System.err.println("Unable to load properties file. Reason: " + e.getMessage());
            System.exit(3);
            return;
        }

        String algorithm = properties.getProperty("algorithm").toLowerCase();

        if (!AVAILABLE_ALGORITHMS.containsKey(algorithm)) {
            System.out.println("No algorithm named \"" + algorithm + "\" available!");
            System.exit(4);
        }

        NARZhangZhang algorithmImpl = new NARZhangZhang();

        TransactionDatabase tdb = null;
        try {
            tdb = new TransactionDatabase(new FileInputStream(properties.getProperty("transaction_table")));
        }
        catch (IOException e) {
            System.err.println("Error reading transaction table: " + e.getMessage());
            System.exit(5);
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(properties.getProperty("output_file")));
        }
        catch (IOException e) {
            System.err.println("Unable to create output file: " + e.getMessage());
            System.exit(6);
        }

        try {
            IDResolver.setResolver(new IDLookup(properties.getProperty("id_database")));
        }
        catch (SQLException e) {
            System.err.println("Unable to connect to ID database: " + e.getMessage());
            System.exit(7);
        }

        Set<AssociationRule>[] rules = algorithmImpl
            .performPRModel(tdb, Double.parseDouble(properties.getProperty("minsupport")),
                            Double.parseDouble(properties.getProperty("minconfidence")),
                            Double.parseDouble(properties.getProperty("mininterest")));

        boolean filterTwoItems = properties.getProperty("only_two_items").equals("true");
        
        try {
            writer.write("Transaction database");
            writer.newLine();
            writer.write(tdb.getStatistics());
            writer.newLine();
            writer.write("Positive rules");
            writer.newLine();
            for (AssociationRule rule : rules[0]) {
                if (filterTwoItems && rule.getUnion().size() != 2) {
                    continue;
                }
                writer.write(rule.toString(IDResolver.getResolver()));
                writer.newLine();
            }

            writer.write("Negative rules");
            writer.newLine();
            for (AssociationRule rule : rules[1]) {
                if (filterTwoItems && rule.getUnion().size() != 2) {
                    continue;
                }
                writer.write(rule.toString(IDResolver.getResolver()));
                writer.newLine();
            }

            writer.close();
        }
        catch (IOException ex) {
            System.err.println("Unable to write output to file: " + ex.getMessage());
            System.exit(8);
        }
    }
}
