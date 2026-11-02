package com.betafy.presentation.navigation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.betafy.presentation.navigation.model.NavigationBottomModel
import kotlin.math.abs
import kotlin.math.roundToInt

private val BarHeight = 64.dp
private val BumpHeight = 30.dp
private val BumpHalfWidth = 46.dp
private val CircleSize = 54.dp
private val IconSize = 26.dp

@Composable
fun AppBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val items = NavigationBottomModel.getNavItems()
    val selectedIndex = items.indexOfFirst { item ->
        destination?.hierarchy?.any { it.route == item.route } == true
    }.coerceAtLeast(0)

    BetafyBottomBar(
        items = items,
        selectedIndex = selectedIndex,
        onSelect = { item ->
            navController.navigate(item.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@Composable
fun BetafyBottomBar(
    items: List<NavigationBottomModel>,
    selectedIndex: Int,
    onSelect: (NavigationBottomModel) -> Unit
) {
    val barColor = MaterialTheme.colorScheme.primary
    val idleIconColor = MaterialTheme.colorScheme.onPrimary
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val selected = items[selectedIndex]
    val selectedColors = selected.accent.colors()

    val circleColor by animateColorAsState(selectedColors.container, tween(350), label = "circle")
    val activeIconColor by animateColorAsState(selectedColors.onContainer, tween(350), label = "icon")

    val position = remember { Animatable(selectedIndex.toFloat()) }
    val bump = remember { Animatable(1f) }
    val strokeProgress = remember { Animatable(1f) }

    LaunchedEffect(selectedIndex) {
        position.animateTo(
            selectedIndex.toFloat(),
            spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow)
        )
    }
    LaunchedEffect(selectedIndex) {
        bump.animateTo(0.35f, tween(140))
        bump.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow))
    }
    LaunchedEffect(selectedIndex) {
        strokeProgress.snapTo(0f)
        strokeProgress.animateTo(1f, tween(650, delayMillis = 120, easing = FastOutSlowInEasing))
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(BumpHeight + BarHeight + bottomInset)
    ) {
        val itemWidth = maxWidth / items.size

        Canvas(Modifier.fillMaxSize()) {
            val width = size.width
            val top = BumpHeight.toPx()
            val centerX = (position.value + 0.5f) * (width / items.size)
            val rise = top * bump.value.coerceAtLeast(0f)
            val half = BumpHalfWidth.toPx()
            val path = Path().apply {
                moveTo(0f, top)
                lineTo(centerX - half, top)
                cubicTo(
                    centerX - half * 0.45f, top,
                    centerX - half * 0.55f, top - rise,
                    centerX, top - rise
                )
                cubicTo(
                    centerX + half * 0.55f, top - rise,
                    centerX + half * 0.45f, top,
                    centerX + half, top
                )
                lineTo(width, top)
                lineTo(width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, barColor)
        }

        Row(
            modifier = Modifier
                .padding(top = BumpHeight)
                .fillMaxWidth()
                .height(BarHeight)
        ) {
            items.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onSelect(item) },
                    contentAlignment = Alignment.Center
                ) {
                    StrokeIcon(
                        pathData = item.iconPaths,
                        progress = { 1f },
                        color = idleIconColor,
                        modifier = Modifier
                            .size(IconSize)
                            .graphicsLayer {
                                val closeness = (1f - abs(position.value - index)).coerceIn(0f, 1f)
                                alpha = 1f - closeness
                            }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = ((position.value + 0.5f) * itemWidth.toPx() - CircleSize.toPx() / 2f).roundToInt(),
                        y = (BumpHeight + 2.dp - CircleSize / 2).roundToPx()
                    )
                }
                .size(CircleSize)
                .graphicsLayer {
                    val s = 0.6f + 0.4f * bump.value
                    scaleX = s
                    scaleY = s
                }
                .background(circleColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            StrokeIcon(
                pathData = selected.iconPaths,
                progress = { strokeProgress.value },
                color = activeIconColor,
                modifier = Modifier.size(IconSize)
            )
        }
    }
}

@Composable
private fun StrokeIcon(
    pathData: List<String>,
    progress: () -> Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val paths = remember(pathData) {
        pathData.map { PathParser().parsePathString(it).toPath() }
    }
    val measure = remember { PathMeasure() }

    Canvas(modifier) {
        val unit = size.width / 24f
        val total = progress().coerceIn(0f, 1f)
        val count = paths.size
        val span = 1f + 0.5f * (count - 1)
        val style = Stroke(width = 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        scale(unit, pivot = Offset.Zero) {
            paths.forEachIndexed { index, path ->
                val local = (total * span - index * 0.5f).coerceIn(0f, 1f)
                when {
                    local >= 1f -> drawPath(path, color, style = style)
                    local > 0f -> {
                        measure.setPath(path, false)
                        val segment = Path()
                        measure.getSegment(0f, measure.length * local, segment, true)
                        drawPath(segment, color, style = style)
                    }
                }
            }
        }
    }
}