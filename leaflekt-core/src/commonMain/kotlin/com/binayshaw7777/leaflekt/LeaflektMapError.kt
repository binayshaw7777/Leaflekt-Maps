package com.binayshaw7777.leaflekt

sealed class LeaflektMapError {
    data class JsError(val message: String) : LeaflektMapError()
    data class TileError(val url: String) : LeaflektMapError()
    data object ProcessCrash : LeaflektMapError()
    data object InitTimeout : LeaflektMapError()
}
