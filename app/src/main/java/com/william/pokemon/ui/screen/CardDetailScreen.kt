package com.william.pokemon.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.william.pokemon.model.PokemonCard
import com.william.pokemon.ui.widgets.RemoteImage
import com.william.pokemon.viewmodel.DetailUiState
import com.william.pokemon.viewmodel.DetailViewModel
import java.util.Locale

private val BackgroundColor = Color(0xFF1A1F2B)
private val PanelColor = Color(0xFF222936)
private val GreenColor = Color(0xFF42B883)
private val YellowColor = Color(0xFFFFC83D)
private val RedColor = Color(0xFFCE493D)

@Composable
fun CardDetailScreen(
    onDone: () -> Unit,
    viewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.Factory
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        is DetailUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is DetailUiState.Success -> {
            val pokemon = (state as DetailUiState.Success).pokemon

            CardDetailContent(
                pokemon = pokemon,
                onDone = onDone
            )
        }

        is DetailUiState.Error -> {
            val message = (state as DetailUiState.Error).message

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(message, color = Color.White)
                Button(onClick = { viewModel.load() }) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun CardDetailContent(
    pokemon: PokemonCard,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        RemoteImage(
            url = pokemon.imageUrl,
            contentDescription = pokemon.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${pokemon.name} - ${pokemon.cardNumber}",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = pokemon.setName,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PriceInfoCard(
                title = "Market Price",
                price = pokemon.marketPrice,
                color = GreenColor,
                modifier = Modifier.weight(1f)
            )

            PriceInfoCard(
                title = "Lowest Price",
                price = pokemon.lowestPrice,
                color = YellowColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Market Activity",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        InfoPanel {
            InfoRow("Primary Printing", pokemon.primaryPrinting ?: "—")
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Card Specifications",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        InfoPanel {
            InfoRow("Card Number", pokemon.cardNumber)
            InfoRow("Rarity", pokemon.rarity)
            InfoRow("Type", pokemon.types.joinToString().ifBlank { "—" })
            InfoRow("Illustrator", pokemon.illustrator)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RedColor
            )
        ) {
            Text(
                text = "Done",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun PriceInfoCard(
    title: String,
    price: Double?,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelColor
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = price?.let {
                    String.format(Locale.US, "$%.2f", it)
                } ?: "Unavailable",
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InfoPanel(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelColor
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            content = content
        )
    }
}

@Composable
fun InfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Color.LightGray,
            fontSize = 13.sp
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
