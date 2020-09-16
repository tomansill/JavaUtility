# Utility

A collection of helpful utility functions and classes.

By Tom Ansill

## Contents

- `Utility` - Collection of commonly used functions.
  - `bytesToHex(byte[])` - Converts a byte array to hexidecimal string.
  - `hexToBytes(String)` - Converts a hexidecimal string to byte array.
  - `format(String,Object,Object...)` - Formats string, replaces any '{}' with object's `toString()` representation.
  - `f(String,Object,Object...)` - Shorthand function for `format(String,Object,Object...)`.
  - `simpleToString(Object)` - `toString()` implementation for the lazy. It will create a string with class name and its fields. Example: MyDogClass(name="fido", color="Brown", age=1). In `toString()` of your classes, you just put `Utility.simpleToString(this)`. 
  - `sensibleToString(Object object)` - Simply adds double quotation marks between a String if input `Object` is a string, otherwise, returns `toString()` of non-`String` object. This function help to solve my minor beef with `String`'s `toString()` implementation where one can easily confuse with `null` or `"null"` when object automatically gets converted to String in like `System.out.println("Hello " + nullableString)`. 
  - `generateString(long)` - Generates a random sequence of string. The characters in the random string will be alphanumeric `[a-zA-z0-9]`.
  - `generateString(Random, long)` - Generates a random sequence of string with provided random generator. The characters in the random string will be alphanumeric `[a-zA-z0-9]`.
  
- `ResourceUtility` - Collection of functions related to reading resources within `.jar` files.
  - `getAllFilesInResource(Class<?>,String,boolean)` - Scans all resources inside `.jar` file that owns the input `Class<?>` and outputs a `Set<String>` of path of resources.
  - `getResourceListing(Class<?>,String)` - Returns a listing of specified path in the `.jar` that owns the input `Class<?>`. **NOTE:** This is not same as `getResource(String)` because directories are not a thing in `.jar` files. All of the files are flattened into a single big root directory. Meaning if you export a `.jar` file and attempt to find a directory in it with just `getResource(String)`, it wouldn't work. 
  - `getResourceFileContent(Class<?>,String)` - Reads entire file in `.jar` that owns the input `Class<?>` and output it as String.
- `Version` - Class to detect and interpret versions in Jars. Assuming the Jar file will have manifest file that has version on it. Class will attempt to find it and interpret it. First it looks at package's `getImplementationVersion()`, then `getSpecificationVersion()`, finally, as last-resort, it will look for `verison.properties` in the resources. If that fails, then it will just output `X.Y.Z`. 
- `ExCollectors` - Extended `Collectors` utility class for more terminal `Stream` functions.
  - `toMap()` - Convenience function of `Collectors.toMap(K,V)` if you are already streaming `Map.Entry<K,V>`. Equivalent to `Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue)`.
  - `toConcurrentMap()` - Same as `toMap()` but as `ConcurrentMap`.
- `function/*` - Package of lambda functions
  - `RunnableWithException` - Variant of `Runnable` that throws `Exception`.
  - `ConsumerWithException` - Variant of `Consumer` that throws `Exception`. 
  - `FunctionWithException` - Variant of `Function` that throws `Exception`. 
  - `SupplierWithException` - Variant of `Supplier` that throws `Exception`.
  - `BiConsumerWithException` - Variant of `BiConsumer` that throws `Exception`. 
  - `BiFunctionWithException` - Variant of `BiFunction` that throws `Exception`. 
  - `TriConsumer` - Extension of `Consumer` but with 3 parameters.
  - `TriConsumerWithException` - Variant of `TriConsumer` that throws `Exception`. 
  - `TriFunction` - Extension of `Function` but with 3 parameters.
  - `TriFunctionWithException` - Variant of `TriFunction` that throws `Exception`. 
  - `QuadConsumer` - Extension of `Consumer` but with 4 parameters.
  - `QuadConsumerWithException` - Variant of `QuadConsumer` that throws `Exception`. 
  - `QuadFunction` - Extension of `Function` but with 4 parameters.
  - `QuadFunctionWithException` - Variant of `QuadFunction` that throws `Exception`. 
  
## Prerequisites

- Java 8 or better
- [Java Validation Library](https://github.com/tomansill/JavaValidation/)

## Download and Install

### Package Repository

The library is availble for download on Sonatype public Maven repository (https://oss.sonatype.org/#nexus):The library is availble for download on Sonatype public Maven repository (https://oss.sonatype.org/#nexus):

#### Utility (`Utility`, `ResourceUtility`, `ExCollectors`, and `functions/*`)
```xml
<dependency>
  <groupId>com.ansill.utility</groupId>
  <artifactId>utility</artifactId>
  <version>0.1.1</version>
</dependency>
```

#### Version
```xml
<dependency>
  <groupId>com.ansill.utility</groupId>
  <artifactId>version</artifactId>
  <version>0.1.1</version>
</dependency>
```

### Build and Install

Maven (or other similar build tools) is needed to build and install JavaUtility

```sh
$ git clone https://github.com/tomansill/javautility
$ cd javautility
$ mvn install
```