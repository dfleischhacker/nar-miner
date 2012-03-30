package de.unima.ki.narminer.utils;

import java.sql.*;
import java.util.HashMap;

/**
 * Provides methods to resolve IDs to their corresponding names and URIs
 */
public class IDLookup extends IDResolver {
    private HashMap<Integer,String> cache;

    static {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        }
        catch (SQLException e) {
            System.out.println("Unable to initialize MySQL driver");
        }
    }

    private PreparedStatement classNameStmt;
    private PreparedStatement classURIStmt;
    private PreparedStatement propNameStmt;
    private PreparedStatement propURIStmt;
    private PreparedStatement individualNameStmt;
    private PreparedStatement individualURIStmt;

    public IDLookup(String dbString) throws SQLException {
        Connection conn = DriverManager.getConnection(dbString);
        classNameStmt = conn.prepareStatement("SELECT `name` FROM `classes` WHERE id = ?");
        classURIStmt = conn.prepareStatement("SELECT `uri` FROM `classes` WHERE id = ?");
        propNameStmt = conn.prepareStatement("SELECT `name` FROM `properties` WHERE id = ?");
        propURIStmt = conn.prepareStatement("SELECT `uri` FROM `properties` WHERE id = ?");
        individualNameStmt = conn.prepareStatement("SELECT `name` FROM `individuals` WHERE id = ?");
        individualURIStmt = conn.prepareStatement("SELECT `uri` FROM `individuals` WHERE id = ?");
        cache = new HashMap<Integer, String>();
    }

    public String resolve(int id) {
        try {
            if (!cache.containsKey(id)) {
                cache.put(id, lookupClassName(id));
            }
            return cache.get(id);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the class name for the given class id
     *
     * @param id id to lookup in database
     * @return class name for given id
     */
    public String lookupClassName(int id) throws SQLException {
        classNameStmt.setInt(1, id);
        ResultSet res = classNameStmt.executeQuery();

        if (res.next()) {
            return res.getString(1);
        }

        return null;
    }

    /**
     * Returns the class URI for the given class id
     *
     * @param id id to lookup in database
     * @return class URI for given id
     */
    public String lookupClassURI(int id) throws SQLException {
        classURIStmt.setInt(1, id);
        ResultSet res = classURIStmt.executeQuery();

        if (res.next()) {
            return res.getString(1);
        }

        return null;
    }

    /**
     * Returns the property name for the given property id
     *
     * @param id id to lookup in database
     * @return property name for given id
     */
    public String lookupPropertyName(int id) throws SQLException {
        propNameStmt.setInt(1, id);
        ResultSet res = propNameStmt.executeQuery();

        if (res.next()) {
            return res.getString(1);
        }

        return null;
    }

    /**
     * Returns the property URI for the given property id
     *
     * @param id id to lookup in database
     * @return property URI for given id
     */
    public String lookupPropertyURI(int id) throws SQLException {
        propURIStmt.setInt(1, id);
        ResultSet res = propURIStmt.executeQuery();

        if (res.next()) {
            return res.getString(1);
        }

        return null;
    }

    /**
     * Returns the individual name for the given individual id
     *
     * @param id id to lookup in database
     * @return individual name for given id
     */
    public String lookupIndividualName(int id) throws SQLException {
        individualNameStmt.setInt(1, id);
        ResultSet res = individualNameStmt.executeQuery();

        if (res.next()) {
            return res.getString(1);
        }

        return null;
    }

    /**
     * Returns the individual URI for the given individual id
     *
     * @param id id to lookup in database
     * @return individual URI for given id
     */
    public String lookupIndividualURI(int id) throws SQLException {
        individualURIStmt.setInt(1, id);
        ResultSet res = individualURIStmt.executeQuery();

        if (res.next()) {
            return res.getString(1);
        }

        return null;
    }
}
