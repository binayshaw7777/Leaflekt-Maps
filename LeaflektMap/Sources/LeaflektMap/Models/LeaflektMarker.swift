import Foundation

public struct LeaflektMarker {
    public let id: String
    public let position: LeaflektLatLng
    public let title: String?
    public let visible: Bool

    public init(id: String = UUID().uuidString, position: LeaflektLatLng, title: String? = nil, visible: Bool = true) {
        self.id = id
        self.position = position
        self.title = title
        self.visible = visible
    }
}
