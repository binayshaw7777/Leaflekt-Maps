# LeafleKT Test Plan

## Overview
This document outlines the test cases for LeafleKT, a Compose-first Android wrapper around Leaflet.js. Tests cover normal, edge, and exceptional cases for all major components.

## Test Categories

### 1. MapView Tests
#### Normal Cases
- Map initializes with default properties
- Map responds to camera position state changes
- Map style switching works correctly
- UI settings (zoom controls, gestures) are properly applied
- Map ready callback is invoked when initialization completes
- Map loaded callback is invoked when Leaflet engine is ready
- Click callbacks work for map, markers, polylines, polygons, circles
- Map supports various styles including OpenStreetMap, CartoLight, CartoDark, OpenTopoMap
- Map supports custom tile URLs from providers like OpenMapTiles
- Map supports OpenMapTiles styles (positron, voyager, etc.)
- Map supports OSM standard raster tiles
- All supported styles work without authentication, login, or API keys (attribution only required)
- Map supports standard OSM raster tiles (tile.openstreetmap.org) with adherence to usage policy

#### Edge Cases
- Map with null modifier
- Map with empty content
- Rapid camera position updates
- Map style changes during initialization
- UI settings toggled rapidly
- Map initialized with zero size
- Map initialized with extremely large size

#### Exceptional Cases
- Map initialization fails (WebView creation error)
- JavaScript bridge communication failure
- Invalid camera position values (NaN, Infinity)
- Map style set to null or invalid value
- Memory leak scenarios with rapid map creation/destruction

### 2. Marker Tests
#### Normal Cases
- Marker renders at correct position
- Marker title and snippet display correctly
- Marker click callbacks work
- Marker visibility toggles correctly
- Marker alpha/transparency works
- Marker rotation works correctly
- Marker z-index ordering works

#### Edge Cases
- Marker at map boundaries (±90 latitude, ±180 longitude)
- Marker at poles (90, -90 latitude)
- Marker with extremely long title/snippet
- Marker with special characters in title/snippet
- Marker with zero alpha (fully transparent)
- Marker with maximum rotation (360 degrees)
- Marker with fractional rotation values

#### Exceptional Cases
- Marker with null position
- Marker with NaN/Infinity coordinates
- Marker click during rapid map movement
- Marker state updates during animation
- Memory leak with rapid marker creation/destruction

### 3. Polyline Tests
#### Normal Cases
- Polyline renders with correct points
- Polyline color, width, alpha work correctly
- Polyline stroke pattern application
- Polyline click callbacks work
- Polyline visibility toggles correctly
- Polyline z-index ordering works

#### Edge Cases
- Polyline with single point
- Polyline with two points (minimum for line)
- Polyline with many points (performance test)
- Polyline that crosses the date line (-180/180 longitude)
- Polyline with zero width
- Polyline with maximum width
- Polyline with complex stroke patterns

#### Exceptional Cases
- Polyline with null points list
- Polyline with empty points list
- Polyline with NaN/Infinity coordinates
- Polyline click during rapid map movement
- Memory leak with rapid polyline creation/destruction

### 4. Polygon Tests
#### Normal Cases
- Polygon renders with correct points
- Polygon fill and stroke colors work
- Polygon fill/stroke opacity work
- Polygon stroke pattern application
- Polygon click callbacks work
- Polygon visibility toggles correctly
- Polygon with holes renders correctly
- Polygon z-index ordering works

#### Edge Cases
- Polygon with minimum points (triangle)
- Polygon with many points (performance test)
- Polygon that crosses the date line
- Polygon with self-intersecting paths
- Polygon with zero area (collinear points)
- Polygon with maximum coordinates
- Polygon with complex hole structures

#### Exceptional Cases
- Polygon with null points list
- Polygon with empty points list
- Polygon with less than 3 points
- Polygon with NaN/Infinity coordinates
- Polygon click during rapid map movement
- Memory leak with rapid polygon creation/destruction

### 5. Circle Tests
#### Normal Cases
- Circle renders at correct center point
- Circle radius renders correctly in meters
- Circle fill and stroke colors work
- Circle fill/stroke opacity work
- Circle stroke pattern application
- Circle click callbacks work
- Circle visibility toggles correctly
- Circle z-index ordering works

