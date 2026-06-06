import Foundation
import UIKit

public struct LeaflektCircle {
    public let id: String
    public let center: LeaflektLatLng
    public let radiusMeters: Double
    public let strokeColor: UIColor
    public let fillColor: UIColor
    public let strokeWidth: Double
    public let visible: Bool
    public let clickable: Bool
    public let zIndex: Double

    public init(
        id: String = UUID().uuidString,
        center: LeaflektLatLng,
        radiusMeters: Double = 1000.0,
        strokeColor: UIColor = .systemGreen,
        fillColor: UIColor = UIColor.systemGreen.withAlphaComponent(0.25),
        strokeWidth: Double = 2.0,
        visible: Bool = true,
        clickable: Bool = true,
        zIndex: Double = 0.0
    ) {
        self.id = id
        self.center = center
        self.radiusMeters = radiusMeters
        self.strokeColor = strokeColor
        self.fillColor = fillColor
        self.strokeWidth = strokeWidth
        self.visible = visible
        self.clickable = clickable
        self.zIndex = zIndex
    }
}
