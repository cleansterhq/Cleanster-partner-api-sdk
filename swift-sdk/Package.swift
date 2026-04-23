// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "Cleanster",
    platforms: [
        .macOS(.v13),
        .iOS(.v16),
        .watchOS(.v9),
        .tvOS(.v16),
    ],
    products: [
        .library(name: "Cleanster", targets: ["Cleanster"]),
    ],
    targets: [
        .target(
            name: "Cleanster",
            path: "Sources/Cleanster"
        ),
        .testTarget(
            name: "CleansterTests",
            dependencies: ["Cleanster"],
            path: "Tests/CleansterTests"
        ),
    ]
)
