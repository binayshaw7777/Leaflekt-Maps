import XCTest
@testable import LeaflektMap

final class LeaflektMapTests: XCTestCase {

    func testLatLngEquality() {
        let a = LeaflektLatLng(latitude: 22.5726, longitude: 88.3639)
        let b = LeaflektLatLng(latitude: 22.5726, longitude: 88.3639)
        XCTAssertEqual(a, b)
    }

    func testCameraPositionEquality() {
        let a = LeaflektCameraPosition(target: LeaflektLatLng(latitude: 22.0, longitude: 88.0), zoom: 12.0)
        let b = LeaflektCameraPosition(target: LeaflektLatLng(latitude: 22.0, longitude: 88.0), zoom: 12.0)
        XCTAssertEqual(a, b)
    }

    func testMapStyleRawValues() {
        XCTAssertEqual(LeaflektMapStyle.openStreetMap.rawValue, "open_street_map")
        XCTAssertEqual(LeaflektMapStyle.cartoDark.rawValue, "carto_dark")
    }
}
