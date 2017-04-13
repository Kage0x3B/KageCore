package com.huskehhh.mysql.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.huskehhh.mysql.Database;

import de.syscy.kagecore.KageCore;

/**
 * Connects to and uses a SQLite database
 *
 * @author tips48
 */
public class SQLiteDatabase extends Database {
	private final String dbLocation;

	/**
	 * Creates a new SQLiteDatabase instance
	 *
	 * @param dbLocation Location of the Database
	 */
	public SQLiteDatabase(String dbLocation) {
		this.dbLocation = !dbLocation.toLowerCase().endsWith(".db") ? dbLocation + ".db" : dbLocation;
	}

	@Override
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		if(checkConnection()) {
			return connection;
		}

		File dataFolder = new File(KageCore.getPluginDirectory().getParentFile(), "pluginDatabases/");

		if(!dataFolder.exists()) {
			dataFolder.mkdirs();
		}

		File file = new File(dataFolder, dbLocation);

		if(!(file.exists())) {
			try {
				file.createNewFile();
			} catch(IOException ex) {
				System.out.println("Unable to create database!");
			}
		}

		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder + "/" + dbLocation);

		return connection;
	}
}