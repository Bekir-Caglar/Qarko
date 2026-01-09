import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lightGray
import com.bekircaglar.qarko.presentation.common.theme.lighterGray
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.util.QarkoTypography
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_search
import qarko.composeapp.generated.resources.ic_x_small
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.bekircaglar.qarko.presentation.common.theme.black
import androidx.compose.ui.text.input.ImeAction

@Composable
fun SearchTextField(
    text: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onSearch: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction: Interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> isFocused = true
                is FocusInteraction.Unfocus -> isFocused = false
            }
        }
    }
    TextField(
        value = text,
        onValueChange = onValueChange,
        textStyle = QarkoTypography.bodyLarge,
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch?.invoke()
                focusManager.clearFocus()
            }
        ),
        leadingIcon = {
            if (isFocused) {
                BackButton(
                    iconColor = gray
                ) {
                    focusManager.clearFocus()
                }
            } else {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = "Search",
                    tint = gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        trailingIcon = {
            if (text.isNotEmpty() && isFocused) {
                Icon(
                    painter = painterResource(Res.drawable.ic_x_small),
                    contentDescription = "Clear",
                    tint = black,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onValueChange("") }
                )
            }
        },
        placeholder = {
            QText(
                text = placeholder,
                color = gray,
                textStyle = QarkoTypography.bodyLarge
            )
        },
        maxLines = 1,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = lighterGray,
            focusedContainerColor = lightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        interactionSource = interactionSource
    )
}