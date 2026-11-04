package com.betafy.presentation.navigation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.betafy.presentation.navigation.model.NavigationBottomModel
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

// ===== РАЗМЕРЫ: здесь меняется внешний вид панели =====
// BarHeight - высота панели, CircleSize - диаметр кружка, RingGap - ширина каёмки между кружком и краем бугорка
// FilletRadius - скругление, которым бугорок переходит в линию панели
// IconSize - иконки невыбранных вкладок, ActiveIconSize - иконка внутри кружка
// StrokeWidth / ActiveStrokeWidth - толщина линий иконок (в единицах сетки 24x24)
private val BarHeight = 64.dp
private val CircleSize = 54.dp
private val RingGap = 7.dp
private val FilletRadius = 18.dp
private val IconSize = 26.dp
private val ActiveIconSize = 28.dp
private const val StrokeWidth = 2.0f
private const val ActiveStrokeWidth = 2.4f

// ===== СКОРОСТЬ АНИМАЦИИ (в миллисекундах) =====
// SlideDuration - как долго бугорок едет к новой вкладке
// DipDuration - как быстро кружок опускается, RiseDuration - как плавно поднимается обратно
// DipLevel - насколько глубоко опускается (1 = вообще не опускается, 0.35 = заметно опускается)
// Чтобы кружок просто плавно ехал без опускания, поставь DipLevel = 1f
private const val SlideDuration = 400
private const val DipDuration = 150
private const val RiseDuration = 350
private const val DipLevel = 0.5f

// Служебные размеры: RingRadius - радиус бугорка, CircleRestDrop - насколько центр кружка ниже линии панели в покое
// CircleDipDistance - на сколько кружок «проседает» при анимации, BumpHeight - высота зоны над панелью
private val RingRadius = CircleSize / 2 + RingGap
private val CircleRestDrop = 2.dp
private val CircleDipDistance = 28.dp
private val BumpHeight = RingRadius - CircleRestDrop + 6.dp

private fun Density.circleDrop(bump: Float): Float =
    CircleRestDrop.toPx() + (1f - bump) * CircleDipDistance.toPx()

// ===== СВЯЗКА С НАВИГАЦИЕЙ: переключение вкладок =====
// Определяет выбранную вкладку по текущему маршруту и переходит на нажатую
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

// ===== САМА ПАНЕЛЬ =====
@Composable
fun BetafyBottomBar(
    items: List<NavigationBottomModel>,
    selectedIndex: Int,
    onSelect: (NavigationBottomModel) -> Unit
) {
    // --- Цвета: фон панели, цвет невыбранных иконок, цвета кружка выбранной вкладки ---
    // Цвета вкладок задаются в AccentColors.kt, роль вкладки - в NavigationBottomModel
    val barColor = MaterialTheme.colorScheme.primary
    val idleIconColor = MaterialTheme.colorScheme.onPrimary
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val selected = items[selectedIndex]
    val selectedColors = selected.accent.colors()

    val circleColor by animateColorAsState(selectedColors.container, tween(350), label = "circle")
    val activeIconColor by animateColorAsState(selectedColors.onContainer, tween(350), label = "icon")

    // --- Анимации: position - положение бугорка, bump - «всплытие» (1 = в покое), strokeProgress - прорисовка иконки ---
    val position = remember { Animatable(selectedIndex.toFloat()) }
    val bump = remember { Animatable(1f) }
    val strokeProgress = remember { Animatable(1f) }

    // Плавное скольжение бугорка к выбранной вкладке, без пружины и покачивания
    LaunchedEffect(selectedIndex) {
        position.animateTo(
            selectedIndex.toFloat(),
            tween(SlideDuration, easing = FastOutSlowInEasing)
        )
    }
    // Кружок мягко опускается и плавно поднимается на место, без подпрыгивания
    LaunchedEffect(selectedIndex) {
        bump.animateTo(DipLevel, tween(DipDuration, easing = FastOutLinearInEasing))
        bump.animateTo(1f, tween(RiseDuration, easing = FastOutSlowInEasing))
    }
    // Прорисовка иконки полосками (650 - длительность в мс, delayMillis - задержка старта)
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

        // --- Слой 1: фон панели с круглым бугорком ---
        // Бугорок - окружность радиуса RingRadius, концентричная кружку, плюс два скругления в линию панели
        Canvas(Modifier.fillMaxSize()) {
            val width = size.width
            val top = BumpHeight.toPx()
            val cx = (position.value + 0.5f) * (width / items.size)
            val radius = RingRadius.toPx()
            val fillet = FilletRadius.toPx()
            val drop = circleDrop(bump.value.coerceIn(0f, 1f))
            val cy = top + drop

            val dx = sqrt((radius + fillet).pow(2) - (fillet + drop).pow(2))
            val a1 = Math.toDegrees(atan2(fillet + drop, dx).toDouble()).toFloat()

            val path = Path().apply {
                moveTo(0f, top)
                lineTo(cx - dx, top)
                arcTo(Rect(Offset(cx - dx, top - fillet), fillet), 90f, a1 - 90f, false)
                arcTo(Rect(Offset(cx, cy), radius), a1 - 180f, 180f - 2f * a1, false)
                arcTo(Rect(Offset(cx + dx, top - fillet), fillet), 180f - a1, a1 - 90f, false)
                lineTo(width, top)
                lineTo(width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, barColor)
        }

        // --- Слой 2: невыбранные иконки и зоны нажатия (выбранная иконка плавно исчезает) ---
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
                        strokeWidth = StrokeWidth,
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

        // --- Слой 3: кружок выбранной вкладки с иконкой, едет вместе с бугорком ---
        // Положение по вертикали считается так же, как у бугорка (circleDrop); размер при появлении - в graphicsLayer (0.7f)
        Box(
            modifier = Modifier
                .offset {
                    val drop = circleDrop(bump.value.coerceIn(0f, 1f))
                    IntOffset(
                        x = ((position.value + 0.5f) * itemWidth.toPx() - CircleSize.toPx() / 2f).roundToInt(),
                        y = (BumpHeight.toPx() + drop - CircleSize.toPx() / 2f).roundToInt()
                    )
                }
                .size(CircleSize)
                .graphicsLayer {
                    val s = 0.7f + 0.3f * bump.value
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
                strokeWidth = ActiveStrokeWidth,
                modifier = Modifier.size(ActiveIconSize)
            )
        }
    }
}

// ===== ИКОНКА ИЗ ПОЛОСОК =====
// Рисует контуры из iconPaths (сетка 24x24); progress от 0 до 1 задаёт, какая часть контура уже прорисована
// strokeWidth - толщина линии; несколько контуров рисуются по очереди со сдвигом
@Composable
private fun StrokeIcon(
    pathData: List<String>,
    progress: () -> Float,
    color: Color,
    strokeWidth: Float,
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
        val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)

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