package com.bekircaglar.qarko.presentation.tenant.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lightGray
import com.bekircaglar.qarko.lighterGray
import com.bekircaglar.qarko.presentation.common.components.QText
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.search


@Composable
fun SearchTextField(
    text: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = text,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                painter = painterResource(Res.drawable.search),
                contentDescription = "Search",
                tint = gray,
                modifier = Modifier
                    .size(20.dp)
            )
        },

        placeholder = {
            QText(
                text = placeholder,
                color = gray,
                fontSize = 14.sp,
            )

        },
        maxLines = 1,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth(),
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = lighterGray,
            focusedContainerColor = lightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent

        )
    )
}