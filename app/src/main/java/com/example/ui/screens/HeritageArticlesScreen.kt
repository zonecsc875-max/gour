package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import com.example.data.model.Article
import com.example.data.repository.SantipurRepository
import com.example.ui.components.AlpanaDivider
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeritageArticlesScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI

    val articles = remember { SantipurRepository.getHeritageArticles() }
    var expandedArticleId by remember { mutableStateOf<String?>(articles.firstOrNull()?.id) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "শান্তিপুরের ইতিহাস ও ঐতিহ্য প্রবন্ধ" else "Santipur Cultural Heritage",
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
                .testTag("heritage_articles_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    color = Maroon900,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = if (isBn) "গবেষণা ও প্রাচীন ঐতিহ্য সংগ্রহ" else "Researched Cultural Chronicles",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold300
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBn)
                                "অদ্বৈত মহাপ্রভুর বৈষ্ণব পদচিহ্ন, সুপ্রাচীন আগমেশ্বরী তান্ত্রিক সাধনা এবং বিশ্বখ্যাত শান্তিপুরী তাঁতের গৌরবময় ইতিহাস।"
                            else
                                "Authentic essays on Advaita Acharya's Vaishnava lineage, Agameswari Shakti lore, and Bengal handloom weaving.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmCream
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        AlpanaDivider(color = Gold400.copy(alpha = 0.5f))
                    }
                }
            }

            items(articles) { article ->
                val isExpanded = expandedArticleId == article.id
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Maroon100,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${article.readTimeMinutes} ${if (isBn) "মিনিট পাঠ" else "min read"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Maroon800,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = article.publishedDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBn) article.titleBn else article.titleEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isBn) "লেখক: ${article.authorBn}" else "Author: ${article.authorEn}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold900,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBn) article.summaryBn else article.summaryEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isBn) article.contentBn else article.contentEn,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = {
                                expandedArticleId = if (isExpanded) null else article.id
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = if (isExpanded) (if (isBn) "সংক্ষেপ করুন" else "Collapse") else (if (isBn) "সম্পূর্ণ প্রবন্ধ পড়ুন" else "Read Full Article"),
                                fontWeight = FontWeight.Bold,
                                color = Maroon700
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Maroon700
                            )
                        }
                    }
                }
            }
        }
    }
}
