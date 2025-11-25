package com.example.seepawandroid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R
import com.example.seepawandroid.data.remote.dtos.Animals.AnimalFilterDto

/**
 * Bottom sheet component used to present animal filtering options.
 *
 * This UI component allows users to filter animals by:
 * - Species (Dog, Cat)
 * - Size (Small, Medium, Large)
 * - Sex (Male, Female)
 * - Breed (dropdown)
 * - Shelter name (text input)
 *
 * @param breedOptions List of available breeds to populate the dropdown menu.
 * @param onDismiss Callback invoked when the bottom sheet is dismissed.
 * @param onApply Called when the user presses the "Apply" button.
 *                It returns an [AnimalFilterDto] built from user selections.
 * @param onReset Called when the user clicks the "Reset" button to clear filters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    breedOptions: List<String>,
    onDismiss: () -> Unit,
    onApply: (AnimalFilterDto) -> Unit,
    onReset: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {

        var species by remember { mutableStateOf<String?>(null) }
        var size by remember { mutableStateOf<String?>(null) }
        var sex by remember { mutableStateOf<String?>(null) }
        var breed by remember { mutableStateOf("") }
        var shelter by remember { mutableStateOf("") }
        var expandedBreed by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = stringResource(R.string.filter_title),
                style = MaterialTheme.typography.headlineSmall
            )

            // -----------------------------
            // SPECIES
            // -----------------------------
            Text(stringResource(R.string.filter_species))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChipItem(
                    stringResource(R.string.species_dog_label),
                    species == "Dog",
                    onClick = { species = "Dog" },
                    modifier = Modifier.testTag("filter_species_dog")
                )
                FilterChipItem(
                    stringResource(R.string.species_cat_label),
                    species == "Cat",
                    onClick = { species = "Cat" },
                    modifier = Modifier.testTag("filter_species_cat")
                )
            }

            // -----------------------------
            // SIZE
            // -----------------------------
            Text(stringResource(R.string.filter_size))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChipItem(
                    stringResource(R.string.size_small_label),
                    size == "Small",
                    onClick = { size = "Small" },
                    modifier = Modifier.testTag("filter_size_small")
                )
                FilterChipItem(
                    stringResource(R.string.size_medium_label),
                    size == "Medium",
                    onClick = { size = "Medium" },
                    modifier = Modifier.testTag("filter_size_medium")
                )
                FilterChipItem(
                    stringResource(R.string.size_large_label),
                    size == "Large",
                    onClick = { size = "Large" },
                    modifier = Modifier.testTag("filter_size_large")
                )
            }

            // -----------------------------
            // SEX
            // -----------------------------
            Text(stringResource(R.string.filter_sex))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChipItem(
                    stringResource(R.string.sex_male_label),
                    sex == "Male",
                    onClick = { sex = "Male" },
                    modifier = Modifier.testTag("filter_sex_male")
                )
                FilterChipItem(
                    stringResource(R.string.sex_female_label),
                    sex == "Female",
                    onClick = { sex = "Female" },
                    modifier = Modifier.testTag("filter_sex_female")
                )
            }

            // -----------------------------
            // BREED (DROPDOWN)
            // -----------------------------
            Text(stringResource(R.string.filter_breed))

            ExposedDropdownMenuBox(
                expanded = expandedBreed,
                onExpandedChange = { expandedBreed = !expandedBreed },
                modifier = Modifier.testTag("filter_breed_dropdown")
            ) {
                OutlinedTextField(
                    value = breed,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBreed)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expandedBreed,
                    onDismissRequest = { expandedBreed = false }
                ) {
                    breedOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            modifier = Modifier.testTag("filter_breed_option_${option}"),
                            onClick = {
                                breed = option
                                expandedBreed = false
                            }
                        )
                    }
                }
            }

            // -----------------------------
            // SHELTER
            // -----------------------------
            OutlinedTextField(
                value = shelter,
                onValueChange = { shelter = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("filter_shelter"),
                label = { Text(stringResource(R.string.filter_shelter)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // -----------------------------
            // RESET / APPLY BUTTONS
            // -----------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = onReset,
                    modifier = Modifier.testTag("resetFiltersButton")
                ) {
                    Text(stringResource(R.string.filter_reset))
                }

                Button(
                    onClick = {
                        onApply(
                            AnimalFilterDto(
                                species = species,
                                size = size,
                                sex = sex,
                                breed = breed.ifBlank { null },
                                shelterName = shelter.ifBlank { null }
                            )
                        )
                    },
                    modifier = Modifier.testTag("applyFiltersButton")
                ) {
                    Text(stringResource(R.string.filter_apply))
                }
            }
        }
    }
}
