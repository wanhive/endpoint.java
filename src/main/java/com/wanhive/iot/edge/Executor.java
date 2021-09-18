/*
 * Executor.java
 * 
 * Executes a Wanhive client application
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
package com.wanhive.iot.edge;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.wanhive.iot.protocol.Client;
import com.wanhive.iot.protocol.Message;

/**
 * Threaded executor for the Wanhive client applications. Uses two bounded
 * queues, one for the outgoing messages and another one for the incoming
 * messages.
 * 
 * @author amit
 *
 */
public class Executor implements Runnable, AutoCloseable {
	private static final String BAD_REQUEST = "Not allowed";
	private final Object notifier = new Object();
	private boolean running = false; // Condition variable
	private final AtomicBoolean stopped = new AtomicBoolean(true); // Status flag

	private Client client;
	private Receiver receiver;
	private Message outgoing;
	private final BlockingQueue<Message> in;
	private final BlockingQueue<Message> out;

	/**
	 * Stops the Executor and closes the Client.
	 */
	private void stop() {
		synchronized (notifier) {
			try {
				if (client != null) {
					client.close();
					Logger.getGlobal().info("Connection closed");
				}
			} catch (Exception e) {
				Logger.getGlobal().warning(e.getMessage());
			} finally {
				client = null;
			}
			running = false;
			notifier.notify();
		}
	}

	/**
	 * Creates an Executor that stores the incoming messages in a bounded queue.
	 * 
	 * @param client      The Client to use for communication
	 * @param inCapacity  Incoming messages queue's capacity
	 * @param outCapacity Outgoing messages queue's capacity
	 */
	public Executor(Client client, int inCapacity, int outCapacity) {
		this.client = client;
		this.in = new ArrayBlockingQueue<Message>(inCapacity);
		this.out = new ArrayBlockingQueue<Message>(outCapacity);
	}

	/**
	 * Creates an Executor that uses a Receiver to process the incoming messages.
	 * 
	 * @param client      The Client to use for communication
	 * @param receiver    The Receiver of the incoming messages
	 * @param outCapacity Outgoing messages queue's capacity
	 */
	public Executor(Client client, Receiver receiver, int outCapacity) {
		this.client = client;
		this.receiver = receiver;
		this.in = null;
		this.out = new ArrayBlockingQueue<Message>(outCapacity);
	}

	/**
	 * Assigns a Client to this Executor. The existing Client is replaced, but not
	 * closed. Fails if the Executor is already running.
	 * 
	 * @param client The Client to use with this Executor
	 */
	public void setClient(Client client) {
		if (!isRunning()) {
			this.client = client;
		} else {
			throw new IllegalStateException(BAD_REQUEST);
		}
	}

	/**
	 * Assigns a Receiver for processing the incoming messages. Fails if the
	 * Executor is already running. Also fails if the Executor was created with an
	 * incoming messages queue.
	 * 
	 * @param receiver The Receiver to use
	 */
	public void setReceiver(Receiver receiver) {
		if (isRunning()) {
			throw new IllegalStateException(BAD_REQUEST);
		} else if (in != null) {
			throw new IllegalArgumentException(BAD_REQUEST);
		} else {
			this.receiver = receiver;
		}
	}

	/**
	 * Tries to put a message into the outgoing queue
	 * 
	 * @param message The outgoing message
	 * @return true on success, false otherwise
	 */
	public boolean offer(Message message) {
		return out.offer(message);
	}

	/**
	 * Puts a message into the outgoing queue
	 * 
	 * @param message The outgoing message
	 * @throws InterruptedException
	 */
	public void put(Message message) throws InterruptedException {
		out.put(message);
	}

	/**
	 * Returns true if the incoming queue is not empty
	 * 
	 * @return true if the incoming queue contains at least one message, false
	 *         otherwise
	 */
	public boolean hasIncomingMessage() {
		return (in != null) && !in.isEmpty();
	}

	/**
	 * Returns a message from the incoming queue
	 * 
	 * @return An incoming message
	 * @throws InterruptedException
	 */
	public Message take() throws InterruptedException {
		if (in != null) {
			return in.take();
		} else {
			throw new IllegalStateException(BAD_REQUEST);
		}
	}

	/**
	 * Returns the "running" state of the Executor
	 * 
	 * @return true if the Executor is running, false otherwise
	 */
	public boolean isRunning() {
		return !stopped.get();
	}

	@Override
	public void run() {

		Thread reader = new Thread(() -> {
			Logger.getGlobal().info("Reader started");
			try {
				while (true) {
					if (receiver != null) {
						receiver.receive(client.receive());
					} else if (in != null) {
						in.put(client.receive());
					} else {
						client.receive();
					}
				}
			} catch (Exception e) {
				close();
			} finally {
				Logger.getGlobal().info("Reader stopped");
			}
		});

		Thread writer = new Thread(() -> {
			Logger.getGlobal().info("Writer started");
			try {
				while (true) {
					if (outgoing == null) {
						outgoing = out.take();
					}
					client.send(outgoing);
					outgoing = null;
				}
			} catch (Exception e) {
				close();
			} finally {
				Logger.getGlobal().info("Writer stopped");
			}
		});

		try {
			stopped.set(false);
			synchronized (notifier) {
				reader.start();
				writer.start();
				running = true;
				while (running) {
					notifier.wait();
				}
			}
		} catch (Exception e) {

		} finally {
			try {
				reader.interrupt();
				reader.join();
			} catch (Exception e2) {

			}

			try {
				writer.interrupt();
				writer.join();
			} catch (Exception e2) {

			}
			Logger.getGlobal().info("Executor stopped");
			stopped.set(true);
		}
	}

	@Override
	public void close() {
		stop();
	}
}
