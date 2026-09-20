package com.hannyberry.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hannyberry.ui.theme.Berry
import com.hannyberry.ui.theme.BerryDark
import com.hannyberry.ui.theme.HannyBerryTheme
import com.hannyberry.ui.theme.Ink
import com.hannyberry.ui.theme.Leaf
import com.hannyberry.ui.theme.SoftGreen
import com.hannyberry.ui.theme.Typography

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    isPositive: Boolean = true,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = Typography.bodyMedium,
                color = Ink.copy(alpha = 0.7f)
            )
            
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value,
                    style = Typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) BerryDark else Color(0xFFB3261E)
                )
                
                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        text = subtitle,
                        style = Typography.labelSmall,
                        color = if (isPositive) Leaf else Color(0xFFB3261E),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (subtitle != null && !subtitle.isEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fullWidth: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Berry,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            style = Typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Ink.copy(alpha = 0.7f)) },
        placeholder = { Text(placeholder, color = Ink.copy(alpha = 0.5f)) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Berry,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            backgroundColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = singleLine
    )
}
