![KryoNet](https://raw.github.com/wiki/EsotericSoftware/kryonet/images/logo.jpg)

[![Release](https://jitpack.io/v/crykn/kryonet.svg)](https://jitpack.io/#crykn/kryonet)

A fork of [KryoNet](https://github.com/EsotericSoftware/kryonet/), a Java library that provides a clean and simple API for efficient network communication.

This fork was specifically made for [ProjektGG](https://github.com/eskalon/ProjektGG) but also adds the most demanded features on KryoNet's issue tracker. If you have a pull request for KryoNet also consider adding it here, as KryoNet doesn't seem to be actively maintained anymore.

## Key Changes
* [Kryo 5.0.0-RC4](https://github.com/EsotericSoftware/kryo/releases/tag/kryo-parent-5.0.0-RC4) is used for the serialization (for a list of changes and new features see [here](https://groups.google.com/forum/#!msg/kryo-users/sBZ10dwrwFQ/hb6FF5ZXCQAJ); takes care of [#77](https://github.com/EsotericSoftware/kryonet/issues/77) and [#123](https://github.com/EsotericSoftware/kryonet/issues/123))
* A TypeListener for easier message handling (see the example below; also fixes [#130](https://github.com/EsotericSoftware/kryonet/issues/130))
* Listener is now an interface ([#39](https://github.com/EsotericSoftware/kryonet/issues/39)) with empty default methods which helps to reduce boilperplate code
* Includes a fix for the common Android 5.0 crash ([#106](https://github.com/EsotericSoftware/kryonet/issues/106), [#120](https://github.com/EsotericSoftware/kryonet/issues/106))
* The LAN Host Discovery is now available to Non-Kryo-Serializations ([#127](https://github.com/EsotericSoftware/kryonet/issues/127))
* Kryonet now uses a [gradle](https://gradle.org/) build setup
* Java 9+ is supported
* Various improvements to the documentation (also takes care of [#35](https://github.com/EsotericSoftware/kryonet/issues/35), [#44](https://github.com/EsotericSoftware/kryonet/issues/44), [#124](https://github.com/EsotericSoftware/kryonet/issues/124), [#137](https://github.com/EsotericSoftware/kryonet/issues/137))
* And a lot more minor fixes and changes

## Usage of the TypeListener

The type listener takes care of distributing received messages to previously specified handlers. Especially with lambdas this allows for rather concise code: In the following example _con_ is the connection to the client and _msg_ is the received object - already cast to the right type:

```java
TypeListener typeListener = new TypeListener();

// add a type handler for SomeRequest.class   
typeListener.addTypeHandler(SomeRequest.class,
   (con, msg) -> {
      System.out.println(msg.getSomeData());
   });
// add another one for SomeOtherRequest.class
typeListener.addTypeHandler(SomeOtherRequest.class,
   (con, msg) -> {
      con.sendTCP(new SomeResponse());
});

server.addListener(typeListener);
```

## Download

The download is available on the [releases](https://github.com/mXaln/kryonet/releases) page. You can also use [jitpack.io](https://jitpack.io/#mXaln/kryonet/).

An example for gradle:

```
allprojects {
   repositories {
      // ...
      maven { url 'https://jitpack.io' }
   }
}
	
dependencies {
   compile 'com.github.mXaln:kryonet:2.22.7'
}
```
