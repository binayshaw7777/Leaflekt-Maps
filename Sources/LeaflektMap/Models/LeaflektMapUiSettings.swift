import Foundation

public struct LeaflektMapUiSettings {
    public let zoomControlsEnabled: Bool
    public let scrollGesturesEnabled: Bool
    public let zoomGesturesEnabled: Bool

    public init(
        zoomControlsEnabled: Bool = true,
        scrollGesturesEnabled: Bool = true,
        zoomGesturesEnabled: Bool = true
    ) {
        self.zoomControlsEnabled = zoomControlsEnabled
        self.scrollGesturesEnabled = scrollGesturesEnabled
        self.zoomGesturesEnabled = zoomGesturesEnabled
    }
}
