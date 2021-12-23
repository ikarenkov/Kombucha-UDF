object di {

    private const val koinVersion = "3.1.4"

    val koinCore = deps(
        "io.insert-koin:koin-core:$koinVersion".dep,
    )
    val koinAndroid = deps(
        "io.insert-koin:koin-android:$koinVersion".dep,
        koinCore
    )

}

object log {

    val logcat = "com.squareup.logcat:logcat:0.1".dep

}