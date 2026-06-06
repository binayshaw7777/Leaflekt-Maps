package com.binayshaw7777.leaflekt

data class LeaflektColor(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float = 1f,
) {
    companion object {
        val Black = LeaflektColor(0f, 0f, 0f)
        val White = LeaflektColor(1f, 1f, 1f)
        val Transparent = LeaflektColor(0f, 0f, 0f, 0f)
        val Red = LeaflektColor(1f, 0f, 0f)
        val Blue = LeaflektColor(0f, 0f, 1f)
        val Green = LeaflektColor(0f, 0.502f, 0f)

        fun fromArgb(argb: Long): LeaflektColor {
            val alpha = ((argb shr 24) and 0xFF) / 255f
            val red = ((argb shr 16) and 0xFF) / 255f
            val green = ((argb shr 8) and 0xFF) / 255f
            val blue = (argb and 0xFF) / 255f
            return LeaflektColor(red, green, blue, alpha)
        }

        fun fromRgb(red: Int, green: Int, blue: Int, alpha: Int = 255): LeaflektColor =
            LeaflektColor(red / 255f, green / 255f, blue / 255f, alpha / 255f)
    }

    internal fun toCssRgba(overrideAlpha: Float = alpha): String {
        val redChannel = (red * 255).toInt().coerceIn(0, 255)
        val greenChannel = (green * 255).toInt().coerceIn(0, 255)
        val blueChannel = (blue * 255).toInt().coerceIn(0, 255)
        return "rgba($redChannel,$greenChannel,$blueChannel,$overrideAlpha)"
    }
}
