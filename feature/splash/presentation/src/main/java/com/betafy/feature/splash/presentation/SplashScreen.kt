package com.betafy.feature.splash.presentation

import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.graphics.PathMeasure as AndroidPathMeasure
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.betafy.betafyapp.core.uikit.components.BetafyLogo
import com.betafy.betafyapp.core.uikit.theme.brandColors
import kotlin.math.roundToInt

// ===== НАСТРОЙКИ АНИМАЦИИ (всё время в секундах) =====
// TotalTime - общая длительность, должна быть меньше SPLASH_DURATION в AppNavigation (там 5000 мс)
// StickersStart - когда начинает рисоваться первая плашка, StickersStep - пауза между плашками
// StickerDrawTime - сколько рисуется контур плашки и текст, StickerFillDelay / StickerFillTime - когда и как долго проявляется цвет
private const val TotalTime = 4.2f
private const val StickersStart = 0.9f
private const val StickersStep = 0.3f
private const val StickerDrawTime = 0.9f
private const val StickerFillDelay = 0.65f
private const val StickerFillTime = 0.4f

// ===== НАСТРОЙКИ РАЗМЕРОВ =====
// CardWidthFraction - ширина карточки от ширины экрана, CardAspect - высота карточки относительно ширины
// WordmarkSizeFraction - размер слова BETAFY внизу от ширины экрана
private const val CardWidthFraction = 0.56f
private const val CardAspect = 0.78f
private const val WordmarkSizeFraction = 0.1f

// ===== ЦВЕТА ПЛАШЕК: одинаковые в светлой и тёмной теме =====
// Фон - пастельный, текст и обводка - тёмные. Чтобы изменить цвет плашки, правь значения здесь
private val StickerInfoBackground = Color(0xFFC6E7FF)
private val StickerInfoContent = Color(0xFF004C6C)
private val StickerBlushContent = Color(0xFF4B1549)
private val StickerButterContent = Color(0xFF524700)
private val StickerMintContent = Color(0xFF1A5212)
private val StickerLavenderContent = Color(0xFF4E3886)
private val StickerPeachContent = Color(0xFF8B370C)

private fun phase(
    time: Float,
    start: Float,
    duration: Float,
    easing: Easing = FastOutSlowInEasing
): Float = easing.transform(((time - start) / duration).coerceIn(0f, 1f))

// ===== ЭКРАН =====
@Composable
fun SplashScreen() {
    val time = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        time.animateTo(
            targetValue = TotalTime,
            animationSpec = tween((TotalTime * 1000).toInt(), easing = LinearEasing)
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SplashHero(
            cardWidth = maxWidth * CardWidthFraction,
            time = { time.value },
            modifier = Modifier.align(Alignment.Center)
        )

        DrawnWordmark(
            text = "BETAFY",
            fontSize = maxWidth * WordmarkSizeFraction,
            color = MaterialTheme.colorScheme.primary,
            start = 0.8f,
            time = { time.value },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 48.dp)
        )
    }
}

// ===== КАРТОЧКА С ЛОГО И ПЛАШКАМИ =====
// x и y - центр плашки в долях карточки (0 - левый/верхний край, 1 - правый/нижний), rotation - наклон в градусах
// Чтобы сдвинуть плашку, меняй x и y; порядок в списке - порядок появления
private class StickerSpec(
    val text: String,
    val x: Float,
    val y: Float,
    val rotation: Float,
    val background: Color,
    val content: Color
)

