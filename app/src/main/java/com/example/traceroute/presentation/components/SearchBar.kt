package com.example.traceroute.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

@Composable
fun SearchBar(
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    onPredictionsChanged: (List<AutocompletePrediction>) -> Unit) {

    val context = LocalContext.current

    val placesClient =
        remember {
            Places.createClient(context)
        }

    Column {

        OutlinedTextField(
            value = value,

            onValueChange = { value ->
                onValueChange(value)
                if (value.isEmpty()) {

                    onPredictionsChanged(
                        emptyList()
                    )

                    return@OutlinedTextField
                }

                val request =
                    FindAutocompletePredictionsRequest
                        .builder()
                        .setQuery(value)
                        .build()

                placesClient
                    .findAutocompletePredictions(
                        request
                    )
                    .addOnSuccessListener {

                        onPredictionsChanged(
                            it.autocompletePredictions
                        )
                    }
            } ,

            modifier = Modifier.fillMaxWidth(),

            label = {
                Text(hint)
            },

            singleLine = true
        )
    }
}