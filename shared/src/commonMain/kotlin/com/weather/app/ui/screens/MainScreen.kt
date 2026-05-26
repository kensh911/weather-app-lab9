package com.weather.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weather.app.model.WeatherData
import com.weather.app.ui.components.*
import com.weather.app.viewmodel.WeatherViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onCityClick: (WeatherData) -> Unit,
    viewModel: WeatherViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Погода") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value         = state.searchQuery,
                        onValueChange = viewModel::onSearchChange,
                        modifier      = Modifier.weight(1f),
                        placeholder   = { Text("Введите город...") },
                        singleLine    = true,
                        shape         = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { viewModel.searchCity() })
                    )
                    Button(
                        onClick  = viewModel::searchCity,
                        modifier = Modifier.height(56.dp),
                        shape    = RoundedCornerShape(12.dp)
                    ) { Text("Найти") }
                }
            }

            items(state.errors.entries.toList(), key = { it.key + "_err" }) { (city, msg) ->
                ErrorCard(city = city, message = msg, onDismiss = { viewModel.dismissError(city) })
            }

            items(state.loading.toList(), key = { it + "_loading" }) { city ->
                LoadingCard(cityName = city)
            }

            items(state.cities, key = { it.city }) { weather ->
                CityCard(weather = weather, onClick = { onCityClick(weather) })
            }
        }
    }
}