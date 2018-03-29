![KryoNet](https://raw.github.com/wiki/EsotericSoftware/kryonet/images/logo.jpg)

A fork of [kryonet](https://github.com/EsotericSoftware/kryonet/), a Java library that provides a clean and simple API for efficient network communication.

This fork was specifically made for [ProjectGG](https://github.com/eskalon/ProjektGG) but also adds the most demanded features on kryonet's issue tracker. If you have a pull request for kryonet also consider adding it here, as kryonet doesn't seem to be updated anymore.

## Key Changes
* A TypeListener for easier message handling (see the example below; also fixes [#130](https://github.com/EsotericSoftware/kryonet/issues/130))
* Listener is now an interface ([#39](https://github.com/EsotericSoftware/kryonet/issues/39))
* Kryo 4.0.2 is used for the serialization ([#77](https://github.com/EsotericSoftware/kryonet/issues/77); also fixes [#123](https://github.com/EsotericSoftware/kryonet/issues/123))
* Includes a fix for the common Android 5 crash ([#106](https://github.com/EsotericSoftware/kryonet/issues/106))
* The LAN Host Discovery is now available to Non-Kryo-Serializations ([#127](https://github.com/EsotericSoftware/kryonet/issues/127))
* A few other changes to serializations (see the respective paragraph below)
* Kryonet now uses a [gradle](https://gradle.org/) build setup
* A couple improvements to the documentation ([#44](https://github.com/EsotericSoftware/kryonet/issues/44), [#35](https://github.com/EsotericSoftware/kryonet/issues/35))

## Usage of the TypeListener

The type listener takes care of distributing received messages to previously specified handlers. In the following example _con_ is the connection to the client and _msg_ is the received object - already cast to the right type:

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
```

## Changes to (Custom) Serializations

* The serialization objects are now created by [factories](https://github.com/crykn/kryonet/blob/master/src/main/java/com/esotericsoftware/kryonet/serialization/SerializationFactory.java): `SerializationFactory#newInstance(Connection)`
* The in-built serializations still behave exactly the same (i.e. one serialization instance is used for the server)
* Custom serializations on the other hand can now decide to only get used _once per TCP connection_ (all UDP connections still use one instance). This allows the server to keep responding while a message for another connection is still being serialized (see [#137](https://github.com/EsotericSoftware/kryonet/issues/137)).

## Download

The download is available on the [releases](https://github.com/crykn/kryonet/releases) page. You can also use [jitpack.io](https://jitpack.io/#crykn/kryonet/).

An example for gradle:
```
allprojects {
   repositories {
      ...
      maven { url 'https://jitpack.io' }
   }
}
	
dependencies {
   compile 'com.github.crykn:kryonet:2.22.2'
}
```