#### Edge Cases
- Circle with zero radius
- Circle with very small radius (sub-meter)
- Circle with very large radius (covering significant map area)
- Circle at map boundaries
- Circle that crosses the date line
- Circle with maximum radius value

#### Exceptional Cases
- Circle with null center
- Circle with NaN/Infinity coordinates
- Circle with negative radius
- Circle click during rapid map movement
- Memory leak with rapid circle creation/destruction

### 6. Marker Icon Tests
#### Normal Cases
- Synchronous bitmap marker icon loads correctly
- App-provided asynchronous marker icon loading works
- Marker icon anchoring works correctly
- Marker icon size scaling works
- Marker icon tinting works
- Composable marker icon renders correctly
- Composable marker icon anchoring works
- Combined bitmap + composable icon works

#### Edge Cases
- Marker icon with zero size
- Marker icon with extremely large size
- Marker icon with fractional anchor values (0.0, 1.0)
- Marker icon loaded from invalid remote source
- Marker icon loaded from invalid resource ID
- Marker icon with null bitmap (sync)
- Rapid icon changes during loading

#### Exceptional Cases
- Marker icon with null app-provided model
- Marker icon loading failure (network error, invalid format)
- Marker icon memory leak during rapid changes
- Marker icon with corrupted image data
- Marker icon that causes OOM (large image)

### 7. Info Window Tests
#### Normal Cases
- Info window displays when marker clicked
- Info window displays when isInfoWindowVisible=true
- Custom info window renders correctly
- Info window anchor positioning works
- Info window with long content displays correctly
- Info window auto-close behavior works

#### Edge Cases
- Info window with empty content
- Info window with very long title/snippet
- Info window with special characters
- Info window at map boundaries
- Info window anchor at extreme positions (0,0 and 1,1)
- Multiple info windows open simultaneously

#### Exceptional Cases
- Info window with null content
- Info window during rapid marker movement
- Info window click-through behavior
- Info window memory leak
- Info window rendering errors

### 8. Camera Tests
#### Normal Cases
- Camera movement responds to user gestures
- Camera movement responds to programmatic changes
- Camera bounds (min/max zoom) work correctly
- Camera movement callbacks fire correctly
- Camera position state sync works bidirectionally
- Camera rotation works correctly

#### Edge Cases
- Camera at minimum zoom level
- Camera at maximum zoom level
- Camera movement beyond bounds (should be clamped)
- Camera movement to invalid coordinates
- Rapid camera movements
- Camera movement during animation

#### Exceptional Cases
- Camera with NaN/Infinity position values
- Camera with invalid zoom (negative, NaN, Infinity)
- Camera movement during map destruction
- Camera controller memory leak

### 9. Theme Sync Tests
#### Normal Cases
- Map switches to dark theme in dark mode
- Map switches to light theme in light mode
- Theme switch works with automaticThemeSync enabled
- Theme switch respects manual style when disabled
- Theme switch during map initialization

#### Edge Cases
- Rapid theme switching
- Theme switch during map interaction
- Theme switch with custom map styles
- Theme switch at boundary conditions (dawn/dusk detection simulation)

#### Exceptional Cases
- Theme switch with invalid map styles
- Theme switch during map destruction
- Theme switch memory leak

### 10. Current Location Tests
#### Normal Cases
- Current location overlay shows when enabled
- Current location overlay hides when disabled
- Custom current location icon works
- Location permission request flow works
- Location updates work correctly
- Accuracy ring displays correctly

#### Edge Cases
- Current location at map boundaries
- Current location with zero accuracy
- Current location with very high accuracy
- Rapid location updates
- Location updates during map movement
- Location permission denied
- Location permission granted after initial denial

#### Exceptional Cases
- Current location with invalid coordinates
- Current location service failure
- Current location during map destruction
- Current location memory leak

### 11. Marker Clustering Tests
#### Normal Cases
- Clusters form correctly at low zoom
- Clusters break apart at high zoom
- Cluster click callbacks work
- Individual marker click within cluster works
- Cluster animation works correctly
- Cluster styling works correctly

