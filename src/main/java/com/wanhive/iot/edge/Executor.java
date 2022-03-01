/*
 * Executor.java
 * 
 * Executes a client
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

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.wanhive.iot.protocol.Client;
import com.wanhive.iot.protocol.Message;

/**
 * Threaded executor for the client applications. Uses two bounded queues, one
 * for the outgoing messages and another one for the incoming messages.
 * 
 * @author amit
 *
 */
public class Executor implements Runnable, AutoCloseable {
	private static final String BAD_REQUEST = "Not permitted";

	private final Object notifier = new Object();
	private boolean running = false; // Condition variable
	private final AtomicBoolean stopped = new AtomicBoolean(true); // Status flag

	private Client client; // The connection
	private Receiver receiver;
	private Message outgoing;
	private final BlockingQueue<Message> in;
	private final BlockingQueue<Message> out;

	/**
	 * Stops {@code this} {@link Executor} and closes the {@link Client}.
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
	 * Helper method for {@link #createWorker(boolean)}
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void receive() throws IOException, InterruptedException {
		Message message = client.receive();
		if (receiver != null) {
			receiver.receive(message);
		} else if (in != null) {
			in.put(message);
		} else {
			return;
		}
	}

	/**
	 * Helper method for {@link #createWorker(boolean)}
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void send() throws InterruptedException, IOException {
		if (outgoing == null) {
			outgoing = out.take();
		}
		client.send(outgoing);
		outgoing = null;
	}

	/**
	 * Helper method for creating reader and writer threads
	 * 
	 * @param isReader Set to {@code true} to create a reader thread, otherwise a
	 *                 writer thread is created
	 * @return A reader/writer {@link Thread}
	 */
	private Thread createWorker(final boolean isReader) {
		final String name = isReader ? "Reader" : "Writer";
		return new Thread(() -> {
			Logger.getGlobal().info(name + " started");
			try {
				while (true) {
					if (isReader) {
						receive();
					} else {
						send();
					}
				}
			} catch (Exception e) {
				close();
			} finally {
				Logger.getGlobal().info(name + " stopped");
			}
		});
	}

	/**
	 * Helper method for gracefully shutting down the reader and writer threads
	 * 
	 * @param worker A reader or writer {@link Thread} object
	 */
	private void stopWorker(Thread worker) {
		try {
			worker.interrupt();
			worker.join();
		} catch (Exception e) {
			Logger.getGlobal().warning(e.getMessage());
		}
	}

	/**
	 * Constructor: creates an {@link Executor} that stores the incoming messages in
	 * a bounded queue.
	 * 
	 * @param client      The connected {@link Client}
	 * @param inCapacity  Incoming messages queue's capacity
	 * @param outCapacity Outgoing messages queue's capacity
	 */
	public Executor(Client client, int inCapacity, int outCapacity) {
		this.client = client;
		this.in = new ArrayBlockingQueue<Message>(inCapacity);
		this.out = new ArrayBlockingQueue<Message>(outCapacity);
	}

	/**
	 * Constructor: creates an {@link Executor} that uses a {@link Receiver} to
	 * process the incoming messages.
	 * 
	 * @param client      The connected {@link Client}
	 * @param receiver    The {@link Receiver} of the incoming messages
	 * @param outCapacity Outgoing messages queue's capacity
	 */
	public Executor(Client client, Receiver receiver, int outCapacity) {
		this.client = client;
		this.receiver = receiver;
		this.in = null;
		this.out = new ArrayBlockingQueue<Message>(outCapacity);
	}

	/**
	 * Sets the {@link Client}. Any existing {@link Client} is replaced, but not
	 * closed. Fails if the {@link Executor} is already running.
	 * 
	 * @param client The {@link Client} to use with this {@link Executor}
	 */
	public void setClient(Client client) {
		if (!isRunning()) {
			this.client = client;
		} else {
			throw new IllegalStateException(BAD_REQUEST);
		}
	}

	/**
	 * Assigns a {@link Receiver} for processing the incoming messages. Fails if the
	 * {@link Executor} is already running. Also fails if the {@link Executor} was
	 * created with an incoming messages queue.
	 * 
	 * @param receiver The {@link Receiver} to use
	 */
	public void setReceiver(Receiver receiver) {
		if (!isRunning() && in == null) {
			this.receiver = receiver;
		} else {
			throw new IllegalStateException(BAD_REQUEST);
		}
	}

	/**
	 * Tries to put a message into the outgoing queue
	 * 
	 * @param message The outgoing {@link Message}
	 * @return true on success, false otherwise
	 */
	public boolean offer(Message message) {
		return out.offer(message);
	}

	/**
	 * Puts a message into the outgoing queue, waits if the queue is full
	 * 
	 * @param message The outgoing {@link Message}
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
	 * Returns a message from the incoming queue, waits if the queue is empty
	 * 
	 * @return An incoming {@link Message}
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
	 * Returns a message from the incoming queue
	 * 
	 * @return An incoming {@link Message}, {@code null} if the queue is empty.
	 */
	public Message poll() {
		if (in != null) {
			return in.poll();
		} else {
			throw new IllegalStateException(BAD_REQUEST);
		}
	}

	/**
	 * Returns the "running" state
	 * 
	 * @return true if {@code this} {@link Executor} is running, false otherwise
	 */
	public boolean isRunning() {
		return !stopped.get();
	}

	@Override
	public void run() {
		Thread reader = createWorker(true);
		Thread writer = createWorker(false);
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
			Logger.getGlobal().warning(e.getMessage());
		} finally {
			stopWorker(reader);
			stopWorker(writer);
			stopped.set(true);
			Logger.getGlobal().info("Executor stopped");
		}
	}

	@Override
	public void close() {
		stop();
	}
}
