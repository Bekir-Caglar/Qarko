package com.bekircaglar.qarko.presentation.common.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.bekircaglar.qarko.util.QarkoFontFamily
import org.jetbrains.compose.resources.Font
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.SFPRODISPLAYMEDIUM
import qarko.composeapp.generated.resources.SFPRODISPLAYREGULAR

@Composable
fun QText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = QarkoFontFamily,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    textStyle: TextStyle? = null,
    textDecoration : TextDecoration? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        maxLines = maxLines,
        lineHeight = lineHeight,
        overflow = overflow,
        textDecoration = textDecoration,
        style = textStyle ?: TextStyle(
            fontFamily = fontFamily ?: QarkoFontFamily,
            fontWeight = fontWeight ?: FontWeight.Normal,
            fontSize = fontSize,
            color = color,
            lineHeight = lineHeight
        )
    )
}