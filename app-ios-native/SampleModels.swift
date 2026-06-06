import Foundation
import LeaflektMap

// MARK: - Autocomplete

struct AutocompleteResponse: Codable {
    let predictions: [Prediction]
    let status: String?
    let errorMessage: String?

    enum CodingKeys: String, CodingKey {
        case predictions, status
        case errorMessage = "error_message"
    }

    init(from decoder: Decoder) throws {
        let c = try decoder.container(keyedBy: CodingKeys.self)
        predictions = (try? c.decode([Prediction].self, forKey: .predictions)) ?? []
        status = try? c.decode(String.self, forKey: .status)
        errorMessage = try? c.decode(String.self, forKey: .errorMessage)
    }
}

struct Prediction: Codable, Identifiable {
    var id: String { placeId }
    let description: String
    let placeId: String
    let structuredFormatting: StructuredFormatting?

    enum CodingKeys: String, CodingKey {
        case description
        case placeId = "place_id"
        case structuredFormatting = "structured_formatting"
    }
}

struct StructuredFormatting: Codable {
    let mainText: String
    let secondaryText: String?

    enum CodingKeys: String, CodingKey {
        case mainText = "main_text"
        case secondaryText = "secondary_text"
    }
}

// MARK: - Place Details

struct PlaceDetailsResponse: Codable {
    let result: PlaceDetails?
    let status: String?
}

struct PlaceDetails: Codable, Equatable {
    let geometry: Geometry?
    let name: String?
    let formattedAddress: String?

    enum CodingKeys: String, CodingKey {
        case geometry, name
        case formattedAddress = "formatted_address"
    }

    func headline() -> String {
        name?.isEmpty == false ? name! : (formattedAddress ?? "Selected place")
    }

    func supportingLine() -> String? {
        guard let addr = formattedAddress, !addr.isEmpty, addr != name else { return nil }
        return addr
    }
}

struct Geometry: Codable, Equatable {
    let location: GeoLocation?
}

struct GeoLocation: Codable, Equatable {
    let lat: Double
    let lng: Double

    var latLng: LeaflektLatLng { LeaflektLatLng(latitude: lat, longitude: lng) }
}

// MARK: - Directions

enum DirectionsEndpoint { case origin, destination }

struct DirectionsRoute: Equatable {
    let points: [LeaflektLatLng]
    let distanceMeters: Double?
    let durationSeconds: Double?
    let summary: String?

    func cameraTarget() -> LeaflektLatLng {
        guard let first = points.first, let last = points.last else {
            return LeaflektLatLng(latitude: 22.5726, longitude: 88.3639)
        }
        return LeaflektLatLng(
            latitude: (first.latitude + last.latitude) / 2,
            longitude: (first.longitude + last.longitude) / 2
        )
    }

    func recommendedZoom() -> Double {
        guard let first = points.first, let last = points.last else { return 12 }
        let span = max(abs(first.latitude - last.latitude), abs(first.longitude - last.longitude))
        switch span {
        case 8...: return 4.5
        case 4...: return 5.5
        case 2...: return 6.5
        case 1...: return 7.5
        case 0.5...: return 9.0
        case 0.2...: return 10.5
        case 0.1...: return 11.5
        case 0.05...: return 12.5
        default: return 13.5
        }
    }

    func distanceLabel() -> String? {
        guard let d = distanceMeters else { return nil }
        return d >= 1000 ? String(format: "%.1f km", d / 1000) : "\(Int(d)) m"
    }

    func durationLabel() -> String? {
        guard let s = durationSeconds else { return nil }
        let mins = Int(s / 60)
        let h = mins / 60, m = mins % 60
        if h > 0 && m > 0 { return "\(h)h \(m)m" }
        if h > 0 { return "\(h)h" }
        return "\(m)m"
    }
}

// MARK: - Directions JSON parsing

