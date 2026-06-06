import Foundation
import WebKit

final class LeaflektSchemeHandler: NSObject, WKURLSchemeHandler {

    func webView(_ webView: WKWebView, start urlSchemeTask: WKURLSchemeTask) {
        guard let url = urlSchemeTask.request.url else {
            urlSchemeTask.didFailWithError(URLError(.badURL))
            return
        }

        let raw = url.path
        let relativePath = raw.hasPrefix("/") ? String(raw.dropFirst()) : raw
        let filePath = relativePath.isEmpty ? "map.html" : relativePath

        // SPM flattens resources to bundle root — strip any directory prefix
        let bundlePath = Bundle.module.bundleURL.path
        let filename = (filePath as NSString).lastPathComponent
        let fullPath = bundlePath + "/" + filename

        guard let data = FileManager.default.contents(atPath: fullPath) else {
            urlSchemeTask.didFailWithError(URLError(.fileDoesNotExist))
            return
        }

        let ext = (filePath as NSString).pathExtension
        let response = URLResponse(
            url: url,
            mimeType: mimeType(for: ext),
            expectedContentLength: data.count,
            textEncodingName: "utf-8"
        )
        urlSchemeTask.didReceive(response)
        urlSchemeTask.didReceive(data)
        urlSchemeTask.didFinish()
    }

    func webView(_ webView: WKWebView, stop urlSchemeTask: WKURLSchemeTask) {}

    private func mimeType(for ext: String) -> String {
        switch ext.lowercased() {
        case "html": return "text/html"
        case "css":  return "text/css"
        case "js":   return "application/javascript"
        case "json", "geojson": return "application/json"
        case "png":  return "image/png"
        case "jpg", "jpeg": return "image/jpeg"
        case "svg":  return "image/svg+xml"
        case "gif":  return "image/gif"
        default:     return "application/octet-stream"
        }
    }
}
