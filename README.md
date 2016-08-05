# dot-notes-java

[![Build Status](https://travis-ci.org/zackehh/dot-notes-java.svg?branch=master)](https://travis-ci.org/zackehh/dot-notes-java) [![Coverage Status](https://coveralls.io/repos/zackehh/dot-notes-java/badge.svg?branch=master&service=github)](https://coveralls.io/github/zackehh/dot-notes-java?branch=master)

This is a Java port of [dot-notes](http://github.com/zackehh/dot-notes) to work with Jackson JSON. The interface is the same, so please check out the [documentation](https://github.com/zackehh/dot-notes/wiki) for example usage. You can find the Javadocs from the latest release [here](http://www.javadoc.io/doc/com.zackehh/dot-notes) - they should be pretty self explanatory.

This lib is built using Travis and has 100% line/branch coverage, making it pretty robust. This does not mean it's perfect however, so make sure you have appropriate tests in your code to cover your usage.

### Setup

`dot-notes-java` available on Maven central, via Sonatype OSS:

```
<dependency>
    <groupId>com.zackehh</groupId>
    <artifactId>dot-notes</artifactId>
    <version>3.1.0</version>
</dependency>
```

### Differences to the JavaScript API

- The interface is the same for the most part, with the same methods surfaced to the API. Naturally there are a few differences due to the language changes, but it's all pretty straightforward.
- All work is done with the `JsonNode` component of `com.fasterxml` Jackson, rather than the old Codehaus version. 
- When passing a callback to `recurse`, please pass an instance of the `NodeIterator` class. You can override `requirePathGeneration` to return `false` to disable path generation for faster parsing.
- There is a lot more type validation, due to the strictness of Java. This leads to minor parity changes between the libraries cross-language, but it clearly unavoidable. An example of this is trying to create an Array noted key inside an ObjectNode; in Java you get an Exception, in JS it works just fine.
- All keys are wrapped in the `NotedKey` class, as they can either Integer (Array) or String (Object) keys. The `NotedKey` class therefore has `isNumber/isString` methods, and `asNumber/asString` methods to deal with appropriately. 

### Contributing

If you wish to contribute (awesome!), please file an issue in the main dot-notes repo, as this is just a port (unless it's a bug in this library). All PRs should pass `mvn clean test` and maintain 100% test coverage.

### Testing

Tests are run using Maven. I aim to maintain 100% coverage where possible (both line and branch). These tests can be run as follows:

```bash
$ mvn clean verify
```
