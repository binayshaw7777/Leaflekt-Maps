import UIKit

extension UIColor {
    func cssRgba() -> String {
        var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
        getRed(&r, green: &g, blue: &b, alpha: &a)
        let ri = Int(r * 255), gi = Int(g * 255), bi = Int(b * 255)
        let aStr = String(format: "%.3f", Double(a))
        return "rgba(\(ri),\(gi),\(bi),\(aStr))"
    }
}
