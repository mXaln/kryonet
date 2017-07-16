![KryoNet](https://raw.github.com/wiki/EsotericSoftware/kryonet/images/logo.jpg)

A fork of [kryonet](https://github.com/EsotericSoftware/kryonet/), a Java library that provides a clean and simple API for efficient network communication.

This fork was specifically made for [ProjectGG](https://github.com/Meidimax99/ProjektGG) but also adds the most demanded features on kryonet's issue tracker.

## Key Changes
* A TypeListener for easier message handling (see the example below)
* Listener is now a interface ([#39](https://github.com/EsotericSoftware/kryonet/issues/39))
* Uses kryo 4.0.0 ([#77](https://github.com/EsotericSoftware/kryonet/issues/77))
* Fixes for the Android 5 and iOS crashes ([#106](https://github.com/EsotericSoftware/kryonet/issues/106))
* Kryonet now uses a gradle setup

## Usage of the TypeListener

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
```

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
   compile 'com.github.crykn:kryonet:2.22'
}
```