func parseDirectionsRoute(from json: Any) -> DirectionsRoute? {
    guard let root = json as? [String: Any],
          let routes = root["routes"] as? [[String: Any]],
          let first = routes.first else { return nil }

    let points = routePoints(from: first)
    guard !points.isEmpty else { return nil }

    let distance = (first["distance"] as? Double)
        ?? (first["distance_meters"] as? Double)
        ?? legSum(first, key: "distance")
    let duration = (first["duration"] as? Double)
        ?? (first["duration_seconds"] as? Double)
        ?? legSum(first, key: "duration")
    let summary = first["summary"] as? String

    return DirectionsRoute(points: points, distanceMeters: distance, durationSeconds: duration, summary: summary)
}

private func routePoints(from route: [String: Any]) -> [LeaflektLatLng] {
    let keys = ["overview_polyline", "overviewPolyline", "geometry", "polyline", "route"]
    for key in keys {
        if let src = route[key] {
            if let points = decodePolylineSource(src) { return points }
        }
    }
    return stepPoints(from: route) ?? []
}

private func stepPoints(from route: [String: Any]) -> [LeaflektLatLng]? {
    guard let legs = route["legs"] as? [[String: Any]] else { return nil }
    var all: [LeaflektLatLng] = []
    for leg in legs {
        for step in (leg["steps"] as? [[String: Any]] ?? []) {
            let src = step["polyline"] ?? step["geometry"]
            all += decodePolylineSource(src) ?? []
        }
    }
    return all.isEmpty ? nil : removeAdjacentDuplicates(all)
}

private func decodePolylineSource(_ src: Any?) -> [LeaflektLatLng]? {
    guard let src = src else { return nil }
    if let s = src as? String { return decodePolyline(s) }
    if let d = src as? [String: Any] {
        let polylineKey = ["points", "polyline", "encodedPolyline", "encoded_polyline"].compactMap { d[$0] as? String }.first
        if let enc = polylineKey { return decodePolyline(enc) }
        let coordKey = d["coordinates"] ?? d["path"]
        if let arr = coordKey as? [[Double]] {
            return arr.compactMap { c in c.count >= 2 ? LeaflektLatLng(latitude: c[1], longitude: c[0]) : nil }
        }
    }
    if let arr = src as? [[Double]] {
        return arr.compactMap { c in c.count >= 2 ? LeaflektLatLng(latitude: c[1], longitude: c[0]) : nil }
    }
    return nil
}

private func legSum(_ route: [String: Any], key: String) -> Double? {
    guard let legs = route["legs"] as? [[String: Any]] else { return nil }
    let sum = legs.compactMap { $0[key] as? Double }.reduce(0, +)
    return sum > 0 ? sum : nil
}

private func removeAdjacentDuplicates(_ pts: [LeaflektLatLng]) -> [LeaflektLatLng] {
    guard !pts.isEmpty else { return pts }
    var result = [pts[0]]
    for p in pts.dropFirst() {
        let last = result.last!
        if last.latitude != p.latitude || last.longitude != p.longitude { result.append(p) }
    }
    return result
}

func decodePolyline(_ encoded: String) -> [LeaflektLatLng] {
    var points: [LeaflektLatLng] = []
    var index = encoded.startIndex
    var lat = 0, lng = 0

    while index < encoded.endIndex {
        lat += decodePolylineChunk(encoded, index: &index)
        lng += decodePolylineChunk(encoded, index: &index)
        points.append(LeaflektLatLng(latitude: Double(lat) / 1e5, longitude: Double(lng) / 1e5))
    }
    return points
}

private func decodePolylineChunk(_ s: String, index: inout String.Index) -> Int {
    var shift = 0, result = 0, chunk: Int
    repeat {
        chunk = Int(s[index].asciiValue ?? 63) - 63
        index = s.index(after: index)
        result |= (chunk & 0x1F) << shift
        shift += 5
    } while chunk >= 0x20
    return (result & 1) != 0 ? ~(result >> 1) : (result >> 1)
}
