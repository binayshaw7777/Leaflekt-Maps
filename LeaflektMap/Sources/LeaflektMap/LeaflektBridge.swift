import WebKit
import Foundation

protocol LeaflektBridgeDelegate: AnyObject {
    func onMapReady()
    func onMapFirstRender()
    func onMapClick(lat: Double, lng: Double)
    func onCameraMoveStarted(lat: Double, lng: Double, zoom: Double)
    func onCameraMove(lat: Double, lng: Double, zoom: Double)
    func onCameraIdle(lat: Double, lng: Double, zoom: Double)
    func onMarkerClick(markerId: String)
    func onPolylineClick(polylineId: String)
    func onPolygonClick(polygonId: String)
    func onCircleClick(circleId: String)
}

final class LeaflektBridge: NSObject, WKScriptMessageHandler {
    weak var delegate: LeaflektBridgeDelegate?

    func userContentController(
        _ userContentController: WKUserContentController,
        didReceive message: WKScriptMessage
    ) {
        guard let body = message.body as? String else { return }
        let parts = body.split(separator: ":", maxSplits: 4).map(String.init)
        guard let event = parts.first else { return }

        switch event {
        case "onMapReady":
            delegate?.onMapReady()
        case "onMapFirstRender":
            delegate?.onMapFirstRender()
        case "onMapClick":
            guard let lat = Double(parts.at(1) ?? ""), let lng = Double(parts.at(2) ?? "") else { return }
            delegate?.onMapClick(lat: lat, lng: lng)
        case "onCameraMoveStarted":
            guard let lat = Double(parts.at(1) ?? ""), let lng = Double(parts.at(2) ?? ""), let zoom = Double(parts.at(3) ?? "") else { return }
            delegate?.onCameraMoveStarted(lat: lat, lng: lng, zoom: zoom)
        case "onCameraMove":
            guard let lat = Double(parts.at(1) ?? ""), let lng = Double(parts.at(2) ?? ""), let zoom = Double(parts.at(3) ?? "") else { return }
            delegate?.onCameraMove(lat: lat, lng: lng, zoom: zoom)
        case "onCameraIdle":
            guard let lat = Double(parts.at(1) ?? ""), let lng = Double(parts.at(2) ?? ""), let zoom = Double(parts.at(3) ?? "") else { return }
            delegate?.onCameraIdle(lat: lat, lng: lng, zoom: zoom)
        case "onMarkerClick":
            delegate?.onMarkerClick(markerId: parts.at(1) ?? "")
        case "onPolylineClick":
            delegate?.onPolylineClick(polylineId: parts.at(1) ?? "")
        case "onPolygonClick":
            delegate?.onPolygonClick(polygonId: parts.at(1) ?? "")
        case "onCircleClick":
            delegate?.onCircleClick(circleId: parts.at(1) ?? "")
        default:
            break
        }
    }
}

private extension Array {
    func at(_ index: Int) -> Element? {
        guard index >= 0 && index < count else { return nil }
        return self[index]
    }
}
