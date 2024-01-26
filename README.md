# Kombucha UDF

Kombucha UDF is a UDF library based on The Elm Architecture (TEA) concepts that helps to focus on logic of you application, rather then understanding
what is going on.

## Core concept

### Models

There are 3 main model: Msg, State, Eff.
Msg - describes intention to do something. It can be user click, result from your back end or anything else.
State - the state that is stored in store. It represents the state of your feature.
Eff - describes side effects, that should be executed. It's just a description of your intention to to something that is not a pure function. F.e. it
can be request to load some data from backend, saving or reading data from database and e.t.c.

### Store

Store is a base UDF components to interact, that holds state and can accept some messages to handle. It has one input and two outputs.

* Fun accept(msg) is the only way to request changes from store. Message from `accept` go directly to `Reducer` with a current state. Then `Reduser`
  return a new state and side effects.
* State - is a state flow of states that you can subscribe to.
* effects - is shared flow of fire and forget effects that you can subscribe to.