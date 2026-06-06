import Foundation

public struct LeaflektMarkerIcon {
    public let dataUrl: String
    public let widthPx: Int
    public let heightPx: Int
    public let anchorFractionX: Double
    public let anchorFractionY: Double

    public init(dataUrl: String, widthPx: Int, heightPx: Int, anchorFractionX: Double = 0.5, anchorFractionY: Double = 1.0) {
        self.dataUrl = dataUrl
        self.widthPx = widthPx
        self.heightPx = heightPx
        self.anchorFractionX = anchorFractionX
        self.anchorFractionY = anchorFractionY
    }
}
