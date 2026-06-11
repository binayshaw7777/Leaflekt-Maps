import Foundation
import UIKit

public struct LeaflektPolyline {
    public let id: String
    public let points: [LeaflektLatLng]
    public let color: UIColor
    public let width: Double
    public let visible: Bool
    public let clickable: Bool
    public let zIndex: Double

    public init(
        id: String = UUID().uuidString,
        points: [LeaflektLatLng],
        color: UIColor = .systemBlue,
        width: Double = 4.0,
        visible: Bool = true,
        clickable: Bool = true,
        zIndex: Double = 0.0
    ) {
        self.id = id
        self.points = points
        self.color = color
        self.width = width
        self.visible = visible
        self.clickable = clickable
        self.zIndex = zIndex
    }
}