@Composable
private fun SplashHero(
    cardWidth: Dp,
    time: () -> Float,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val brand = MaterialTheme.brandColors
    val cardHeight = cardWidth * CardAspect

    val stickers = listOf(
        StickerSpec("сюжет", 0.04f, -0.01f, -15f, brand.blush, StickerBlushContent),
        StickerSpec("стиль", 0.53f, -0.02f, 3f, StickerInfoBackground, StickerInfoContent),
        StickerSpec("герои", 0.98f, -0.01f, 10f, brand.butter, StickerButterContent),
        StickerSpec("бета-ридер", 1.09f, 0.58f, -17f, brand.mint, StickerMintContent),
        StickerSpec("диалоги", 0.92f, 1.07f, 10f, brand.blush, StickerBlushContent),
        StickerSpec("истории", 0.10f, 1.07f, -12f, lerp(brand.tangerine, brand.cream, 0.58f), StickerPeachContent),
        StickerSpec("черновик", -0.09f, 0.48f, 17f, brand.lavender, StickerLavenderContent)
    )

    Box(
        modifier = modifier
            .size(cardWidth, cardHeight)
            .graphicsLayer {
                val s = 0.92f + 0.08f * phase(time(), 0f, 1.2f)
                scaleX = s
                scaleY = s
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = phase(time(), 0f, 0.7f) }
                .clip(RoundedCornerShape(cardWidth * 0.08f))
                .background(colors.primary),
            contentAlignment = Alignment.Center
        ) {
            BetafyLogo(
                modifier = Modifier
                    .height(cardHeight * 0.41f)
                    .graphicsLayer {
                        val p = phase(time(), 0.4f, 0.8f)
                        alpha = p
                        scaleX = 0.9f + 0.1f * p
                        scaleY = 0.9f + 0.1f * p
                    },
                color = colors.background,
                showWordmark = false
            )
        }

        stickers.forEachIndexed { index, sticker ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset {
                        IntOffset(
                            x = (cardWidth.toPx() * sticker.x).roundToInt(),
                            y = (cardHeight.toPx() * sticker.y).roundToInt()
                        )
                    }
                    .size(0.dp),
                contentAlignment = Alignment.Center
            ) {
                DrawnSticker(
                    text = sticker.text,
                    background = sticker.background,
                    content = sticker.content,
                    stickerHeight = cardWidth * 0.17f,
                    start = StickersStart + index * StickersStep,
                    time = time,
                    modifier = Modifier
                        .wrapContentSize(unbounded = true)
                        .graphicsLayer { rotationZ = sticker.rotation }
                )
            }
        }
    }
}

// ===== ПЛАШКА: контур, прорисовка текста, потом заливка =====
// stickerHeight - высота плашки, размер шрифта 0.38 от высоты (меняй число в fontSizePx)
@Composable
private fun DrawnSticker(
    text: String,
    background: Color,
    content: Color,
    stickerHeight: Dp,
    start: Float,
    time: () -> Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val heightPx = with(density) { stickerHeight.toPx() }
    val fontSizePx = heightPx * 0.38f
    val outline = rememberTextOutline(
        text = text,
        fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
        fontWeight = FontWeight.Bold,
        sizePx = fontSizePx
    )

    val padding = heightPx * 0.42f
    val widthPx = outline.width + padding * 2f
    val borderPx = heightPx * 0.035f

    val pill = remember(widthPx, heightPx) {
        Path().apply {
            addRoundRect(RoundRect(0f, 0f, widthPx, heightPx, CornerRadius(heightPx / 2f)))
        }
    }
    val pillMeasure = remember(pill) { PathMeasure().apply { setPath(pill, false) } }
    val pillSegment = remember { Path() }
    val textSegment = remember { AndroidPath() }

    Canvas(modifier.size(with(density) { widthPx.toDp() }, stickerHeight)) {
        val draw = phase(time(), start, StickerDrawTime, LinearOutSlowInEasing)
        val fill = phase(time(), start + StickerFillDelay, StickerFillTime)
        val borderStyle = Stroke(borderPx, cap = StrokeCap.Round, join = StrokeJoin.Round)

        if (fill > 0f) drawPath(pill, background.copy(alpha = fill))

        if (draw >= 1f) {
            drawPath(pill, content, style = borderStyle)
        } else if (draw > 0f) {
            pillSegment.rewind()
            pillMeasure.getSegment(0f, pillMeasure.length * draw, pillSegment, true)
            drawPath(pillSegment, content, style = borderStyle)
        }

        drawOutlineText(
            outline = outline,
            origin = Offset(padding, size.height / 2f - (outline.ascent + outline.descent) / 2f),
            draw = ((draw - 0.2f) / 0.8f).coerceIn(0f, 1f),
            fill = fill,
            color = content,
            strokeWidth = fontSizePx * 0.035f,
            segment = textSegment
        )
    }
}

