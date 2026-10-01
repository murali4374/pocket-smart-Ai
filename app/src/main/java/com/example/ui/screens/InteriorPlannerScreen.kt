package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.components.BudgetSummaryPanel
import com.example.ui.components.formatInr
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BudgetSafeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteriorPlannerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val budget by viewModel.homeBudget.collectAsStateWithLifecycle()
    val roomType by viewModel.homeRoomType.collectAsStateWithLifecycle()
    val style by viewModel.homeStyle.collectAsStateWithLifecycle()
    val colors by viewModel.homeColors.collectAsStateWithLifecycle()
    val items by viewModel.homeItems.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingHome.collectAsStateWithLifecycle()
    val result by viewModel.homeResult.collectAsStateWithLifecycle()

    val roomOptions = listOf("Living Room", "Bedroom", "Kitchen", "Dining Room", "Home Office")
    val styleOptions = listOf("Modern", "Minimalist", "Traditional", "Scandinavian", "Industrial")
    val quickItemPresets = listOf("Sofa", "Coffee Table", "Curtains", "Floor Lamp", "Area Rug", "TV Console", "Wall Art")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("interior_planner_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_interior),
                        contentDescription = "Interior Planner Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.55f))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = BrandPrimary
                        ) {
                            Text(
                                text = "HOME INTERIOR",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Interior Budget Planner",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Allocate furniture within your total budget ceiling",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                        )
                    }
                }
            }
        }

        // Input Form Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("interior_form_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Budget Input
                    OutlinedTextField(
                        value = budget,
                        onValueChange = { viewModel.homeBudget.value = it },
                        label = { Text("Total Budget (₹ INR) *") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_interior_budget")
                    )

                    // Room Type Selector
                    Column {
                        Text(
                            text = "Select Room Type",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(roomOptions) { room ->
                                FilterChip(
                                    selected = roomType == room,
                                    onClick = { viewModel.homeRoomType.value = room },
                                    label = { Text(room) },
                                    modifier = Modifier.testTag("chip_room_$room")
                                )
                            }
                        }
                    }

                    // Interior Style Selector
                    Column {
                        Text(
                            text = "Interior Aesthetic / Style",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(styleOptions) { s ->
                                FilterChip(
                                    selected = style == s,
                                    onClick = { viewModel.homeStyle.value = s },
                                    label = { Text(s) }
                                )
                            }
                        }
                    }

                    // Preferred Color Palette
                    OutlinedTextField(
                        value = colors,
                        onValueChange = { viewModel.homeColors.value = it },
                        label = { Text("Color Palette / Materials") },
                        leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null) },
                        placeholder = { Text("e.g. Warm Beige, Walnut Wood, Brass") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Items to Procure
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Items to Procure (${items.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(
                            onClick = { viewModel.addHomeItem("Accent Item", 1) },
                            modifier = Modifier.testTag("btn_add_item")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Item")
                        }
                    }

                    // Quick item presets
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(quickItemPresets) { preset ->
                            SuggestionChip(
                                onClick = { viewModel.addHomeItem(preset, 1) },
                                label = { Text("+ $preset", fontSize = 12.sp) }
                            )
                        }
                    }

                    // Dynamic Item Rows
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = item.itemName,
                                onValueChange = { newName -> viewModel.updateHomeItem(index, newName, item.quantity) },
                                label = { Text("Item ${index + 1}") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_item_name_$index")
                            )

                            // Quantity Stepper
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(horizontal = 4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (item.quantity > 1) {
                                            viewModel.updateHomeItem(index, item.itemName, item.quantity - 1)
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                                }
                                Text(
                                    text = "${item.quantity}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                IconButton(
                                    onClick = {
                                        viewModel.updateHomeItem(index, item.itemName, item.quantity + 1)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                                }
                            }

                            // Delete button if more than 1 item
                            if (items.size > 1) {
                                IconButton(
                                    onClick = { viewModel.removeHomeItem(index) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = "Remove item",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Submit Button
                    Button(
                        onClick = { viewModel.generateHomePlan() },
                        enabled = !isGenerating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_generate_interior_plan"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Gemini AI Architect Analyzing...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Interior Plan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Results Section
        result?.let { planResult ->
            item {
                BudgetSummaryPanel(
                    budget = planResult.budget,
                    estimatedTotal = planResult.estimatedTotal,
                    remainingBudget = planResult.remainingBudget
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Curated Allocations (${planResult.recommendations.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        onClick = { viewModel.saveHomePlan() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_save_interior_plan")
                    ) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Plan", fontSize = 13.sp)
                    }
                }
            }

            items(planResult.recommendations) { rec ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_rec_${rec.product.take(15)}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = rec.category.uppercase(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = rec.platform,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = rec.product,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = rec.reason,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Qty: ${rec.quantity} × ${formatInr(rec.estimatedPrice)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatInr(rec.totalPrice),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BudgetSafeGreen
                                    )
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rec.searchUrl))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Search on ${rec.platform}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
