package com.huskehhh.mysql.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.huskehhh.mysql.Database;

/**
 * Connects to and uses a MySQL database
 * 
 * @author -_Husky_-
 * @author tips48
 */
public class MySQLDatabase extends Database {
	private final String user;
	private final String database;
	private final String password;
	private final int port;
	private final String hostname;

	/**
	 * Creates a new MySQLDatabase instance
	 *
	 * @param hostname
	 *            Name of the host
	 * @param port
	 *            Port number
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 */
	public MySQLDatabase(String hostname, int port, String username, String password) {
		this(hostname, port, null, username, password);
	}

	/**
	 * Creates a new MySQLDatabase instance for a specific database
	 *
	 * @param hostname
	 *            Name of the host
	 * @param port
	 *            Port number
	 * @param database
	 *            Database name
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 */
	public MySQLDatabase(String hostname, int port, String database, String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
		
		if(this.port < 0 ||this.port > 65535) {
			throw new IllegalArgumentException("The port needs to be between 0 and 65535! (Current port: " + this.port + ")");
		}
	}

	@Override
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		if(checkConnection()) {
			return connection;
		}

		String connectionURL = "jdbc:mysql://" + this.hostname + ":" + this.port;
		
		if(database != null) {
			connectionURL = connectionURL + "/" + this.database;
		}

		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(connectionURL, this.user, this.password);
		
		return connection;
	}
}
