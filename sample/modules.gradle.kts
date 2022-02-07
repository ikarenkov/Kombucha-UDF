include(
    ":sample:app",
    ":sample:binary",
)
apply(from = "./features/modules.gradle.kts")
apply(from = "./core/modules.gradle.kts")