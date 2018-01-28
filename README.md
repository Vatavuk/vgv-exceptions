Java library for object oriented exception handling.

## Usage
```java
new Try(
    new TryWith(
        ClientException.class,
        exp -> { Logger.error("client msg") }),
    new TryWith(
        ServerException.class,
        exp -> { Logger.error("server msg") }),
    new TryWith(
        new Array<>(FileNotFoundException.class, IOException.class),
        exp -> { throw new IllegalStateException(exp) })
    ).valueOf( () -> entities.fetch("someId"));
