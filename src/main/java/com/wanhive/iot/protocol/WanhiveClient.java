/*
 * WanhiveClient.java
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
package com.wanhive.iot.protocol;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketException;

import javax.net.ssl.SSLSocketFactory;

import com.wanhive.iot.protocol.bean.NameInfo;

/**
 * Reference implementation of the Wanhive client
 * 
 * @author amit
 *
 */
public class WanhiveClient implements Client {
	private static final String BAD_MESSAGE = "Invalid message";
	private static final String BAD_CONNECTION = "Invalid connection";
	private Socket socket;

	/**
	 * The default constructor
	 */
	WanhiveClient() {

	}

	/**
	 * Constructor
	 * 
	 * @param socket The socket to use for communication
	 */
	WanhiveClient(Socket socket) {
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
	WanhiveClient(NameInfo host, int timeout, boolean ssl) throws IOException {
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
		if (socket != this.socket) {
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
			if (ssl) {
				socket = SSLSocketFactory.getDefault().createSocket();
			} else {
				socket = new Socket();
			}
			socket.connect(new InetSocketAddress(host.getHost(), Integer.parseInt(host.getService())), timeout);
			setTimeout(timeout);
		} catch (IOException e) {
			close();
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
		} finally {
			socket = null;
		}
	}

	@Override
	public void send(Message message) throws IOException {
		int messageLength = message.header().getLength();
		if (Packet.isValidLength(messageLength)) {
			OutputStream out = socket.getOutputStream();
			out.write(message.getBuffer(), 0, messageLength);
		} else {
			throw new IllegalArgumentException(BAD_MESSAGE);
		}
	}

	@Override
	public Message receive() throws IOException {
		Message message = new Message();
		InputStream in = socket.getInputStream();

		// STEP 1: fetch the header
		int bytes = in.read(message.getBuffer(), 0, Packet.HEADER_SIZE);
		if (bytes != Packet.HEADER_SIZE) {
			throw new EOFException(BAD_CONNECTION);
		}

		// STEP 2: Validate the message length
		short messageLength = message.header().getLength();
		if (!Packet.isValidLength(messageLength)) {
			throw new ProtocolException(BAD_MESSAGE);
		}

		// STEP3: fetch the payload data
		if (messageLength > Packet.HEADER_SIZE) {
			bytes += in.read(message.getBuffer(), bytes, messageLength - Packet.HEADER_SIZE);
		}

		// STEP 4: check the message length
		if (bytes != messageLength) {
			throw new EOFException(BAD_CONNECTION);
		}

		// STEP 5: make the message internally consistent and return
		message.header().setLength(messageLength);
		return message;
	}

	@Override
	public Message receive(short sequenceNumber) throws IOException {
		while (true) {
			Message message = receive();
			if (sequenceNumber == 0 || message.header().getSequenceNumber() == sequenceNumber) {
				return message;
			} else {
				continue;
			}
		}
	}

	@Override
	public Message execute(Message request) throws IOException {
		short sn = request.header().getSequenceNumber();
		send(request);
		return receive(sn);
	}

	@Override
	public void setTimeout(int milliseconds) throws SocketException {
		socket.setSoTimeout(milliseconds);
	}

}
