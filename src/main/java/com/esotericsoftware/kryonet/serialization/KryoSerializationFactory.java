package com.esotericsoftware.kryonet.serialization;

import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.DiscoverHost;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.FrameworkMessage.RegisterTCP;
import com.esotericsoftware.kryonet.FrameworkMessage.RegisterUDP;

public class KryoSerializationFactory implements SerializationFactory {

	private Kryo kryo;

	public KryoSerializationFactory() {
		this(new Kryo());
	}

	public KryoSerializationFactory(Kryo kryo) {
		this.kryo = kryo;
	}

	public Kryo getKryo() {
		return kryo;
	}

	@Override
	public Serialization newInstance(Connection connection) {
		return new KryoSerialization(connection, kryo);
	}

	public class KryoSerialization implements Serialization {
		private final Kryo kryo;
		private final ByteBufferInput input;
		private final ByteBufferOutput output;

		public KryoSerialization(Connection connection, Kryo kryo) {
			this(kryo);
			kryo.setReferences(false);
			kryo.setRegistrationRequired(true);

			kryo.getContext().put("connection", connection);
		}

		public KryoSerialization(Kryo kryo) {
			this.kryo = kryo;

			kryo.register(RegisterTCP.class);
			kryo.register(RegisterUDP.class);
			kryo.register(KeepAlive.class);
			kryo.register(DiscoverHost.class);
			kryo.register(Ping.class);

			input = new ByteBufferInput();
			output = new ByteBufferOutput();
		}

		public Kryo getKryo() {
			return kryo;
		}

		public synchronized void write(ByteBuffer buffer, Object object) {
			output.setBuffer(buffer);
			kryo.writeClassAndObject(output, object);
			output.flush();
		}

		public synchronized Object read(ByteBuffer buffer) {
			input.setBuffer(buffer);
			return kryo.readClassAndObject(input);
		}

		public void writeLength(ByteBuffer buffer, int length) {
			buffer.putInt(length);
		}

		public int readLength(ByteBuffer buffer) {
			return buffer.getInt();
		}

		public int getLengthLength() {
			return 4;
		}
	}

}
