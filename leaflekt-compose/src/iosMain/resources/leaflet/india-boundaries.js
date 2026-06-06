(function () {
    const INDIA_BOUNDARY_GEOJSON_PATH = "/assets/leaflet/india-country-outline.min.geojson";

    let boundaryDataPromise = null;
    let boundaryLayer = null;
    let boundMap = null;
    let activeStyleId = "open_street_map";

    function loadBoundaryData() {
        if (boundaryDataPromise !== null) {
            return boundaryDataPromise;
        }

        boundaryDataPromise = fetch(INDIA_BOUNDARY_GEOJSON_PATH)
            .then(function (response) {
                if (!response.ok) {
                    throw new Error("Failed to load India boundary GeoJSON");
                }

                return response.json();
            });

        return boundaryDataPromise;
    }

    function boundaryPalette(styleId) {
        switch (styleId) {
            case "carto_dark":
                return {
                    borderColor: "#333333",
                    claimedOpacity: 0.8,
                };
            case "esri_world_imagery":
                return {
                    borderColor: "#ffffff",
                    claimedOpacity: 0.6,
                };
            case "open_topo_map":
                return {
                    borderColor: "#9e9e9e",
                    claimedOpacity: 0.7,
                };
            case "carto_light":
                return {
                    borderColor: "#dbdbdb",
                    claimedOpacity: 1,
                };
            case "open_street_map":
            default:
                return {
                    borderColor: "#A07B9E",
                    claimedOpacity: 0.6,
                };
        }
    }

    function boundaryWeight(map) {
        const zoom = map.getZoom();
        if (zoom < 6) return 0.8;
        if (zoom < 10) return 1.2;
        return 1.8;
    }

    function pathStyle(map, feature) {
        const palette = boundaryPalette(activeStyleId);
        const zoom = map.getZoom();
        
        let opacity = palette.claimedOpacity;
        if (zoom > 10) opacity *= 0.7; 
        if (zoom > 14) opacity *= 0.5;

        return {
            color: palette.borderColor,
            weight: boundaryWeight(map),
            opacity: opacity,
            fill: false,
            interactive: false,
            bubblingMouseEvents: false,
            lineCap: "round",
            lineJoin: "round"
        };
    }

    function createLayer(map, featureCollection) {
        return L.geoJSON(featureCollection, {
            style: function (feature) {
                return pathStyle(map, feature);
            },
            interactive: false,
            bubblingMouseEvents: false
        });
    }

    function addLayerToMap(map) {
        return loadBoundaryData()
            .then(function (featureCollection) {
                if (boundaryLayer === null) {
                    boundaryLayer = createLayer(map, featureCollection);
                }

                boundaryLayer.setStyle(function (feature) {
                    return pathStyle(map, feature);
                });

                if (!map.hasLayer(boundaryLayer)) {
                    boundaryLayer.addTo(map);
                }
            })
            .catch(function (error) {
                console.warn("[LeafleKT] India boundary overlay failed", error);
            });
    }

    function removeLayerFromMap(map) {
        if (boundaryLayer !== null && map.hasLayer(boundaryLayer)) {
            map.removeLayer(boundaryLayer);
        }
    }

    function bindMap(map) {
        if (boundMap === map) {
            return;
        }

        boundMap = map;
        map.on("zoomend", function () {
            if (boundaryLayer !== null && map.hasLayer(boundaryLayer)) {
                boundaryLayer.setStyle(function (feature) {
                    return pathStyle(map, feature);
                });
            }
        });
    }

    window.LeaflektIndiaBoundaryOverlay = {
        setVisible: function (map, isVisible) {
            if (!map) {
                return;
            }

            bindMap(map);

            if (isVisible) {
                addLayerToMap(map);
                return;
            }

            removeLayerFromMap(map);
        },
        setMapStyle: function (map, styleId) {
            if (!map) {
                return;
            }

            activeStyleId = styleId || "open_street_map";
            bindMap(map);

            if (boundaryLayer !== null && map.hasLayer(boundaryLayer)) {
                boundaryLayer.setStyle(function (feature) {
                    return pathStyle(map, feature);
                });
            }
        }
    };
})();
