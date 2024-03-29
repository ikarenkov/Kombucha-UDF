# Kombucha UDF

<img src=".idea/icon.png" width=256>

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.ikarenkov/kombucha-core?label=kombucha-core&labelColor=005464&color=82e24c)](https://central.sonatype.com/artifact/io.github.ikarenkov/kombucha-core)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.ikarenkov/kombucha-test?label=kombucha-test&labelColor=005464&color=82e24c)](https://central.sonatype.com/artifact/io.github.ikarenkov/kombucha-test)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.ikarenkov/kombucha-ui-adapter?label=kombucha-ui-adapter&labelColor=005464&color=82e24c)
](https://central.sonatype.com/artifact/io.github.ikarenkov/kombucha-ui-adapter)

Kombucha UDF is a UDF library based on The Elm Architecture (TEA) concepts that helps to focus on logic of you application, rather than understanding
what is going on.

# Quick start

## Apply dependency in build.gradle(.kts)

* If you use android project, in your module build.gradle(.kts)
    ```kotlin
    dependencies {
        // Core dependency for main functionality
        implementation("io.github.ikarenkov:kombucha-core:${version}")
        // For convinien mapping ui and preserving loosing ui effects
        implementation("io.github.ikarenkov:kombucha-ui-adapter:${version}")
        // For testing
        testImplementation("io.github.ikarenkov:kombucha-test:${version}")    
    }
    ```
* If you use kotlin multiplatform project
    ```kotlin
    kotlin {
        sourceSets {
          commonMain.dependencies {
              // Core dependency for main functionality
              implementation("io.github.ikarenkov:kombucha-core:${version}")
              // For convinien mapping ui and preserving loosing ui effects
              implementation("io.github.ikarenkov:kombucha-ui-adapter:${version}")
          }
          commonTest.dependencies {
             // For testing
             testImplementation("io.github.ikarenkov:kombucha-test:${version}")
          }
        }
    }
    ```

## Create you first simple store

Take a look at [CounterFeature](sample/features/counter/impl/src/main/kotlin/io/github/ikarenkov/kombucha/sample/counter/impl/CounterFeature.kt) for
simple counter store implementation.

<details>
  <summary>To create you store you need:</summary>

1. Create an object named ${YourFeatureName}Feature and define your `Msg`, `State` and `Eff` inside.
2. Create reducer, that implements your business logic. Take a look to
   the [counterDslReducerReducer](sample/features/counter/impl/src/main/kotlin/io/github/ikarenkov/kombucha/sample/counter/impl/CounterFeature.kt#L70)
3. Create `EffectHandler` for side effects and unstable behavior.
   [CounterEffectHandle](sample/features/counter/impl/src/main/kotlin/io/github/ikarenkov/kombucha/sample/counter/impl/CounterEffectHandler.kt)
4. Create a store named ${YourFeatureName}Store and inherited
   from [Store](kombucha/core/src/commonMain/kotlin/io/github/ikarenkov/kombucha/store/Store.kt), defining initial state, using
   created `counterDslReducerReducer` and `CounterEffectHandler`.

</details>

## Store usage

```Kotlin
// to send a message
store.accept(Msg.OnIncreaseClick)
// to observe states
store.state.collect { ... }
// to observe effectts
store.effects.collect { ... }
```

# Artifacts

There are 3 kotlin multiplatform libraries available for jvm, ios and js. Check out modules README.md for detailed description.

1. [**kombucha-core**](kombucha/core) - core library that contains main logic, such as store, reducer, effectHandler and so on.
2. [**kombucha-ui-adapter**](kombucha/ui-adapter) - extension of library that helps to adapt your store for UI.
3. [**kombucha-test**](kombucha/test) - helper classes and functions that provides convenient dsl for tenting your store.

# Core concept

## Store

Store maintains the application's state and orchestrates the interactions between components.
Basicly, Store is a base UDF components to interact, that holds state and can accept some messages to handle. It has one input and two outputs:

<img src="https://github.com/ikarenkov/Kombucha-UDF/assets/17216532/09fff09b-f7f9-42c9-ab73-ce3f2f25fb9a" width="600">

Let's take a look to Store main components:

<img src="https://github.com/ikarenkov/Kombucha-UDF/assets/17216532/9669c03c-2d9d-4610-a30a-58619a4bcfb7" width="600">

* Reducer - *pure function*, generates a new state and produces effects that require handling.
* EffectHandler - receives effects from the reducer and has the ability to send messages back to trigger the reducer. Our way to comunicate with the
  outer world, including any operations that are not pure. F.e. requesting data from db or api or saving data, requesting random and e.t.c.

## Models

There are 3 main model: Msg, State, Eff.
Msg - describes intention to do something. It can be user click, result from your back end or anything else.
State - the state that is stored in store. It represents the state of your feature.
Eff - describes side effects, that should be executed. It's just a description of your intention to something that is not a pure function. F.e. it
can be request to load some data from backend, saving or reading data from database and e.t.c.

## Store factory

You can implement [StoreFactory](kombucha/core/src/commonMain/kotlin/io/github/ikarenkov/kombucha/store/StoreFactory.kt) to customize creation of the
store instead of using direct inheritance. It helps to implement dependency inversion principe. F.e. it can be used to implement exception logging

# Thanks

Huge thanks for the inspiration to [elmsli](https://github.com/vivid-money/elmslie) and [Puerh](https://github.com/Mishkun/Puerh).
