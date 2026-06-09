import Foundation
import UIKit

public struct LeaflektPolygon {
    public let id: String
    public let points: [LeaflektLatLng]
    public let holes: [[LeaflektLatLng]]
    public let strokeColor: UIColor
    public let fillColor: UIColor
    public let strokeWidth: Double
    public let visible: Bool
    public let clickable: Bool
    public let zIndex: Double

    public init(
        id: String = UUID().uuidString,
        points: [LeaflektLatLng],
        holes: [[LeaflektLatLng]] = [],
        strokeColor: UIColor = .systemRed,
        fillColor: UIColor = UIColor.systemRed.withAlphaComponent(0.3),
        strokeWidth: Double = 2.0,
        visible: Bool = true,
        clickable: Bool = true,
        zIndex: Double = 0.0
    ) {
        self.id = id
        self.points = points
        self.holes = holes
        self.strokeColor = strokeColor
        self.fillColor = fillColor
        self.strokeWidth = strokeWidth
        self.visible = visible
        self.clickable = clickable
        self.zIndex = zIndex
    }
}
