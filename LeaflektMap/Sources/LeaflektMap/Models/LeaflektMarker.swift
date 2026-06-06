import Foundation

public struct LeaflektMarker {
    public let id: String
    public let position: LeaflektLatLng
    public let title: String?
    public let visible: Bool
    public let alpha: Double
    public let zIndex: Int
    public let rotationDegrees: Double
    public let icon: LeaflektMarkerIcon?

    public init(
        id: String = UUID().uuidString,
        position: LeaflektLatLng,
        title: String? = nil,
        visible: Bool = true,
        alpha: Double = 1.0,
        zIndex: Int = 0,
        rotationDegrees: Double = 0,
        icon: LeaflektMarkerIcon? = nil
    ) {
        self.id = id
        self.position = position
        self.title = title
        self.visible = visible
        self.alpha = alpha
        self.zIndex = zIndex
        self.rotationDegrees = rotationDegrees
        self.icon = icon
    }
}
