# Sample with Favorite screens

## Samples

There are 2 samples:

1. [FavoriteListFeature](src/main/kotlin/io/github/ikarenkov/sample/favorite/impl/FavoriteListFeature.kt) - simple implementation list loading logic
   and interaction as single store.
2. [FavoriteAggregatorStore](src/main/kotlin/io/github/ikarenkov/sample/favorite/impl/aggregated/FavoriteAggregatorStore.kt) - this is enhanced
   version of the previous sample, with suppot of pagination and extend favorite logic. It is an aggregation of 2 features: pagination and favorite.

## Logic of samples

In the sample we emulate work with favorite:

1. Loading favorite feature.
2. Subscribe to updates.
3. We can try to remove the item from favorite, but it can fail. In this case we return the item into the list.