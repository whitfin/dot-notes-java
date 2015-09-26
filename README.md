dot-notes-java [![Build Status](https://travis-ci.org/zackehh/dot-notes-java.svg?branch=master)](https://travis-ci.org/zackehh/dot-notes-java)
==============

- [Setup](#setup)
- [Notation](#notation)
- [How Does It Work?](#apis)
- [Invalid Syntax](#exceptions)
- [Issues](#issues)

This is a Java port of [dot-notes](http://github.com/iwhitfield/dot-notes) to work with Jackson JSON. The interface is largely the same, and is documented below. All work is done with the `JsonNode` component of `com.fasterxml` Jackson, rather than the old Codehaus version.

This lib is built using Travis and has 100% code/line coverage, making it pretty robust. This does not mean it's perfect however, so make sure you have appropriate tests in your code to cover your usage.

### Setup ###

`dot-notes-java` is only available here as yet, however I'll attempt to push it to Maven central soon.

Just build it as usual with Maven:

```
$ mvn clean install
```

### Notation ###

This module follows the following notations:

```
// Any key may be referenced via dot separators
test.one

// Array elements must be wrapped in square brackets
test.one[1]

// Keys with special characters much go in quotes, in square brackets
test.one[1]['my.test']

// Quotes can be either double or single, as long as they match
test.one[1]["my.test"]

// Should you wish to, you can place normal field names in this form
['test']['one']
```

The parser is quite generous in what it will accept, although certain forms are blocked on purpose due to bad practice (e.g. `test[key]`). If your dot notation does not work correctly, `ParseException` will be thrown.

### APIs ###

JSON is shown here just for ease of documentation, don't get confused :).

#### create(String str, JsonNode value[, ObjectNode target]) ####

This method will take a dot notated string and convert it into an object, by populating either an existing object, or creating a new one. A second parameter can be provided to set the innermost field to a specific value. Similar to the `inflate` method, this method can accept a JsonNode parameter to merge keys into.

```
JsonNode obj = DotNotes.create("this.is.a.test", 5);

// becomes

{
  this: {
    is: {
      a: {
        test: 5
      }
    }
  }
}
```

#### get(String str, JsonNode obj) ####

Simply returns a nested object value from a dot notated string. Used for easy access to a value. This is the counterpart to `create`.

```
// would return 5
DotNotes.get("this.is.a.test", {
 this: {
   is: {
     a: {
       test: 5
     }
   }
 }
});
```

#### flatten(JsonNode inflatedObject) ####

Similar to the `inflate` method, but in reverse. This method will take a nested object and flatten it down to a single level, with dot notated keys.

```
DotNotes.flatten({
  test: {
    one: 5
  }
});

// becomes

{
    'test.one': 5
}
```

#### inflate(JsonNode flatObject[, ObjectNode target]) ####

This method will transform an object with flattened keys in the top level into the nested counterpart being represented by the keys. This method accepts a second parameter in order to merge keys over an existing object. If no object is provided, an empty one will be used.

```
DotNotes.inflate({
  test.one: 5
})

// becomes

{
  test: {
    one: 5
  }
}
```

#### keys(String str) ####

Transforms a dot notated string to an array of keys. Useful for recursion. In order to provide distinction, array indexes will be of type `Number` and integer keys will be of type `String`. Invalid strings will throw a `ParseException`.

```
String str = "test[1].2['three']";

DotNotes.keys(str); // [ 'test', 1, '2', 'three' ]
```

#### notedCursor(ObjectNode obj, NotedHandler handler) ####

Iterates through the paths of the current node, feeding all keys/values to a NotedHandler instance. This interface enforces an `execute(String path, JsonNode value)` method, which takes the following, in order:

```
path:  the nested path (i.e. a dot notation path to the value)
value: the value of the node at the end of the path
```

This allows for pretty customized flattening.

### Exceptions ###

There is very basic exception handling should an invalid syntax be used. Should this occur, a `ParseException` will be thrown, with an (attempted) reference to where the error is in the string.

### Issues ###

If you find any issues inside this module, feel free to open an issue [here](https://github.com/iwhitfield/dot-notes-java/issues "dot-notes-java Issues").