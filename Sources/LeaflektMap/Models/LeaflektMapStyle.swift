import Foundation

public enum LeaflektMapStyle: String, CaseIterable {
    case openStreetMap = "open_street_map"
    case cartoLight = "carto_light"
    case cartoDark = "carto_dark"
    case openTopoMap = "open_topo_map"
    case esriWorldImagery = "esri_world_imagery"

    var tileUrl: String {
        switch self {
        case .openStreetMap: return "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        case .cartoLight: return "https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png"
        case .cartoDark: return "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
        case .openTopoMap: return "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png"
        case .esriWorldImagery: return "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
        }
    }

    var attribution: String {
        switch self {
        case .openStreetMap: return "&copy; <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors"
        case .cartoLight, .cartoDark: return "&copy; <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors &copy; <a href='https://carto.com/attributions'>CARTO</a>"
        case .openTopoMap: return "Map data: &copy; OpenStreetMap contributors | Map style: &copy; OpenTopoMap"
        case .esriWorldImagery: return "Tiles &copy; Esri"
        }
    }

    var maxZoom: Int {
        switch self {
        case .openTopoMap: return 17
        default: return 19
        }
    }
}
