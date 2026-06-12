import Foundation

public struct LeaflektMapProperties {
    public let mapStyle: LeaflektMapStyle
    public let geoJsonOverlay: LeaflektGeoJsonOverlay
    public let tileBufferSize: Int

    public init(
        mapStyle: LeaflektMapStyle = .openStreetMap,
        geoJsonOverlay: LeaflektGeoJsonOverlay = .india,
        tileBufferSize: Int = {
            let ramMb = Int(ProcessInfo.processInfo.physicalMemory / (1024 * 1024))
            if ramMb < 2048 { return 4 }
            if ramMb < 4096 { return 8 }
            return 12
        }()
    ) {
        self.mapStyle = mapStyle
        self.geoJsonOverlay = geoJsonOverlay
        self.tileBufferSize = tileBufferSize
    }
}
