package com.binayshaw7777.leaflekt.compose

import platform.Foundation.NSUUID

internal actual fun generateId(): String = NSUUID().UUIDString()
