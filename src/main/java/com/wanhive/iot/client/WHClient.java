/*
 * WHClient.java
 * 
 * Reference implementation of the Wanhive client
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
package com.wanhive.iot.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

import com.wanhive.iot.protocol.Message;
import com.wanhive.iot.protocol.bean.NameInfo;
import com.wanhive.iot.protocol.hosts.Hosts;

/**
 * Reference implementation of the Wanhive client
 * 
 * @author amit
 *
 */
public class WHClient implements Client {
	private static final String BADLENMSG = "Invalid message length";
	private static final String BADSOCKMSG = "Invalid connection";
	private Socket socket;

	/**
	 * Default constructor
	 */
	WHClient() {

	}

	/**
	 * Constructor
	 * 
	 * @param socket The socket to use for communication
	 */
	WHClient(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Opens a new connection with a remote host. Closes any existing connection.
	 * 
	 * @param hosts       For network address resolution of a remote host
	 * @param host        Identifier of the host
	 * @param timeoutMils Read timeout of the underlying connection (0=disable)
	 * @param ssl         If set then a secure SSL connection is established
	 * @throws Exception
	 */
	void open(Hosts hosts, long host, int timeoutMils, boolean ssl) throws Exception {
		try {
			close();
			socket = null;
			NameInfo ni = hosts.get(host);
			if (ssl) {
				SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
				socket = ssf.createSocket(ni.getHost(), Integer.parseInt(ni.getService()));
			} else {
				socket = new Socket(ni.getHost(), Integer.parseInt(ni.getService()));
			}
			socket.setSoTimeout(timeoutMils);
		} catch (Exception e) {
			close();
			socket = null;
			throw e;
		}
	}

	/**
	 * Releases the underlying socket connection
	 * 
	 * @return The Socket connection object
	 */
	Socket release() {
		Socket rv = this.socket;
		this.socket = null;
		return rv;
	}

	/**
	 * Returns the underlying socket
	 * 
	 * @return The Socket connection object
	 */
	Socket getSocket() {
		return this.socket;
	}

	/**
	 * Sets a new socket connection after closing the existing one
	 * 
	 * @param socket The socket connection
	 */
	void setSocket(Socket socket) {
		if (socket == null || socket != this.socket) {
			close();
			this.socket = socket;
		}

	}

	@Override
	public void close() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void send(Message message) throws Exception {
		int messageLength = message.getLength();
		if (messageLength >= Message.HEADER_SIZE && messageLength <= Message.MTU) {
			OutputStream out = socket.getOutputStream();
			out.write(message.getBuffer(), 0, messageLength);
		} else {
			throw new Exception(BADLENMSG);
		}
	}

	@Override
	public Message receive() throws Exception {
		Message message = new Message();

		InputStream in = socket.getInputStream();
		int bytesRead = in.read(message.getBuffer(), 0, Message.HEADER_SIZE);
		if (bytesRead != Message.HEADER_SIZE) {
			throw new Exception(BADSOCKMSG);
		}

		int messageLength = message.getLength();
		if (!Message.isValidLength(messageLength)) {
			throw new Exception(BADLENMSG);
		} else if (messageLength > Message.HEADER_SIZE) {
			bytesRead += in.read(message.getBuffer(), bytesRead, messageLength - Message.HEADER_SIZE);
			if (bytesRead != messageLength) {
				throw new Exception(BADSOCKMSG);
			}
		} else {
			// No payload
		}
		return message;
	}

	@Override
	public Message receive(short sequenceNumber) throws Exception {
		while (true) {
			Message message = receive();
			if (sequenceNumber == 0 || message.getSequenceNumber() == sequenceNumber) {
				return message;
			} else {
				continue;
			}
		}
	}

	@Override
	public void setTimeout(int milliseconds) throws Exception {
		socket.setSoTimeout(milliseconds);
	}

}