#### Edge Cases
- Single marker (no clustering)
- All markers in same location (maximum clustering)
- Markers arranged in grid pattern
- Markers arranged in circle pattern
- Rapid marker additions/removals
- Cluster at map boundaries

#### Exceptional Cases
- Cluster with null markers list
- Cluster with empty markers list
- Cluster during rapid map movement
- Cluster memory leak
- Cluster with invalid marker positions

### 12. Map Effect Tests
#### Normal Cases
- MapEffect executes JavaScript correctly
- MapEffect can change map style
- MapEffect can add custom controls
- Multiple MapEffects work correctly
- MapEffect cleanup works correctly

#### Edge Cases
- MapEffect with empty JavaScript
- MapEffect with invalid JavaScript
- Rapid MapEffect execution
- MapEffect during map initialization
- MapEffect during map destruction

#### Exceptional Cases
- MapEffect causing JavaScript errors
- MapEffect with infinite loops
- MapEffect memory leak
- MapEffect blocking main thread

### 13. Performance Tests
#### Normal Cases
- Map renders smoothly with 100 markers
- Map renders smoothly with 50 polylines
- Map renders smoothly with 30 polygons
- Map renders smoothly with 20 circles
- Map maintains 60fps during interactions
- Memory usage stays within reasonable bounds

#### Edge Cases
- Map with 1000 markers (stress test)
- Map with complex polygons (many vertices)
- Map with very large polylines (geodesic-like)
- Rapid addition/removal of map elements
- Map interactions during heavy load

#### Exceptional Cases
- Map that causes OOM
- Map that causes UI jank (>16ms frame times)
- Map that leaks memory over time
- Map that causes ANR (Application Not Responding)

### 14. Accessibility Tests
#### Normal Cases
- Map content description works
- Marker content description works
- UI elements have proper accessibility labels
- Touch targets meet minimum size requirements
- Color contrast meets accessibility standards

#### Edge Cases
- Map with dynamic content description changes
- Accessibility services interaction (TalkBack)
- Font scaling effects on map UI
- High contrast mode compatibility

#### Exceptional Cases
- Accessibility service crashes
- Content description causing overflow
- Accessibility focus trapping

## Test Automation Plan

To implement automated testing for LeafleKT, we'll create:

1. **Unit Tests** (JUnit/Test-Kotlin)
   - Pure logic testing (CameraPositionState, MarkerState, etc.)
   - Stateless utility functions
   - Data transformation functions

2. **Instrumented Tests** (AndroidJUnitRunner)
   - Compose testing with createComposeRule()
   - Map rendering verification
   - User interaction simulation
   - State change verification

3. **Screenshot Tests**
   - Visual regression testing for map rendering
   - Component appearance verification
   - Theme switching verification

4. **Performance Tests**
   - Frame timing tests
   - Memory usage tests
   - Stress testing with large datasets

## Test File Structure Proposed

```
leaflekt/
└── src/
    ├── test/
    │   └── java/                 # Unit tests (JVM)
    │       └── com/binayshaw7777/leaflekt/library/
    │           ├── CameraPositionStateTest.kt
    │           ├── MarkerStateTest.kt
    │           ├── PolylineStateTest.kt
    │           ├── PolygonStateTest.kt
    │           ├── CircleStateTest.kt
    │           ├── LeaflektJsBridgeTest.kt
    │           └── MapControllerTest.kt
    │
    └── androidTest/
        └── java/                 # Instrumented tests (Android)
            └── com/binayshaw7777/leaflektsampleapp/
                ├── MapBasicTest.kt
                ├── MarkerTest.kt
                ├── PolylineTest.kt
                ├── PolygonTest.kt
                ├── CircleTest.kt
                ├── InfoWindowTest.kt
                ├── CameraTest.kt
                ├── ThemeSyncTest.kt
                ├── CurrentLocationTest.kt
                ├── ClusteringTest.kt
                ├── PerformanceTest.kt
                └── AccessibilityTest.kt
```

## Immediate Next Steps

1. Fix compilation errors in sample app (type inference issues)
2. Create baseline unit tests for state classes
3. Create instrumented tests for core map functionality
4. Set up screenshot testing infrastructure
5. Create performance test benchmarks

