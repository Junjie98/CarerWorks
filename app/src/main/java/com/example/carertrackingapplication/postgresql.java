package com.example.carertrackingapplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class postgresql {
    Connection c = null;
    public Connection connectDB(){
		try {
        Class.forName("org.postgresql.Driver");
        c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/",
                    "postgres", "2600104L");
    } catch (Exception er) {
        System.out.println("Could not find JDBC Driver");
        er.printStackTrace();
    }
		if (c != null) {
        System.out.println("Controlling your database...");
        return c;
    }
		else {
        System.out.println("Failed to establish connection!");
        return c;
		}
    }
}
