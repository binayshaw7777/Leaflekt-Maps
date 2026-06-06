import WebKit
import Foundation
import UIKit

public final class LeaflektMapController {
    private weak var webView: WKWebView?
    private var pendingScripts: [String] = []
    private var isMapReady = false

    private var markerClickHandlers: [String: () -> Void] = [:]

    internal func setWebView(_ webView: WKWebView) {
        self.webView = webView
    }

    internal func notifyMapReady() {
        isMapReady = true
        let pending = pendingScripts
        pendingScripts.removeAll()
        pending.forEach { executeJs($0) }
    }

    private func enqueueOrRun(_ script: String) {
        if isMapReady {
            executeJs(script)
        } else {
            pendingScripts.append(script)
        }
    }

    private func executeJs(_ script: String) {
        DispatchQueue.main.async { [weak self] in
            self?.webView?.evaluateJavaScript(script, completionHandler: nil)
        }
    }

    public func moveCamera(lat: Double, lng: Double, zoom: Double) {
        enqueueOrRun("window.LeaflektBridge.moveCamera(\(lat),\(lng),\(zoom));")
    }

    public func setZoomControlsEnabled(_ isEnabled: Bool) {
        enqueueOrRun("window.LeaflektBridge.setZoomControlsEnabled(\(isEnabled));")
    }

    public func executeJavaScript(_ script: String) {
        enqueueOrRun(script)
    }

