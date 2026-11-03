package com.betafy.betafyapp.core.uikit.theme


import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.betafy.betafyapp.core.uikit.R

@OptIn(ExperimentalTextApi::class)
private fun variableFont(resId: Int, weight: FontWeight, style: FontStyle = FontStyle.Normal): Font = Font(
    resId = resId,
    weight = weight,
    style = style,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

val Onest = FontFamily(
    variableFont(R.font.onest_variable, FontWeight.Normal),
    variableFont(R.font.onest_variable, FontWeight.Medium),
    variableFont(R.font.onest_variable, FontWeight.SemiBold),
    variableFont(R.font.onest_variable, FontWeight.Bold),
    variableFont(R.font.onest_variable, FontWeight.ExtraBold),
)

val Unbounded = FontFamily(
    variableFont(R.font.unbounded_variable, FontWeight.Normal),
    variableFont(R.font.unbounded_variable, FontWeight.Medium),
    variableFont(R.font.unbounded_variable, FontWeight.SemiBold),
    variableFont(R.font.unbounded_variable, FontWeight.Bold),
    variableFont(R.font.unbounded_variable, FontWeight.ExtraBold),
)

val Literata = FontFamily(
    variableFont(R.font.literata_variable, FontWeight.Normal),
    variableFont(R.font.literata_variable, FontWeight.Medium),
    variableFont(R.font.literata_variable, FontWeight.SemiBold),
    variableFont(R.font.literata_variable, FontWeight.Bold),
    variableFont(R.font.literata_italic_variable, FontWeight.Normal, FontStyle.Italic),
    variableFont(R.font.literata_italic_variable, FontWeight.SemiBold, FontStyle.Italic),
)

val BetafyTypography = Typography(
    displayLarge = TextStyle(fontFamily = Unbounded, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp),
    displayMedium = TextStyle(fontFamily = Unbounded, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = (-0.4).sp),
    displaySmall = TextStyle(fontFamily = Unbounded, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = (-0.3).sp),
    headlineLarge = TextStyle(fontFamily = Unbounded, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 34.sp, letterSpacing = (-0.2).sp),
    headlineMedium = TextStyle(fontFamily = Unbounded, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 30.sp, letterSpacing = (-0.1).sp),
    headlineSmall = TextStyle(fontFamily = Unbounded, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp),
    titleLarge = TextStyle(fontFamily = Unbounded, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 26.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontFamily = Onest, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.1.sp),
    titleSmall = TextStyle(fontFamily = Onest, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge = TextStyle(fontFamily = Onest, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 26.sp, letterSpacing = 0.15.sp),
    bodyMedium = TextStyle(fontFamily = Onest, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = 0.15.sp),
    bodySmall = TextStyle(fontFamily = Onest, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 18.sp, letterSpacing = 0.2.sp),
    labelLarge = TextStyle(fontFamily = Onest, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp),
    labelMedium = TextStyle(fontFamily = Onest, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    labelSmall = TextStyle(fontFamily = Onest, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
)

@Immutable
data class BetafyReadingTypography(
    val chapterTitle: TextStyle,
    val heading: TextStyle,
    val bodyLarge: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val quote: TextStyle,
    val footnote: TextStyle,
)

val DefaultReadingTypography = BetafyReadingTypography(
    chapterTitle = TextStyle(fontFamily = Literata, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp),
    heading = TextStyle(fontFamily = Literata, fontWeight = FontWeight.SemiBold, fontSize = 21.sp, lineHeight = 30.sp, letterSpacing = 0.sp),
    bodyLarge = TextStyle(fontFamily = Literata, fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = 34.sp, letterSpacing = 0.1.sp),
    body = TextStyle(fontFamily = Literata, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 30.sp, letterSpacing = 0.1.sp),
    bodySmall = TextStyle(fontFamily = Literata, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 26.sp, letterSpacing = 0.1.sp),
    quote = TextStyle(fontFamily = Literata, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic, fontSize = 18.sp, lineHeight = 30.sp, letterSpacing = 0.1.sp),
    footnote = TextStyle(fontFamily = Literata, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 19.sp, letterSpacing = 0.2.sp),
)

/**
 * Типографика шапки экранов
 */
@Immutable
data class BetafyHeaderTypography(
    val caption: TextStyle,
    val title: TextStyle
)

val BetafyHeaderText = BetafyHeaderTypography(
    caption = TextStyle(
        fontFamily = Onest,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    ),
    title = TextStyle(
        fontFamily = Unbounded,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = -0.1.sp
    )
)