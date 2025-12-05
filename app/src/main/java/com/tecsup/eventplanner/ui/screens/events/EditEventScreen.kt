package com.tecsup.eventplanner.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.eventplanner.ui.components.CustomTextField
import com.tecsup.eventplanner.ui.components.PrimaryButton
import com.tecsup.eventplanner.ui.components.SecondaryButton
import com.tecsup.eventplanner.ui.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: EventViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event = uiState.events.find { it.id == eventId }

    var title by remember { mutableStateOf(event?.title ?: "") }
    var date by remember { mutableStateOf(event?.date ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(event) {
        event?.let {
            title = it.title
            date = it.date
            description = it.description
        }
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            onNavigateBack()
            viewModel.clearMessages()
        }
    }

    if (event == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Evento no encontrado")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Icon
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Actualizar Información",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title field
                CustomTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = null
                    },
                    label = "Título del evento *",
                    leadingIcon = Icons.Default.Title,
                    isError = titleError != null,
                    supportingText = titleError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date field
                OutlinedTextField(
                    value = date,
                    onValueChange = { },
                    label = { Text("Fecha del evento *") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.EditCalendar, contentDescription = "Seleccionar fecha")
                        }
                    },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = dateError != null,
                    supportingText = if (dateError != null) {
                        { Text(dateError!!) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description field
                CustomTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Descripción (opcional)",
                    leadingIcon = Icons.Default.Description,
                    singleLine = false,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Error message
                if (uiState.errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = uiState.errorMessage!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                PrimaryButton(
                    text = "Guardar Cambios",
                    onClick = {
                        var hasError = false

                        if (title.isBlank()) {
                            titleError = "El título es requerido"
                            hasError = true
                        }

                        if (date.isBlank()) {
                            dateError = "La fecha es requerida"
                            hasError = true
                        }

                        if (!hasError) {
                            viewModel.updateEvent(
                                eventId = eventId,
                                title = title,
                                date = date,
                                description = description
                            )
                        }
                    },
                    isLoading = uiState.isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecondaryButton(
                    text = "Cancelar",
                    onClick = onNavigateBack
                )
            }

            // Loading overlay
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = millis
                            }
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            date = sdf.format(calendar.time)
                            dateError = null
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}