    public func addMarker(_ marker: LeaflektMarker) {
        let titleJson = marker.title.map { "\"\($0.replacingOccurrences(of: "\"", with: "\\\""))\"" } ?? "null"
        let iconJson: String
        if let icon = marker.icon {
            let escapedUrl = icon.dataUrl.replacingOccurrences(of: "\"", with: "\\\"")
            iconJson = "{\"dataUrl\":\"\(escapedUrl)\",\"widthPx\":\(icon.widthPx),\"heightPx\":\(icon.heightPx),\"anchorFractionX\":\(icon.anchorFractionX),\"anchorFractionY\":\(icon.anchorFractionY)}"
        } else {
            iconJson = "null"
        }
        let script = "window.LeaflektBridge.addMarkers([{\"id\":\"\(marker.id)\",\"lat\":\(marker.position.latitude),\"lng\":\(marker.position.longitude),\"title\":\(titleJson),\"visible\":\(marker.visible),\"alpha\":\(marker.alpha),\"zIndex\":\(marker.zIndex),\"rotationDegrees\":\(marker.rotationDegrees),\"icon\":\(iconJson)}]);"
        enqueueOrRun(script)
    }

    public func removeMarker(id: String) {
        enqueueOrRun("window.LeaflektBridge.removeMarker('\(id)');")
    }

    public func addPolyline(_ polyline: LeaflektPolyline) {
        let pointsJson = polyline.points.map { "{\"latitude\":\($0.latitude),\"longitude\":\($0.longitude)}" }.joined(separator: ",")
        let colorJson = polyline.color.cssRgba()
        let script = "window.LeaflektBridge.addPolyline({\"id\":\"\(polyline.id)\",\"points\":[\(pointsJson)],\"clickable\":\(polyline.clickable),\"color\":\"\(colorJson)\",\"geodesic\":false,\"pattern\":null,\"visible\":\(polyline.visible),\"width\":\(polyline.width),\"zIndex\":\(polyline.zIndex)});"
        enqueueOrRun(script)
    }

    public func updatePolyline(_ polyline: LeaflektPolyline) {
        removePolyline(id: polyline.id)
        addPolyline(polyline)
    }

    public func removePolyline(id: String) {
        enqueueOrRun("window.LeaflektBridge.removePolyline('\(id)');")
    }

    public func addPolygon(_ polygon: LeaflektPolygon) {
        let pointsJson = polygon.points.map { "{\"latitude\":\($0.latitude),\"longitude\":\($0.longitude)}" }.joined(separator: ",")
        let holesJson = polygon.holes.map { hole in
            "[" + hole.map { "{\"latitude\":\($0.latitude),\"longitude\":\($0.longitude)}" }.joined(separator: ",") + "]"
        }.joined(separator: ",")
        let strokeJson = polygon.strokeColor.cssRgba()
        let fillJson = polygon.fillColor.cssRgba()
        let script = "window.LeaflektBridge.addPolygon({\"id\":\"\(polygon.id)\",\"points\":[\(pointsJson)],\"clickable\":\(polygon.clickable),\"fillColor\":\"\(fillJson)\",\"geodesic\":false,\"holes\":[\(holesJson)],\"strokeColor\":\"\(strokeJson)\",\"strokePattern\":null,\"strokeWidth\":\(polygon.strokeWidth),\"visible\":\(polygon.visible),\"zIndex\":\(polygon.zIndex)});"
        enqueueOrRun(script)
    }

    public func updatePolygon(_ polygon: LeaflektPolygon) {
        removePolygon(id: polygon.id)
        addPolygon(polygon)
    }

    public func removePolygon(id: String) {
        enqueueOrRun("window.LeaflektBridge.removePolygon('\(id)');")
    }

    public func addCircle(_ circle: LeaflektCircle) {
        let strokeJson = circle.strokeColor.cssRgba()
        let fillJson = circle.fillColor.cssRgba()
        let script = "window.LeaflektBridge.addCircle({\"id\":\"\(circle.id)\",\"center\":{\"latitude\":\(circle.center.latitude),\"longitude\":\(circle.center.longitude)},\"clickable\":\(circle.clickable),\"fillColor\":\"\(fillJson)\",\"radiusMeters\":\(circle.radiusMeters),\"strokeColor\":\"\(strokeJson)\",\"strokePattern\":null,\"strokeWidth\":\(circle.strokeWidth),\"visible\":\(circle.visible),\"zIndex\":\(circle.zIndex)});"
        enqueueOrRun(script)
    }

    public func updateCircle(_ circle: LeaflektCircle) {
        removeCircle(id: circle.id)
        addCircle(circle)
    }

    public func removeCircle(id: String) {
        enqueueOrRun("window.LeaflektBridge.removeCircle('\(id)');")
    }

    public func clearMarkers() {
        enqueueOrRun("window.LeaflektBridge.clearMarkers();")
    }

    public func createClusterGroup(groupId: String, maxClusterRadius: Int = 80) {
        let escaped = groupId.replacingOccurrences(of: "\"", with: "\\\"")
        enqueueOrRun("window.LeaflektBridge.createClusterGroup(\"\(escaped)\",\(maxClusterRadius));")
    }

    public func addMarkersToCluster(groupId: String, markers: [LeaflektMarker]) {
        guard !markers.isEmpty else { return }
        let escaped = groupId.replacingOccurrences(of: "\"", with: "\\\"")
        let markersJson = markers.map { m in
            let titleJson = m.title.map { "\"\($0.replacingOccurrences(of: "\"", with: "\\\""))\"" } ?? "null"
            let iconJson: String
            if let icon = m.icon {
                let escapedUrl = icon.dataUrl.replacingOccurrences(of: "\"", with: "\\\"")
                iconJson = "{\"dataUrl\":\"\(escapedUrl)\",\"widthPx\":\(icon.widthPx),\"heightPx\":\(icon.heightPx),\"anchorFractionX\":\(icon.anchorFractionX),\"anchorFractionY\":\(icon.anchorFractionY)}"
            } else {
                iconJson = "null"
            }
            return "{\"id\":\"\(m.id)\",\"lat\":\(m.position.latitude),\"lng\":\(m.position.longitude),\"title\":\(titleJson),\"visible\":\(m.visible),\"alpha\":\(m.alpha),\"zIndex\":\(m.zIndex),\"rotationDegrees\":\(m.rotationDegrees),\"icon\":\(iconJson)}"
        }.joined(separator: ",")
        enqueueOrRun("window.LeaflektBridge.addMarkersToCluster(\"\(escaped)\",[\(markersJson)]);")
    }

    public func removeClusterGroup(groupId: String) {
        let escaped = groupId.replacingOccurrences(of: "\"", with: "\\\"")
        enqueueOrRun("window.LeaflektBridge.removeClusterGroup(\"\(escaped)\");")
    }

    public func setZoomBounds(min minZoom: Double, max maxZoom: Double) {
        enqueueOrRun("window.LeaflektBridge.setZoomBounds(\(minZoom),\(maxZoom));")
    }

    public func setMapStyle(_ style: LeaflektMapStyle) {
        let styleJson = "{\"id\":\"\(style.rawValue)\",\"tileUrlTemplate\":\"\(style.tileUrl)\",\"attributionHtml\":\"\(style.attribution)\",\"maxZoom\":\(style.maxZoom),\"subdomains\":null}"
        enqueueOrRun("window.LeaflektBridge.setMapStyle(\(styleJson));")
    }

    public func setGeoJsonOverlay(_ overlay: LeaflektGeoJsonOverlay) {
        switch overlay {
        case .india:
            enqueueOrRun("window.LeaflektBridge.setGeoJsonOverlay(null);")
        case .none:
            enqueueOrRun("window.LeaflektBridge.setGeoJsonOverlay(\"none\");")
        case .custom(let geojson):
            let escaped = geojson
                .replacingOccurrences(of: "\\", with: "\\\\")
                .replacingOccurrences(of: "\"", with: "\\\"")
                .replacingOccurrences(of: "\n", with: "\\n")
                .replacingOccurrences(of: "\r", with: "")
            enqueueOrRun("window.LeaflektBridge.setGeoJsonOverlay(\"\(escaped)\");")
        }
    }

    internal func initialize(position: LeaflektCameraPosition, uiSettings: LeaflektMapUiSettings, properties: LeaflektMapProperties) {
        let lat = position.target.latitude
        let lng = position.target.longitude
        let zoom = position.zoom
        enqueueOrRun("window.LeaflektBridge.initMap(\(lat),\(lng),\(zoom));")
        enqueueOrRun("window.LeaflektBridge.setZoomControlsEnabled(\(uiSettings.zoomControlsEnabled));")
        let style = properties.mapStyle
        let styleJson = "{\"id\":\"\(style.rawValue)\",\"tileUrlTemplate\":\"\(style.tileUrl)\",\"attributionHtml\":\"\(style.attribution)\",\"maxZoom\":\(style.maxZoom),\"subdomains\":null}"
        enqueueOrRun("window.LeaflektBridge.setMapStyle(\(styleJson));")
        if case .india = properties.geoJsonOverlay { } else {
            setGeoJsonOverlay(properties.geoJsonOverlay)
        }
    }
}
