import tools.forma.android.target.LibraryTargetTemplate

object tea {

    val core = target(":teamaker:core-library")
    val instanceKeeperUtil = target(":teamaker:instanceKeeper-android-util")
    val compose = target(":teamaker:compose-android-util")

}

object navigation {

    val modoCore = mimicTarget(":sample:core:modo:modo", LibraryTargetTemplate)
    val modoCompose = deps(
        mimicTarget(":sample:core:modo:modo-render-android-compose", LibraryTargetTemplate),
        modoCore
    )

}

object core {

    val feature = target(":sample:core:feature-library")

}