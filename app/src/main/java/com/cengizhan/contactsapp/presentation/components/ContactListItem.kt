package com.cengizhan.contactsapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cengizhan.contactsapp.domain.model.Contact

@Composable
fun ContactListItem(
    contact: Contact,
    isFromDevice: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar – fotoğraf varsa onu göster, yoksa baş harfler
            if (!contact.profileImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = contact.profileImageUrl,
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                val first = contact.firstName.firstOrNull()?.uppercaseChar()?.toString() ?: ""
                val last = contact.lastName.firstOrNull()?.uppercaseChar()?.toString() ?: ""
                val initials = (first + last).ifBlank { "#" }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${contact.firstName} ${contact.lastName}".trim(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1
                )

                if (contact.phoneNumber.isNotBlank()) {
                    Text(
                        text = contact.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // Cihaz rehberinde ise küçük telefon ikonu
            if (isFromDevice) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = "Saved in device contacts",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(18.dp)
                )
            }

            // Delete butonu
            TextButton(onClick = onDeleteClick) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
