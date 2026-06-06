import Foundation

public struct LeaflektCameraPosition: Equatable {
    public let target: LeaflektLatLng
    public let zoom: Double

    public init(target: LeaflektLatLng, zoom: Double) {
        self.target = target
        self.zoom = zoom
    }
}
