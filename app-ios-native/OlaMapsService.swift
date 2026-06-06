import Foundation

final class OlaMapsService {

    private let apiKey: String = {
        Bundle.main.infoDictionary?["OLA_MAPS_API_KEY"] as? String ?? ""
    }()

    private let session: URLSession = {
        let cfg = URLSessionConfiguration.default
        cfg.timeoutIntervalForRequest = 15
        return URLSession(configuration: cfg)
    }()

    private let decoder: JSONDecoder = {
        let d = JSONDecoder()
        return d
    }()

    func autocomplete(input: String) async -> [Prediction] {
        guard input.count > 2,
              var comps = URLComponents(string: "https://api.olamaps.io/places/v1/autocomplete") else {
            return []
        }
        comps.queryItems = [
            .init(name: "input", value: input),
            .init(name: "api_key", value: apiKey)
        ]
        guard let url = comps.url else { return [] }
        do {
            let (data, _) = try await session.data(from: url)
            return (try? decoder.decode(AutocompleteResponse.self, from: data))?.predictions ?? []
        } catch {
            return []
        }
    }

    func getPlaceDetails(placeId: String) async -> PlaceDetails? {
        guard var comps = URLComponents(string: "https://api.olamaps.io/places/v1/details") else { return nil }
        comps.queryItems = [
            .init(name: "place_id", value: placeId),
            .init(name: "api_key", value: apiKey)
        ]
        guard let url = comps.url else { return nil }
        do {
            let (data, _) = try await session.data(from: url)
            return try? decoder.decode(PlaceDetailsResponse.self, from: data).result
        } catch {
            return nil
        }
    }

    func getDirections(origin: GeoLocation, destination: GeoLocation) async -> DirectionsRoute? {
        guard var comps = URLComponents(string: "https://api.olamaps.io/routing/v1/directions") else { return nil }
        comps.queryItems = [
            .init(name: "origin", value: "\(origin.lat),\(origin.lng)"),
            .init(name: "destination", value: "\(destination.lat),\(destination.lng)"),
            .init(name: "overview", value: "full"),
            .init(name: "alternatives", value: "false"),
            .init(name: "steps", value: "false"),
            .init(name: "api_key", value: apiKey)
        ]
        guard let url = comps.url else { return nil }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"

        do {
            let (data, response) = try await session.data(for: request)
            let statusCode = (response as? HTTPURLResponse)?.statusCode ?? 0
            let finalData: Data
            if statusCode == 405 || statusCode == 0 {
                let getReq = URLRequest(url: url)
                finalData = try await session.data(from: url).0
            } else {
                finalData = data
            }
            guard let json = try? JSONSerialization.jsonObject(with: finalData) else { return nil }
            return parseDirectionsRoute(from: json)
        } catch {
            do {
                let (data, _) = try await session.data(from: url)
                guard let json = try? JSONSerialization.jsonObject(with: data) else { return nil }
                return parseDirectionsRoute(from: json)
            } catch {
                return nil
            }
        }
    }
}
