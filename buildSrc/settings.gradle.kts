include(":forma")
project(":forma").projectDir = file("../forma")

fun pluginTarget(name: String) {
    include(":forma:$name")
    project(":forma:$name").projectDir = file("../forma/$name")
}

pluginTarget("android")
pluginTarget("deps")
pluginTarget("deps-core")
pluginTarget("validation")
pluginTarget("target")