// ===== СЛОВО BETAFY ВНИЗУ: прорисовка букв, потом заливка =====
@Composable
private fun DrawnWordmark(
    text: String,
    fontSize: Dp,
    color: Color,
    start: Float,
    time: () -> Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val sizePx = with(density) { fontSize.toPx() }
    val outline = rememberTextOutline(
        text = text,
        fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
        fontWeight = FontWeight.Black,
        sizePx = sizePx
    )
    val segment = remember { AndroidPath() }

    Canvas(
        modifier.size(
            with(density) { outline.width.toDp() },
            with(density) { (outline.descent - outline.ascent).toDp() }
        )
    ) {
        drawOutlineText(
            outline = outline,
            origin = Offset(0f, -outline.ascent),
            draw = phase(time(), start, 1.5f, LinearOutSlowInEasing),
            fill = phase(time(), start + 1.2f, 0.6f),
            color = color,
            strokeWidth = sizePx * 0.03f,
            segment = segment
        )
    }
}

// ===== ТЕКСТ КАК КОНТУРЫ: основа эффекта «отрисовки» =====
// Берёт шрифт из темы, превращает текст в контуры букв и считает их длины
private class TextOutline(
    val fill: AndroidPath,
    val contours: List<AndroidPath>,
    val measures: List<AndroidPathMeasure>,
    val lengths: FloatArray,
    val total: Float,
    val width: Float,
    val ascent: Float,
    val descent: Float
)

@Composable
private fun rememberTextOutline(
    text: String,
    fontFamily: FontFamily?,
    fontWeight: FontWeight,
    sizePx: Float
): TextOutline {
    val resolver = LocalFontFamilyResolver.current
    return remember(text, fontFamily, fontWeight, sizePx) {
        val typeface = resolver.resolve(fontFamily = fontFamily, fontWeight = fontWeight).value as? Typeface
            ?: Typeface.DEFAULT
        buildOutline(text, typeface, sizePx)
    }
}

private fun buildOutline(text: String, typeface: Typeface, sizePx: Float): TextOutline {
    val paint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG)
    paint.typeface = typeface
    paint.textSize = sizePx

    val fill = AndroidPath()
    paint.getTextPath(text, 0, text.length, 0f, 0f, fill)

    val contours = mutableListOf<AndroidPath>()
    val measures = mutableListOf<AndroidPathMeasure>()
    val lengths = mutableListOf<Float>()
    val walker = AndroidPathMeasure(fill, false)
    do {
        val length = walker.length
        if (length > 0f) {
            val contour = AndroidPath()
            walker.getSegment(0f, length, contour, true)
            contours += contour
            measures += AndroidPathMeasure(contour, false)
            lengths += length
        }
    } while (walker.nextContour())

    val metrics = paint.fontMetrics
    return TextOutline(
        fill = fill,
        contours = contours,
        measures = measures,
        lengths = lengths.toFloatArray(),
        total = lengths.sum(),
        width = paint.measureText(text),
        ascent = metrics.ascent,
        descent = metrics.descent
    )
}

// draw (0..1) - сколько контуров уже прорисовано, fill (0..1) - насколько проявилась заливка
// Пока идёт заливка, линия контура плавно исчезает, и в конце остаётся чистый текст
private fun DrawScope.drawOutlineText(
    outline: TextOutline,
    origin: Offset,
    draw: Float,
    fill: Float,
    color: Color,
    strokeWidth: Float,
    segment: AndroidPath
) {
    translate(origin.x, origin.y) {
        if (fill > 0f) {
            drawPath(outline.fill.asComposePath(), color.copy(alpha = fill))
        }
        if (draw > 0f && fill < 1f) {
            val style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val strokeColor = color.copy(alpha = 1f - fill)
            var target = outline.total * draw
            for (i in outline.contours.indices) {
                if (target <= 0f) break
                val length = outline.lengths[i]
                if (target >= length) {
                    drawPath(outline.contours[i].asComposePath(), strokeColor, style = style)
                } else {
                    segment.rewind()
                    outline.measures[i].getSegment(0f, target, segment, true)
                    drawPath(segment.asComposePath(), strokeColor, style = style)
                }
                target -= length
            }
        }
    }
}