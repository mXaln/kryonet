/* Copyright (c) 2008, Nathan Sweet
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of Esoteric Software nor the names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.esotericsoftware.kryonet;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Used to be notified about connection events.
 */
public interface Listener {
	/**
	 * Called when the remote end has been connected. This will be invoked
	 * before any objects are received by {@link #received(Connection, Object)}.
	 * This will be invoked on the same thread as {@link Client#update(int)} and
	 * {@link Server#update(int)}. This method should not block for long periods
	 * as other network activity will not be processed until it returns.
	 */
	void connected(Connection connection);

	/**
	 * Called when the remote end is no longer connected. There is no guarantee
	 * as to what thread will invoke this method.
	 */
	void disconnected(Connection connection);

	/**
	 * Called when an object has been received from the remote end of the
	 * connection. This will be invoked on the same thread as
	 * {@link Client#update(int)} and {@link Server#update(int)}. This method
	 * should not block for long periods as other network activity will not be
	 * processed until it returns.
	 */
	void received(Connection connection, Object object);

	/**
	 * Called when the connection is below the
	 * {@link Connection#setIdleThreshold(float) idle threshold}.
	 */
	void idle(Connection connection);

	/**
	 * Wraps the listener interface and implements
	 * {@link Listener#idle(Connection)} and
	 * {@link Listener#received(Connection, Object)}.
	 *
	 */
	public abstract class ConnectionListener implements Listener {

		@Override
		public abstract void disconnected(Connection arg0);

		@Override
		public abstract void connected(Connection arg0);

		@Override
		public void received(Connection connection, Object object) {
		}

		@Override
		public void idle(Connection connection) {
		}

	}

	/**
	 * Wraps a listener and queues notifications as {@link Runnable runnables}.
	 * This allows the runnables to be processed on a different thread,
	 * preventing the connection's update thread from being blocked.
	 */
	static public abstract class QueuedListener implements Listener {
		final Listener listener;

		public QueuedListener(Listener listener) {
			if (listener == null)
				throw new NullPointerException("listener cannot be null.");
			this.listener = listener;
		}

		@Override
		public void connected(final Connection connection) {
			queue(new Runnable() {
				@Override
				public void run() {
					listener.connected(connection);
				}
			});
		}

		@Override
		public void disconnected(final Connection connection) {
			queue(new Runnable() {
				@Override
				public void run() {
					listener.disconnected(connection);
				}
			});
		}

		@Override
		public void received(final Connection connection, final Object object) {
			queue(new Runnable() {
				@Override
				public void run() {
					listener.received(connection, object);
				}
			});
		}

		@Override
		public void idle(final Connection connection) {
			queue(new Runnable() {
				@Override
				public void run() {
					listener.idle(connection);
				}
			});
		}

		abstract protected void queue(Runnable runnable);
	}

	/**
	 * Wraps a listener and processes notification events on a separate thread.
	 */
	static public class ThreadedListener extends QueuedListener {
		protected final ExecutorService threadPool;

		/**
		 * Creates a single thread to process notification events.
		 */
		public ThreadedListener(Listener listener) {
			this(listener, Executors.newFixedThreadPool(1));
		}

		/**
		 * Uses the specified <code>threadPool</code> to process notification
		 * events.
		 */
		public ThreadedListener(Listener listener, ExecutorService threadPool) {
			super(listener);
			if (threadPool == null)
				throw new NullPointerException("threadPool cannot be null.");
			this.threadPool = threadPool;
		}

		@Override
		public void queue(Runnable runnable) {
			threadPool.execute(runnable);
		}
	}

	/**
	 * Delays the notification of the wrapped listener to simulate lag on
	 * incoming objects. Notification events are processed on a separate thread
	 * after a delay. Note that only incoming objects are delayed. To delay
	 * outgoing objects, use a LagListener at the other end of the connection.
	 */
	static public class LagListener extends QueuedListener {
		private final ScheduledExecutorService threadPool;
		private final int lagMillisMin, lagMillisMax;
		final LinkedList<Runnable> runnables = new LinkedList<Runnable>();

		public LagListener(int lagMillisMin, int lagMillisMax,
				Listener listener) {
			super(listener);
			this.lagMillisMin = lagMillisMin;
			this.lagMillisMax = lagMillisMax;
			threadPool = Executors.newScheduledThreadPool(1);
		}

		@Override
		public void queue(Runnable runnable) {
			synchronized (runnables) {
				runnables.addFirst(runnable);
			}
			int lag = lagMillisMin
					+ (int) (Math.random() * (lagMillisMax - lagMillisMin));
			threadPool.schedule(new Runnable() {
				@Override
				public void run() {
					Runnable runnable;
					synchronized (runnables) {
						runnable = runnables.removeLast();
					}
					runnable.run();
				}
			}, lag, TimeUnit.MILLISECONDS);
		}
	}
}
