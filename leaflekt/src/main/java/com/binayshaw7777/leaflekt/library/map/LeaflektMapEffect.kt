package com.binayshaw7777.leaflekt.library.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.binayshaw7777.leaflekt.library.controller.LeaflektController
import kotlinx.coroutines.CoroutineScope

/**
 * A side-effect backed by [LaunchedEffect] that provides the managed [LeaflektController].
 *
 * Use this when you need imperative access to the underlying map runtime for features that are not
 * yet wrapped by the declarative SDK surface.
 */
@Composable
@LeaflektMapComposable
fun MapEffect(
    key1: Any?,
    block: suspend CoroutineScope.(LeaflektController) -> Unit
) {
    val controller = LocalLeaflektController.current ?: return
    LaunchedEffect(controller, key1) {
        block(controller)
    }
}

/**
 * A side-effect backed by [LaunchedEffect] that provides the managed [LeaflektController].
 *
 * Use this when you need imperative access to the underlying map runtime for features that are not
 * yet wrapped by the declarative SDK surface.
 */
@Composable
@LeaflektMapComposable
fun MapEffect(
    key1: Any?,
    key2: Any?,
    block: suspend CoroutineScope.(LeaflektController) -> Unit
) {
    val controller = LocalLeaflektController.current ?: return
    LaunchedEffect(controller, key1, key2) {
        block(controller)
    }
}

/**
 * A side-effect backed by [LaunchedEffect] that provides the managed [LeaflektController].
 *
 * Use this when you need imperative access to the underlying map runtime for features that are not
 * yet wrapped by the declarative SDK surface.
 */
@Composable
@LeaflektMapComposable
fun MapEffect(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    block: suspend CoroutineScope.(LeaflektController) -> Unit
) {
    val controller = LocalLeaflektController.current ?: return
    LaunchedEffect(controller, key1, key2, key3) {
        block(controller)
    }
}

/**
 * A side-effect backed by [LaunchedEffect] that provides the managed [LeaflektController].
 *
 * Use this when you need imperative access to the underlying map runtime for features that are not
 * yet wrapped by the declarative SDK surface.
 */
@Composable
@LeaflektMapComposable
fun MapEffect(
    vararg keys: Any?,
    block: suspend CoroutineScope.(LeaflektController) -> Unit
) {
    val controller = LocalLeaflektController.current ?: return
    LaunchedEffect(controller, *keys) {
        block(controller)
    }
}
