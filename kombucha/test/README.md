# Testing

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.ikarenkov/kombucha-test?label=kombucha-test&labelColor=005464&color=82e24c)](https://central.sonatype.com/artifact/io.github.ikarenkov/kombucha-test)

You can test you Store and Reducers using build-in functions [`testReducer`](src/commonMain/kotlin/io/github/ikarenkov/kombucha/test/TestReducer.kt)
and [`testStoreReducer`](src/commonMain/kotlin/io/github/ikarenkov/kombucha/test/TestStoreReducer.kt).

## Testing DSL

It is more convenient to use build-in DSL [TestReducerDslBuilder](src/commonMain/kotlin/io/github/ikarenkov/kombucha/test/TestReducerDslBuilder.kt) to
describe
expected behavior of the store.

Let's take a look to the example:

```kotlin
testReducer(
    initialState = State.Init(false),
    reducer = AuthFeature.Reducer
) {
    Msg.Init returns State.Init(inProgress = true) + Eff.LoadCachedData
    Msg.LoadCacheAuthResult(null) returns State.NotAuthorized.Idle
}
```

[//]: # (TODO)