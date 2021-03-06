# Vgv-exceptions

### UPDATE:
This library was used as a POC for object-oriented exception handling in [cactoos](https://github.com/yegor256/cactoos).
Please refer to that project. Exception handling functionallity is implemented in `CheckedScalar`/`ScalarWithFallback` and
related `Checked*` classes.

More details in the [discussion](https://github.com/yegor256/cactoos/issues/656) 

**How to use**.
Latest version [here](https://github.com/Vatavuk/vgv-exceptions/releases)

```xml
<dependency>
    <groupId>hr.com.vgv</groupId>
    <artifactId>vgv-exceptions</artifactId>
    <version>1.2</version>
</dependency>
```

Java version required: 1.8+.

## Example
Assume following simplified scenario (Fetching entity from DB):
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

We can use objects instead:
```java
public Entity getEntity(String id) throws MyAppException {
    return
        new Try(
            new Catch(
                ClientIOException.class,
                exp -> LOGGER.error("Client exception message", e)
            ),
            new Catch(
                DatabaseIOException.class,
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

