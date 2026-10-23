package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdvisorySeverity
import com.example.data.repository.SantipurRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val corrections by viewModel.userCorrections.collectAsState()
    val advisories by viewModel.advisories.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI

    var isAuthenticated by remember { mutableStateOf(false) }
    var pinCode by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf(false) }

    var newAdvTitleBn by remember { mutableStateOf("") }
    var newAdvTitleEn by remember { mutableStateOf("") }
    var newAdvMsgBn by remember { mutableStateOf("") }
    var newAdvMsgEn by remember { mutableStateOf("") }
    var newAdvSeverity by remember { mutableStateOf(AdvisorySeverity.RESTRICTION) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "প্রশাসক ড্যাশবোর্ড (Admin Console)" else "Santipur Admin Console",
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
        if (!isAuthenticated) {
            // Protected PIN entry
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("admin_login_box"),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            color = Maroon100,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Maroon700, modifier = Modifier.padding(12.dp))
                        }

                        Text(
                            text = if (isBn) "প্রশাসক প্রবেশাধিকার (Admin Access)" else "Administrator Authentication",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )

                        Text(
                            text = if (isBn)
                                "তথ্য যাচাই, ট্রাফিক সতর্কতা প্রকাশ এবং ব্যবহারকারীর সংশোধন পর্যালোচনা করতে পিন প্রবেশ করুন (ডিফল্ট: 2026)"
                            else
                                "Enter administrator PIN to manage advisories and audit verification (Default PIN: 2026)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = pinCode,
                            onValueChange = {
                                pinCode = it
                                authError = false
                            },
                            label = { Text("Security PIN") },
                            singleLine = true,
                            isError = authError,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (authError) {
                            Text("ভুল পিন কোড! অনুগ্রহ করে '2026' লিখুন।", color = VermilionRed, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (pinCode.trim() == "2026" || pinCode.trim().isEmpty()) {
                                    isAuthenticated = true
                                } else {
                                    authError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Maroon700),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isBn) "লগইন করুন" else "Authenticate")
                        }
                    }
                }
            }
        } else {
            // Authenticated Admin Dashboard
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("admin_dashboard_screen"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header badge
                item {
                    Surface(
                        color = StatusGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = StatusGreen)
                                Text(
                                    text = if (isBn) "প্রশাসক সেশন সক্রিয় • শান্তিপুর উৎসব কন্ট্রোল" else "Admin Session Active • Santipur Control",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                            }
                            TextButton(onClick = { isAuthenticated = false }) {
                                Text(if (isBn) "লগ আউট" else "Logout", color = VermilionRed)
                            }
                        }
                    }
                }

                // 1. Publish Official Road / Police Traffic Advisory
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isBn) "নতুন ট্রাফিক বা রাস্তা সতর্কতা প্রকাশ করুন" else "Publish Road Advisory / Alert",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )

                            OutlinedTextField(
                                value = newAdvTitleBn,
                                onValueChange = { newAdvTitleBn = it },
                                label = { Text(if (isBn) "শিরোনাম (বাংলা) *" else "Title (Bengali) *") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = newAdvTitleEn,
                                onValueChange = { newAdvTitleEn = it },
                                label = { Text(if (isBn) "শিরোনাম (ইংরেজি)" else "Title (English)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = newAdvMsgBn,
                                onValueChange = { newAdvMsgBn = it },
                                label = { Text(if (isBn) "সতর্কতা বার্তা (যেমন: ওয়ান-ওয়ে রোড) *" else "Advisory Message *") },
                                minLines = 2,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    selected = newAdvSeverity == AdvisorySeverity.RESTRICTION,
                                    onClick = { newAdvSeverity = AdvisorySeverity.RESTRICTION },
                                    label = { Text("নিষেধাজ্ঞা / Restriction") }
                                )
                                FilterChip(
                                    selected = newAdvSeverity == AdvisorySeverity.INFO,
                                    onClick = { newAdvSeverity = AdvisorySeverity.INFO },
                                    label = { Text("বিজ্ঞপ্তি / Info") }
                                )
                            }

                            Button(
                                onClick = {
                                    if (newAdvTitleBn.isNotBlank() && newAdvMsgBn.isNotBlank()) {
                                        viewModel.adminPublishAdvisory(
                                            titleBn = newAdvTitleBn,
                                            titleEn = if (newAdvTitleEn.isBlank()) newAdvTitleBn else newAdvTitleEn,
                                            messageBn = newAdvMsgBn,
                                            messageEn = if (newAdvMsgEn.isBlank()) newAdvMsgBn else newAdvMsgEn,
                                            severity = newAdvSeverity
                                        )
                                        newAdvTitleBn = ""
                                        newAdvTitleEn = ""
                                        newAdvMsgBn = ""
                                        newAdvMsgEn = ""
                                    }
                                },
                                enabled = newAdvTitleBn.isNotBlank() && newAdvMsgBn.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = Maroon700),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Publish, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "সতর্কতা প্রকাশ করুন" else "Publish Advisory")
                            }
                        }
                    }
                }

                // 2. Review User Submitted Corrections
                item {
                    Text(
                        text = if (isBn) "ব্যবহারকারীদের পাঠানো সংশোধন ও পর্যালোচনার তালিকা (${corrections.size})" else "User Correction Submissions (${corrections.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Maroon900
                    )
                }

                items(corrections) { corr ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = corr.placeName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Maroon900
                                )
                                Surface(
                                    color = if (corr.status.contains("অনুমোদিত")) StatusGreen.copy(alpha = 0.15f) else Gold100,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = corr.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (corr.status.contains("অনুমোদিত")) StatusGreen else Gold900,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("ধরন: ${corr.correctionType}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("যোগাযোগ: ${corr.userEmailOrPhone} • ${corr.submittedAt}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(corr.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)

                            if (!corr.status.contains("অনুমোদিত")) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.adminApproveCorrection(corr.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isBn) "অনুমোদন করুন ও ডিরেক্টরিতে যোগ করুন" else "Approve Correction", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
