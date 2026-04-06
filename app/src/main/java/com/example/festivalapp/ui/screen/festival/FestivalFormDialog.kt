package com.example.festivalapp.ui.screen.festival

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.festivalapp.data.festival.Festival
import com.example.festivalapp.data.festival.TariffZone

@Composable
fun FestivalFormDialog(
    festival: Festival?,
    isEditMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (Festival) -> Unit,
    onCalculateRemaining: (Festival) -> Map<String, Int>,
    onPrepareFestival: (String, List<TariffZone>, String?, String?) -> Festival
) {
    var festivalName by remember { mutableStateOf(festival?.name ?: "") }
    var beginDate by remember { mutableStateOf(festival?.beginDate ?: "") }
    var endDate by remember { mutableStateOf(festival?.endDate ?: "") }
    var zones by remember { mutableStateOf(festival?.tariffZones?.toList() ?: emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { onDismiss() },  
        modifier = Modifier.fillMaxWidth(0.9f),
        title = { Text(if (isEditMode) "Modifier le festival" else "Créer un festival") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = festivalName,
                    onValueChange = { festivalName = it },
                    label = { Text("Nom du festival") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    isError = festivalName.isEmpty()
                )
                OutlinedTextField(
                    value = beginDate,
                    onValueChange = { beginDate = it },
                    label = { Text("Date de début (optionnel)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Date de fin (optionnel)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
                Text(
                    text = "Zones tarifaires",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                if (zones.isEmpty()) {
                    Text(
                        text = "Au moins une zone tarifaire est requise",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                zones.forEachIndexed { index, zone ->
                    TariffZoneFormField(
                        zone = zone,
                        onZoneChange = { updatedZone ->
                            zones = zones.toMutableList().apply { set(index, updatedZone) }
                        },
                        onRemove = { zones = zones.toMutableList().apply { removeAt(index) } }
                    )
                }
                Button(
                    onClick = {
                        zones = zones.toMutableList().apply {
                            add(TariffZone(idTZ = 0, festivalName = festivalName))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("Ajouter une zone")
                }
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validation du nom
                    if (festivalName.isBlank()) {
                        errorMessage = "Le nom du festival est requis"
                        return@Button
                    }
                    // Validation des zones
                    if (zones.isEmpty()) {
                        errorMessage = "Au moins une zone tarifaire est requise"
                        return@Button
                    }
                    // Validation que chaque zone a au moins une table
                    val hasInvalidZone = zones.any { zone ->
                        zone.nbSmallTables == 0 && 
                        zone.nbLargeTables == 0 && 
                        zone.nbCityHallTables == 0
                    }
                    if (hasInvalidZone) {
                        errorMessage = "Chaque zone doit avoir au moins une table"
                        return@Button
                    }
                    
                    // Si tout est valide, réinitialise le message d'erreur
                    errorMessage = null
                    
                    // Prépare et sauvegarde le festival
                    val prepared = onPrepareFestival(
                        festivalName, 
                        zones, 
                        beginDate.ifBlank { null }, 
                        endDate.ifBlank { null }
                    )
                    println("✅ Festival préparé: $prepared")
                    println("✅ onSave appelé avec: $prepared")
                    onSave(prepared)
                },
                enabled = festivalName.isNotBlank() && zones.isNotEmpty()
            ) {
                Text(if (isEditMode) "Modifier" else "Créer")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun TariffZoneFormField(
    zone: TariffZone,
    onZoneChange: (TariffZone) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zone: ${zone.name.ifBlank { "Sans nom" }}",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Close, contentDescription = "Supprimer")
                }
            }
            OutlinedTextField(
                value = zone.name,
                onValueChange = { onZoneChange(zone.copy(name = it)) },
                label = { Text("Nom") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = (zone.nbSmallTables ?: 0).toString(),
                    onValueChange = {
                        val value = it.toIntOrNull() ?: 0
                        onZoneChange(zone.copy(nbSmallTables = value, remainingSmallTables = value))
                    },
                    label = { Text("Petites") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = (zone.nbLargeTables ?: 0).toString(),
                    onValueChange = {
                        val value = it.toIntOrNull() ?: 0
                        onZoneChange(zone.copy(nbLargeTables = value, remainingLargeTables = value))
                    },
                    label = { Text("Grandes") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = (zone.nbCityHallTables ?: 0).toString(),
                    onValueChange = {
                        val value = it.toIntOrNull() ?: 0
                        onZoneChange(zone.copy(nbCityHallTables = value, remainingCityHallTables = value))
                    },
                    label = { Text("Mairie") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = zone.smallTablePrice.toString(),
                    onValueChange = { onZoneChange(zone.copy(smallTablePrice = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Prix petite") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = zone.largeTablePrice.toString(),
                    onValueChange = { onZoneChange(zone.copy(largeTablePrice = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Prix grande") },
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = zone.cityHallTablePrice.toString(),
                onValueChange = { onZoneChange(zone.copy(cityHallTablePrice = it.toDoubleOrNull() ?: 0.0)) },
                label = { Text("Prix mairie") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = zone.squareMeterPrice.toString(),
                onValueChange = { onZoneChange(zone.copy(squareMeterPrice = it.toDoubleOrNull() ?: 0.0)) },
                label = { Text("Prix m²") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}