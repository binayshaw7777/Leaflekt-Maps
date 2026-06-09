// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "LeaflektMap",
    platforms: [
        .iOS(.v15)
    ],
    products: [
        .library(name: "LeaflektMap", targets: ["LeaflektMap"])
    ],
    targets: [
        .target(
            name: "LeaflektMap",
            resources: [
                .process("Resources")
            ]
        ),
        .testTarget(
            name: "LeaflektMapTests",
            dependencies: ["LeaflektMap"]
        )
    ]
)
