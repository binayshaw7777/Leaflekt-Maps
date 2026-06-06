import Foundation

public struct LeaflektMapProperties {
    public let mapStyle: LeaflektMapStyle
    public let geoJsonOverlay: LeaflektGeoJsonOverlay

    public init(
        mapStyle: LeaflektMapStyle = .openStreetMap,
        geoJsonOverlay: LeaflektGeoJsonOverlay = .india
    ) {
        self.mapStyle = mapStyle
        self.geoJsonOverlay = geoJsonOverlay
    }
}
