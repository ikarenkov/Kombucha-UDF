include(
    ":sample:app",
    ":sample:core:feature",
)
apply(from = "./features/modules.gradle.kts")
//apply(from = "./core/modules.gradle.kts")