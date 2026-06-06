package com.binayshaw7777.leaflekt.compose

data class LeaflektCurrentLocationIcon(
    val pngBytes: ByteArray,
    val widthPx: Int,
    val heightPx: Int,
    val anchorFractionX: Float = 0.5f,
    val anchorFractionY: Float = 0.5f
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as LeaflektCurrentLocationIcon
        return pngBytes.contentEquals(other.pngBytes) &&
            widthPx == other.widthPx &&
            heightPx == other.heightPx &&
            anchorFractionX == other.anchorFractionX &&
            anchorFractionY == other.anchorFractionY
    }

    override fun hashCode(): Int {
        var result = pngBytes.contentHashCode()
        result = 31 * result + widthPx
        result = 31 * result + heightPx
        result = 31 * result + anchorFractionX.hashCode()
        result = 31 * result + anchorFractionY.hashCode()
        return result
    }
}
