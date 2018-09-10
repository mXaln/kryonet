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

	private final KryoSerialization SERIALIZATION_INSTANCE;

	@Override
	public Serialization newInstance(Connection connection) {
		return SERIALIZATION_INSTANCE;
	}

	public KryoSerializationFactory() {
		this(new Kryo());

		kryo.setReferences(false);
		kryo.setRegistrationRequired(true);
	}

	public KryoSerializationFactory(Kryo kryo) {
		this.kryo = kryo;
		this.kryo.register(RegisterTCP.class);
		this.kryo.register(RegisterUDP.class);
		this.kryo.register(KeepAlive.class);
		this.kryo.register(DiscoverHost.class);
		this.kryo.register(Ping.class);

		this.SERIALIZATION_INSTANCE = new KryoSerialization(kryo);
	}

	public Kryo getKryo() {
		return kryo;
	}

	public class KryoSerialization implements Serialization {
		private final Kryo kryo;
		private final ByteBufferInput input;
		private final ByteBufferOutput output;

		public KryoSerialization(Kryo kryo) {
			this.kryo = kryo;

			input = new ByteBufferInput();
			output = new ByteBufferOutput();
		}

		public Kryo getKryo() {
			return kryo;
		}

		@Override
		public synchronized void write(ByteBuffer buffer, Object object) {
			output.setBuffer(buffer);
			kryo.writeClassAndObject(output, object);
			output.flush();
		}

		@Override
		public synchronized Object read(ByteBuffer buffer) {
			input.setBuffer(buffer);
			return kryo.readClassAndObject(input);
		}

		@Override
		public void writeLength(ByteBuffer buffer, int length) {
			buffer.putInt(length);
		}

		@Override
		public int readLength(ByteBuffer buffer) {
			return buffer.getInt();
		}

		@Override
		public int getLengthLength() {
			return 4;
		}
	}

}
