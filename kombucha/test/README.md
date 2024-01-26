# Testing

You can test you Store and Reducers using build-in functions [`testReducer`](src/main/kotlin/ru/ikarenkov/kombucha/test/TestReducer.kt)
and [`testStoreReducer`](src/main/kotlin/ru/ikarenkov/kombucha/test/TestStoreReducer.kt).

## Testing DSL

It is more convenient to use build-in DSL [TestReducerDslBuilder](src/main/kotlin/ru/ikarenkov/kombucha/test/TestReducerDslBuilder.kt) to describe
expected behavior of the store.

Lets take a look to the example:

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