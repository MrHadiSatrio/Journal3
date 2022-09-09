package scripts

import scripts.Variants_gradle.BuildTypes

private object Default {
    const val BUILD_TYPE = BuildTypes.DEBUG
    val VARIANT = BUILD_TYPE.capitalize()
}

tasks.register("runUnitTests", Exec::class) {
    description = "Run all tests."
    commandLine("$rootDir/scripts/run-tests")
}

tasks.register("runBenchmarks", Exec::class) {
    description = "Run all benchmarks."
    commandLine("$rootDir/scripts/run-benchmarks")
}

tasks.register("updateDocumentations", Exec::class) {
    description = "Update HTML documentations through Dokka."
    commandLine("$rootDir/scripts/update-docs")
}

