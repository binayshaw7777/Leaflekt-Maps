
$replacements = @{
    "rememberLeaflektCameraPositionState" = "rememberCameraPositionState"
    "LeaflektCameraPositionState" = "CameraPositionState"
    "LeaflektCameraPosition" = "CameraPosition"
    "LeaflektLatLng" = "LatLng"
    "LeaflektMapUiSettings" = "MapUiSettings"
    "LeaflektMapProperties" = "MapProperties"
    "LeaflektMapStyle" = "MapStyle"
    "LeaflektMap" = "MapView"
    "LeaflektMarkerCluster" = "MarkerCluster"
    "LeaflektMarker" = "Marker"
    "LeaflektCircle" = "Circle"
    "LeaflektPolygon" = "Polygon"
    "LeaflektPolyline" = "Polyline"
    "LeaflektController" = "MapController"
    "LeaflektOverlay" = "MapOverlay"
    # Additional obvious ones to keep API consistent
    "LeaflektMarkerState" = "MarkerState"
    "LeaflektMarkerInfo" = "MarkerInfo"
    "LeaflektMarkerIcon" = "MarkerIcon"
    "LeaflektMarkerIconInfo" = "MarkerIconInfo"
    "LeaflektPolylineState" = "PolylineState"
    "LeaflektPolylineInfo" = "PolylineInfo"
    "LeaflektPolygonState" = "PolygonState"
    "LeaflektPolygonInfo" = "PolygonInfo"
    "LeaflektCircleState" = "CircleState"
    "LeaflektCircleInfo" = "CircleInfo"
    "LocalLeaflektController" = "LocalMapController"
    "LeaflektMapComposable" = "MapComposable"
    "LeaflektMapEffect" = "MapEffect"
}

$files = Get-ChildItem -Path . -Include *.kt, *.md -Recurse

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    $changed = $false
    
    # Sort keys by length descending to avoid partial replacements (e.g. LeaflektMarker before LeaflektMarkerState)
    $sortedKeys = $replacements.Keys | Sort-Object -Descending -Property Length
    
    foreach ($key in $sortedKeys) {
        if ($content -match $key) {
            $content = $content -replace $key, $replacements[$key]
            $changed = $true
        }
    }
    
    if ($changed) {
        Set-Content -Path $file.FullName -Value $content
        Write-Host "Updated $($file.FullName)"
    }
}

# Renaming files
$renames = @{
    "LeaflektController.kt" = "MapController.kt"
    "LeaflektMap.kt" = "MapView.kt"
    "LeaflektMapProperties.kt" = "MapProperties.kt"
    "LeaflektPolyline.kt" = "Polyline.kt"
    "LeaflektPolylineInfo.kt" = "PolylineInfo.kt"
    "LeaflektPolylineState.kt" = "PolylineState.kt"
    "LeaflektPolygon.kt" = "Polygon.kt"
    "LeaflektPolygonInfo.kt" = "PolygonInfo.kt"
    "LeaflektPolygonState.kt" = "PolygonState.kt"
    "LeaflektMarker.kt" = "Marker.kt"
    "LeaflektMarkerInfo.kt" = "MarkerInfo.kt"
    "LeaflektMarkerState.kt" = "MarkerState.kt"
    "LeaflektMarkerIcon.kt" = "MarkerIcon.kt"
    "LeaflektCircle.kt" = "Circle.kt"
    "LeaflektCircleInfo.kt" = "CircleInfo.kt"
    "LeaflektCircleState.kt" = "CircleState.kt"
    "LeaflektOverlay.kt" = "MapOverlay.kt"
    "LeaflektMarkerCluster.kt" = "MarkerCluster.kt"
    "LeaflektMapStyle.kt" = "MapStyle.kt"
    "LeaflektMapUiSettings.kt" = "MapUiSettings.kt"
    "LeaflektMapComposable.kt" = "MapComposable.kt"
    "LeaflektMapEffect.kt" = "MapEffect.kt"
}

foreach ($oldName in $renames.Keys) {
    $fileToRename = Get-ChildItem -Path . -Filter $oldName -Recurse
    foreach ($f in $fileToRename) {
        $newName = Join-Path $f.DirectoryName $renames[$oldName]
        Move-Item -Path $f.FullName -Destination $newName -Force
        Write-Host "Renamed $($f.FullName) to $newName"
    }
}
