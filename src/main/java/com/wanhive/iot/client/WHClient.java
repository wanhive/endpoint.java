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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketException;

import javax.net.ssl.SSLSocketFactory;

import com.wanhive.iot.protocol.Message;
import com.wanhive.iot.protocol.bean.NameInfo;

/**
 * Reference implementation of the Wanhive client
 * 
 * @author amit
 *
 */
public class WHClient implements Client {
	private Socket socket;

	/**
	 * The default constructor
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
	 * Constructor
	 * 
	 * @param host    The remote host
	 * @param timeout The read timeout in milliseconds (set to 0 to block forever)
	 * @param ssl     Enable or disable secure connection
	 * @throws IOException
	 */
	WHClient(NameInfo host, int timeout, boolean ssl) throws IOException {
		connect(host, timeout, ssl);
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

	/**
	 * Connects to a remote host (closes any existing connection).
	 * 
	 * @param host    The remote host
	 * @param timeout The read timeout in milliseconds (set to 0 to block forever)
	 * @param ssl     Enable or disable secure connection
	 * @throws IOException
	 */
	void connect(NameInfo host, int timeout, boolean ssl) throws IOException {
		try {
			close();
			socket = null;
			if (ssl) {
				SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
				socket = ssf.createSocket(host.getHost(), Integer.parseInt(host.getService()));
			} else {
				socket = new Socket(host.getHost(), Integer.parseInt(host.getService()));
			}
			socket.setSoTimeout(timeout);
		} catch (IOException e) {
			close();
			socket = null;
			throw e;
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
	public void send(Message message) throws IOException {
		int messageLength = message.getLength();
		if (messageLength >= Message.HEADER_SIZE && messageLength <= Message.MTU) {
			OutputStream out = socket.getOutputStream();
			out.write(message.getBuffer(), 0, messageLength);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Message receive() throws IOException {
		Message message = new Message();

		InputStream in = socket.getInputStream();
		int bytesRead = in.read(message.getBuffer(), 0, Message.HEADER_SIZE);
		if (bytesRead != Message.HEADER_SIZE) {
			throw new EOFException();
		}

		int messageLength = message.getLength();
		if (!Message.isValidLength(messageLength)) {
			throw new ProtocolException();
		} else if (messageLength > Message.HEADER_SIZE) {
			bytesRead += in.read(message.getBuffer(), bytesRead, messageLength - Message.HEADER_SIZE);
			if (bytesRead != messageLength) {
				throw new EOFException();
			}
		} else {
			// No payload
		}
		return message;
	}

	@Override
	public Message receive(short sequenceNumber) throws IOException {
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
	public Message execute(Message request) throws IOException {
		short sn = request.getSequenceNumber();
		send(request);
		return receive(sn);
	}

	@Override
	public void setTimeout(int milliseconds) throws SocketException {
		socket.setSoTimeout(milliseconds);
	}

}
