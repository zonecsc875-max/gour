package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorrectionScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI

    var placeName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var correctionType by remember { mutableStateOf("সময়সূচী বা পুজো সংক্রান্ত (Timings/Ritual)") }
    var message by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "তথ্য সংশোধন ও নতুন তথ্য জমা" else "Submit a Correction",
                        fontWeight = FontWeight.Bold,
                        color = Maroon900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Maroon900)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("correction_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    color = Maroon100,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = Maroon700)
                        Text(
                            text = if (isBn)
                                "শান্তিপুরের উৎসবের তথ্য নিখুঁত রাখতে আপনার অবদান অত্যন্ত মূল্যবান। মন্দির কমিটি বা ভক্তদের জমা দেওয়া প্রতিটি তথ্য গবেষক দল দ্বারা যাচাই করার পর অ্যাপে প্রকাশ করা হয়।"
                            else
                                "Every submission is thoroughly verified by our editorial researchers before being published on the official guide.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Maroon900
                        )
                    }
                }
            }

            if (isSubmitted) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(56.dp))
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (isBn) "আপনার তথ্য সফলভাবে জমা হয়েছে!" else "Submitted Successfully!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBn) "প্রশাসক প্যানেলে পর্যালোচনার পর এটি দ্রুত হালনাগাদ করা হবে।" else "It will be reviewed in the admin console shortly.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { isSubmitted = false }) {
                                Text(if (isBn) "আরেকটি তথ্য পাঠান" else "Submit Another")
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = placeName,
                                onValueChange = { placeName = it },
                                label = { Text(if (isBn) "পুজো কমিটি বা মন্দিরের নাম *" else "Puja Committee or Temple Name *") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = contact,
                                onValueChange = { contact = it },
                                label = { Text(if (isBn) "যোগাযোগের ফোন নম্বর বা ইমেল *" else "Contact Phone or Email *") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = if (isBn) "সংশোধনের ধরন:" else "Correction Category:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon800
                            )

                            val types = if (isBn) listOf(
                                "সময়সূচী বা পুজো সংক্রান্ত (Timings/Ritual)",
                                "ঠিকানা বা ম্যাপ পিন সংশোধন (Address/Map Pin)",
                                "নতুন পুজো বা প্যান্ডেল অন্তর্ভুক্তি (New Listing)",
                                "পার্কিং ও যাতায়াত তথ্য (Transit/Parking)"
                            ) else listOf(
                                "Timings or Ritual Updates",
                                "Address or Map Location Correction",
                                "New Pandal or Temple Addition",
                                "Parking or Transit Guidance"
                            )

                            types.forEach { t ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = correctionType == t,
                                        onClick = { correctionType = t },
                                        colors = RadioButtonDefaults.colors(selectedColor = Maroon700)
                                    )
                                    Text(
                                        text = t,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = message,
                                onValueChange = { message = it },
                                label = { Text(if (isBn) "সঠিক তথ্য ও বিস্তারিত বিবরণ *" else "Detailed Correction Details *") },
                                minLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (placeName.isNotBlank() && message.isNotBlank()) {
                                        viewModel.submitUserCorrection(placeName, contact, correctionType, message)
                                        isSubmitted = true
                                        placeName = ""
                                        contact = ""
                                        message = ""
                                    }
                                },
                                enabled = placeName.isNotBlank() && message.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = Maroon700),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isBn) "জমা দিন (Submit for Review)" else "Submit for Review")
                            }
                        }
                    }
                }
            }
        }
    }
}
