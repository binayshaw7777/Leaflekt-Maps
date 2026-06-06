import SwiftUI
import WebKit

struct LeaflektMapRepresentable: UIViewRepresentable {
    let position: LeaflektCameraPosition
    let properties: LeaflektMapProperties
    let uiSettings: LeaflektMapUiSettings
    let controller: LeaflektMapController
    let onMapReady: (() -> Void)?
    let onMapClick: ((LeaflektLatLng) -> Void)?
    let onCameraIdle: ((LeaflektCameraPosition) -> Void)?
    let onMarkerClick: ((String) -> Void)?

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIView(context: Context) -> WKWebView {
        let bridge = LeaflektBridge()
        bridge.delegate = context.coordinator
        context.coordinator.bridge = bridge

        let userContentController = WKUserContentController()
        let weakHandler = WeakScriptMessageHandler(delegate: bridge)
        userContentController.add(weakHandler, name: "LeaflektIosBridge")

        let bridgeNameScript = WKUserScript(
            source: "window.bridgeName = 'LeaflektIosBridge';",
            injectionTime: .atDocumentStart,
            forMainFrameOnly: true
        )
        userContentController.addUserScript(bridgeNameScript)

        let config = WKWebViewConfiguration()
        config.userContentController = userContentController
        config.setURLSchemeHandler(LeaflektSchemeHandler(), forURLScheme: "leaflekt")

        let webView = WKWebView(frame: .zero, configuration: config)
        webView.navigationDelegate = context.coordinator
        webView.uiDelegate = context.coordinator

        controller.setWebView(webView)

        webView.load(URLRequest(url: URL(string: "leaflekt://localhost/map.html")!))

        return webView
    }

    func updateUIView(_ webView: WKWebView, context: Context) {
        context.coordinator.parent = self
    }

    final class Coordinator: NSObject, WKNavigationDelegate, WKUIDelegate, LeaflektBridgeDelegate {
        var parent: LeaflektMapRepresentable
        var bridge: LeaflektBridge?
        private var hasInitialized = false

        init(_ parent: LeaflektMapRepresentable) {
            self.parent = parent
        }

        func onMapReady() {
            if !hasInitialized {
                hasInitialized = true
                parent.controller.notifyMapReady()
                parent.controller.initialize(
                    position: parent.position,
                    uiSettings: parent.uiSettings,
                    properties: parent.properties
                )
                parent.onMapReady?()
            }
        }

        func onMapClick(lat: Double, lng: Double) {
            parent.onMapClick?(LeaflektLatLng(latitude: lat, longitude: lng))
        }

        func onCameraMoveStarted(lat: Double, lng: Double, zoom: Double) {}

        func onCameraMove(lat: Double, lng: Double, zoom: Double) {}

        func onCameraIdle(lat: Double, lng: Double, zoom: Double) {
            parent.onCameraIdle?(LeaflektCameraPosition(
                target: LeaflektLatLng(latitude: lat, longitude: lng),
                zoom: zoom
            ))
        }

        func onMarkerClick(markerId: String) {
            parent.onMarkerClick?(markerId)
        }

        func onPolylineClick(polylineId: String) {}
        func onPolygonClick(polygonId: String) {}
        func onCircleClick(circleId: String) {}

        func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
            completionHandler()
        }
    }
}
