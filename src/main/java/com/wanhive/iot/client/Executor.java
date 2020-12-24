/*
 * Executor.java
 * 
 * The IO engine for Wanhive
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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import com.wanhive.iot.protocol.Message;

/**
 * Bounded queue based threaded IO engine for Wanhive. Maintains two separate
 * queues, one for the outgoing messages and another one for the incoming
 * messages.
 * 
 * @author amit
 *
 */
public class Executor implements Runnable, AutoCloseable {
	private final Object notifier = new Object();
	private volatile boolean running = false; // The condition variable
	private volatile boolean stopped = true; // The status tracker

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
				}
			} catch (Exception e) {

			}
			running = false;
			notifier.notify();
		}
	}

	/**
	 * Creates an Executor that stores the incoming message into it's incoming
	 * queue.
	 * 
	 * @param client      The Client to be used for communication
	 * @param inCapacity  The capacity of the incoming messages queue
	 * @param outCapacity The capacity of the outgoing messages queue
	 */
	public Executor(Client client, int inCapacity, int outCapacity) {
		this.client = client;
		this.in = new ArrayBlockingQueue<Message>(inCapacity);
		this.out = new ArrayBlockingQueue<Message>(outCapacity);
	}

	/**
	 * Creates an Executor that uses a Receiver to process the incoming messages.
	 * 
	 * @param client      The Client to be used for communication
	 * @param receiver    The Receiver for the incoming messages
	 * @param outCapacity The capacity of the outgoing messages queue
	 */
	public Executor(Client client, Receiver receiver, int outCapacity) {
		this.client = client;
		this.receiver = receiver;
		this.in = null;
		this.out = new ArrayBlockingQueue<Message>(outCapacity);
	}

	/**
	 * Sets executor's Client. Existing Client is replaced, but not closed. Fails if
	 * the executor is running.
	 * 
	 * @param client The Client to use with the Executor
	 */
	public void setClient(Client client) {
		if (!isRunning()) {
			this.client = client;
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Sets the Receiver for the incoming messages. Fails if the executor is
	 * running. Also fails if the executor was created with an incoming queue.
	 * 
	 * @param receiver The Receiver to use
	 */
	public void setReceiver(Receiver receiver) {
		if (isRunning()) {
			throw new IllegalStateException();
		} else if (in != null) {
			throw new IllegalArgumentException();
		} else {
			this.receiver = receiver;
		}
	}

	/**
	 * Tries to put a message into the outgoing queue
	 * 
	 * @param message Message to send out
	 * @return true on success, false otherwise
	 */
	public boolean offer(Message message) {
		return out.offer(message);
	}

	/**
	 * Puts a message into outgoing queue
	 * 
	 * @param message Message to send out
	 * @throws InterruptedException
	 */
	public void put(Message message) throws InterruptedException {
		out.put(message);
	}

	/**
	 * Returns true if the incoming queue contains at least one message
	 * 
	 * @return true if the incoming queue is not empty, false otherwise
	 */
	public boolean hasMessage() {
		return (in != null) && !in.isEmpty();
	}

	/**
	 * Returns a message from the incoming queue
	 * 
	 * @return A message from the incoming queue
	 * @throws InterruptedException
	 */
	public Message take() throws InterruptedException {
		if (in != null) {
			return in.take();
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Checks Executor's running state
	 * 
	 * @return true if the Executor is running, false otherwise
	 */
	public boolean isRunning() {
		return !stopped;
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
			running = true;
			stopped = false;
			reader.start();
			writer.start();
			synchronized (notifier) {
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
			stopped = true;
		}
	}

	@Override
	public void close() {
		stop();
	}
}
