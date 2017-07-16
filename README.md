![KryoNet](https://raw.github.com/wiki/EsotericSoftware/kryonet/images/logo.jpg)

A fork of [kryonet](https://github.com/EsotericSoftware/kryonet/), a Java library that provides a clean and simple API for efficient network communication.

This fork was specifically made for [ProjectGG]() and adds only a few small improvements.

### Key Changes
* A TypeListener for easier message handling (see the example below)
* Fixes for the Android 5 and iOS crashes

### Usage of the changes

This code adds a listener to handle receiving objects:

```java
        TypeListener typeListener = new TypeListener();
        
		typeListener.addTypeHandler(SomeRequest.class,
				(con, msg) -> {
					System.out.println(msg.getSomeData());
				});
		typeListener.addTypeHandler(SomeOtherRequest.class,
				(con, msg) -> {
					con.sendTCP(new SomeResponse());
				});

		server.addListener(typeListener);
    });
```
