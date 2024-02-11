# UI Adapter

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.ikarenkov/kombucha-ui-adapter?label=kombucha-ui-adapter&labelColor=005464&color=82e24c)
](https://central.sonatype.com/artifact/io.github.ikarenkov/kombucha-ui-adapter)

This module contains [UiStore](src/commonMain/kotlin/io/github/ikarenkov/kombucha/ui/UiStore.kt) that provides functionality:

1. Convert models to Ui Models
2. Cache ui effects when there is no subscribers and emit cached effects with a first subscription. It can be disabled using
   parameter `cacheUiEffects = false`.

You can use [UiStoreBuilder](src/commonMain/kotlin/io/github/ikarenkov/kombucha/ui/UiStoreBuilder.kt) and function `uiBuilder` for convenient usage
without declaring all 6 generics. [UiStoreBuilder] also provides some build in functions, and you can easily extend it using extension fun.

## Sample

Take a look to the [sample.feature.ui](../../sample/features/ui) for detailed examples of usage.

If your UiMsg and UiEff are subclasses of Msg and Eff, you can use following code for simple mapping only UiState

```kotlin
val store: Store<Msg, State, Eff> = ...
val uiStore = store.uiBuilder().using<Msg.Ext, UiState, Eff.Ext> { state ->
    UiState(
        state.itemsIds.map { resources.getString(R.string.item_title, it) }
    )
}
```

Otherwise, you can provide your own mappers for UiMsg -> Msg and for Eff -> UiEff

```kotlin
store.uiBuilder().using<Msg.Ext, UiState, Eff.Ext>(
    uiMsgToMsgConverter = { it },
    uiStateConverter = { state ->
        UiState(
            state.itemsIds.map { resources.getString(R.string.item_title, it) }
        )
    },
    uiEffConverter = { eff ->
        eff as? Eff.Ext
    }
)
```