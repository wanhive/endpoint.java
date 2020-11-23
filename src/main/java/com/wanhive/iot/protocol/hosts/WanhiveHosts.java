/*
 * WanhiveHosts.java
 * 
 * Hosts database manager
 * 
 * This program is part of Wanhive IoT Platform.
 * 
 * Apache-2.0 License
 * Copyright 2020 Wanhive Systems Private Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.wanhive.iot.protocol.hosts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import com.wanhive.iot.protocol.bean.NameInfo;

/**
 * Reference implementation of hosts manager based on SQLITE3 database
 * 
 * @author amit
 *
 */
public class WanhiveHosts implements Hosts {
	private final Connection conn;
	static {
		init();
	}

	private static void init() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {

		}
	}

	private void createTables() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS hosts (uid INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE, name TEXT NOT NULL DEFAULT '127.0.0.1', service TEXT NOT NULL DEFAULT '9000', type INTEGER NOT NULL DEFAULT 0)";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.execute();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param db Pathname of the sqlite3 database file
	 * @throws Exception
	 */
	public WanhiveHosts(String db) throws Exception {
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + db);
			createTables();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Imports hosts from a text file into the database
	 * 
	 * @param pathname Pathname of the text file
	 * @throws Exception
	 */
	public void importHosts(String pathname) throws Exception {
		String query = "INSERT INTO hosts (uid, name, service, type) VALUES (?,?,?,?)";
		try (BufferedReader reader = new BufferedReader(new FileReader(pathname))) {
			boolean commitFlag = conn.getAutoCommit();
			try {
				conn.setAutoCommit(false);
				try (PreparedStatement ps = conn.prepareStatement(query)) {
					while (true) {
						String line = reader.readLine();
						if (line == null) {
							break;
						}

						line = line.trim();
						if (line.length() == 0) {
							continue;
						}
						
						String[] data = line.split("\t");
						if (data == null || data.length < 3) {
							continue;
						}

						ps.clearParameters();
						ps.setLong(1, Long.parseLong(data[0]));
						ps.setString(2, data[1]);
						ps.setString(3, data[2]);
						if (data.length == 4) {
							ps.setInt(4, Integer.parseInt(data[3]));
						} else {
							ps.setInt(4, 0);
						}
						ps.executeUpdate();

					}
				}
				conn.commit();
			} finally {
				conn.setAutoCommit(commitFlag);
			}
		}
	}

	/**
	 * Exports the hosts database to the given text file
	 * 
	 * @param pathname Pathname of the text file
	 * @throws Exception
	 */
	public void exportHosts(String pathname) throws Exception {
		String query = "SELECT uid, name, service, type from hosts";

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathname))) {
			try (PreparedStatement ps = conn.prepareStatement(query)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						writer.write(rs.getString(1));
						writer.write("\t");
						writer.write(rs.getString(2));
						writer.write("\t");
						writer.write(rs.getString(3));
						writer.write("\n");
					}
				}
			}
		}
	}

	@Override
	public NameInfo get(long identity) throws Exception {
		String query = "SELECT name, service, type FROM hosts where uid=?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setLong(1, identity);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					NameInfo ni = new NameInfo();
					ni.setHost(rs.getString(1));
					ni.setService(rs.getString(2));
					ni.setType(rs.getInt(3));
					return ni;
				} else {
					throw new Exception("Not found");
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void put(long identity, NameInfo ni) throws Exception {
		String query = "INSERT INTO hosts (uid, name, service, type) VALUES (?,?,?,?)";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setLong(1, identity);
			ps.setString(2, ni.getHost());
			ps.setString(3, ni.getService());
			ps.setInt(4, ni.getType());
			ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void remove(long identity) throws Exception {
		String query = "DELETE FROM hosts where uid=?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setLong(1, identity);
			ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public long[] list(int type, int limit) throws Exception {
		if (limit <= 0) {
			limit = 1;
		}

		String query = "SELECT uid FROM hosts where type=? order by RANDOM() limit ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, type);
			ps.setInt(2, limit);

			try (ResultSet rs = ps.executeQuery()) {
				long[] list = new long[limit];
				int i = 0;
				while (rs.next()) {
					list[i++] = rs.getLong(1);
				}

				if (i == limit) {
					return list;
				} else {
					return Arrays.copyOfRange(list, 0, i);
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void close() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {

		}
	}
}
