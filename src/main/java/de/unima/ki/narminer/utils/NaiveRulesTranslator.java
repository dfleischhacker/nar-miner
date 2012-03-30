package de.unima.ki.narminer.utils;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translator which is used to determine the name for given ids
 */
public class NaiveRulesTranslator {
    private final static Pattern idPattern = Pattern.compile("^[0-9]+$");

    private IDResolver resolver;
    private int summand;

    public NaiveRulesTranslator(IDResolver resolver, int summand) {
        this.resolver = resolver;
        this.summand = summand;
    }

    /**
     * Returns the name for the given integer ID possibly containing a negation symbol
     *
     * @param id id to resolve
     * @return name for given integer ID possibly containing negation symbol
     */
    public String getName(int id) {
        StringBuilder sb = new StringBuilder();
        if (id >= summand) {
            id -= summand;
            sb.append("￢");
        }
        sb.append(resolver.resolve(id));

        return sb.toString();
    }

    /**
     * Translates a whole rule into the more readable variant
     *
     * @param rule rule string to translate
     */
    public String translateRule(String rule) {
        String[] ruleElements = rule.split(" ");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ruleElements.length; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            String s = ruleElements[i];

            Matcher m = idPattern.matcher(s);
            if (!m.matches()) {
                sb.append(s);
                continue;
            }

            sb.append(getName(Integer.parseInt(s)));
        }

        return sb.toString();
    }

    /**
     * Translates a given rule file and writes the result into the given stream
     */
    public void translateRuleFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        String line;

        while ((line = reader.readLine()) != null) {
            writer.write(translateRule(line));
            writer.newLine();
        }

        writer.flush();
    }
}
