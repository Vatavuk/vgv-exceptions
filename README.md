# Vgv-exceptions
Java library for object oriented exception handling.
Library converts try/catch/finally statements into reusable objects.

## Usage
Lets assume following simplified scenario (Fetching entity from DB).
```java
public Entity getEntity(String id) throws MyAppException {
    try {
        return entities.get(id);
    } catch(ClientIOException exp) {
        LOGGER.error("Client exception message", e);
        throw new MyAppException(e);
    } catch(DatabaseIOException exp) {
        LOGGER.error("Database exception message", e);
        throw new MyAppException(e);
    } catch(ValidationRuntimeException | IllegalStateException exp) {
        LOGGER.error("Validation exception message", e);
        throw new MyAppException(e);
    } finally {
        LOGGER.info("Attempt to fetch entity");
    }
}
```
The above code is very procedural and "go-to" like.

Lets build the above exception control step by step using objects.

We can start with **try/finally** statement.
```java
public Entity getEntity(String id) throws MyAppException {
    try {
        return entities.get(id);
    //...
    } finally {
        LOGGER.info("Attempt to fetch entity");
    }
}
```
Using objects:
```java
public Entity getEntity(String id) throws IOException {
    return
        new Try()
            .with(new Finally(() -> LOGGER.info("Attempt to fetch entity")))
            .exec(() -> entities.get(id));
}
```
Notice that the above method is now throwing IOException. That is because we did not
specify what kind of exception Try object will throw.
So lets specify it:
```java
public Entity getEntity(String id) throws MyAppException {
    return
        new Try()
            .with(
                new Finally(() -> LOGGER.info("Attempt to fetch entity")),
                new Throws(MyAppException::new)
             )
            .exec(() -> entities.get(id));
}
```
Finally add the catch statements to make full **try/catch/finally** exception control:
```java
public Entity getEntity(String id) throws MyAppException {
    return
        new Try(
            new Catch(
                ClientException.class,
                exp -> LOGGER.error("Client exception message", e)
            ),
            new Catch(
                DatabaseException.class,
                exp -> LOGGER.error("Database exception meassage", e)
            ),
            new Catch(
                new ListOf<>(ValidationRuntimeException.class, IllegalStateException.class),
                exp -> LOGGER.error("Validation exception meassage", e)
            )
        ).with(
            new Finally(() -> LOGGER.info("Attempt to fetch entity")),
            new Throws(MyAppException::new)
        ).exec(() -> entities.get(id));
```

### Unchecked exception handling
There are two ways to avoid throwing checked exceptions:
```java
public Entity getEntity(String id) {
    return
        new Try(
            ...
        ).with(new Throws(MyAppRuntimeException::new)
        ).exec(() -> entities.get(id));
}
```
or
```java
public Entity getEntity(String id) {
    return
        new UncheckedTry(
            new Try(
                ...
            )
        ).exec(() -> entities.get(id));
}
```
## Benefits?
    * Declarative instead of procedural code. Easier to maintain, extend and test
    * Classes can be decoupled from exception handling and specific implementation
      can be injected into them
    * SRP compliant
    * Reusable
### Exception handling injection
```java
public class MyApp {

    private final TryBlock block;

    public MyApp(final TryBlock blk) {
        this.block = blk;
    }

    public Entity getEntity(String id) throws MyAppException{
        return this.block.exec(() -> entities.get(id));
    }
}
```

