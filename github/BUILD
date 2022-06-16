java_binary(
    name = "github-bin-mac",
    data = [
        "//github/datasets:github-data-json",
        "//github/schemas:github-data-schemas",
    ],
    main_class = "github.view.GitApplication",
    runtime_deps = [
        "//github/view",
        "@maven//:org_jetbrains_skiko_skiko_awt_runtime_macos_x64",
    ],
)

java_binary(
    name = "github-bin-windows",
    data = [
        "//github/datasets:github-data-json",
        "//github/schemas:github-data-schemas",
    ],
    main_class = "github.view.GitApplication",
    runtime_deps = [
        "//github/view",
        "@maven//:org_jetbrains_skiko_skiko_awt_runtime_windows_x64",
    ],
)

java_binary(
    name = "github-bin-linux",
    data = [
        "//github/datasets:github-data-json",
        "//github/schemas:github-data-schemas",
    ],
    main_class = "github.view.GitApplication",
    runtime_deps = [
        "//github/view",
        "@maven//:org_jetbrains_skiko_skiko_awt_runtime_linux_x64",
    ],
)
