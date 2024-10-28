package com.example.studyapp
//Programmer: Kene Ogbuefi
//Date: 9/13/2024; version 1
//Android Studio Koala Feature Drop| 2024.1.2
//macOS Sonoma 14.0
//Description: This app allows you to create a study session with a subject, tasks, and a timer.
//The color scheme of the app is customizable.


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "HomeScreen") {
        composable("HomeScreen") { HomeScreen(navController) }
        composable("SessionCreator") { SessionCreatorScreen(navController) }
        composable(
            "session_screen/{subject}/{tasks}/{timerMinutes}/{selectedColor}/{textColor}",
            arguments = listOf(
                navArgument("subject") { type = NavType.StringType },
                navArgument("tasks") { type = NavType.StringType },
                navArgument("timerMinutes") { type = NavType.IntType },
                navArgument("selectedColor") { type = NavType.IntType },
                navArgument("textColor") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            StudySessionScreen(
                navController = navController,
                subject = backStackEntry.arguments?.getString("subject") ?: "",
                tasks = backStackEntry.arguments?.getString("tasks") ?: "",
                timerMinutes = backStackEntry.arguments?.getInt("timerMinutes") ?: 0,
                selectedColor = Color(backStackEntry.arguments?.getInt("selectedColor") ?: 0),
                textColor = Color(backStackEntry.arguments?.getInt("textColor") ?: 0)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Study Session Manager", fontSize = 50.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("SessionCreator") },colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000000))) {
            Text(text = "New Session", fontSize = 24.sp)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionCreatorScreen(navController: NavController) {
    var subject by remember { mutableStateOf(TextFieldValue("")) }
    var task by remember { mutableStateOf(TextFieldValue("")) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    val tasks = remember { mutableStateListOf<String>() }
    var timerValue by remember { mutableStateOf(0f) }
    var textColor by remember { mutableStateOf(Color.White) }
    var selectedColor by remember { mutableStateOf(Color(0xFF000000)) } // Updated default color

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Subject Input
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subject") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = selectedColor,
                unfocusedBorderColor = selectedColor.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Task Input and Add Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = task,
                onValueChange = { task = it },
                label = { Text("Task") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = selectedColor,
                    unfocusedBorderColor = selectedColor.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    if (task.text.isNotBlank()) {
                        if (editingIndex != null) {
                            tasks[editingIndex!!] = task.text
                            editingIndex = null
                        } else {
                            tasks.add(task.text)
                        }
                        task = TextFieldValue("")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = selectedColor)
            ) {
                Text(if (editingIndex != null) "Save" else "Add Task", color = textColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Task List
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(modifier = Modifier.heightIn(max = maxHeight * 0.5f)) {
                itemsIndexed(tasks) { index, task ->
                    if (editingIndex == index) {
                        // Edit Mode
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = task,
                                onValueChange = { tasks[index] = it },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = selectedColor,
                                    unfocusedBorderColor = selectedColor.copy(alpha = 0.5f)
                                )
                            )
                            IconButton(onClick = { editingIndex = null }) {
                                Icon(Icons.Filled.Check, contentDescription = "Save", tint = selectedColor)
                            }
                        }
                    } else {
                        // View Mode
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = task, modifier = Modifier.weight(1f))
                            IconButton(onClick = { editingIndex = index }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = selectedColor)
                            }
                            IconButton(onClick = { tasks.removeAt(index) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = selectedColor)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer
        Text("Timer: ${timerValue.toInt()} minutes")
        Slider(
            value = timerValue,
            onValueChange = { timerValue = it },
            valueRange = 0f..120f,
            colors = SliderDefaults.colors(
                thumbColor = selectedColor,
                activeTrackColor = selectedColor
            )
        )

        // Color Picker
        Spacer(modifier = Modifier.height(16.dp))
        ColorPicker(
            selectedColor = selectedColor,
            onColorSelected = { color ->
                selectedColor = color
            },
            textColor = textColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Text Color Switch
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Text Color")
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = textColor == Color.Black,
                onCheckedChange = {
                    textColor = if (it) Color.Black else Color.White
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    uncheckedThumbColor = Color.White,
                    checkedTrackColor = selectedColor,
                    uncheckedTrackColor = selectedColor.copy(alpha = 0.5f)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Start Session Button
        Button(
            onClick = {
                navController.navigate(
                    "session_screen/${subject.text}/${tasks.joinToString(",")}/${timerValue.toInt()}/${
                        selectedColor.toArgb()
                    }/${
                        textColor.toArgb()
                    }"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = selectedColor)
        ) {
            Text("Start Session", color = textColor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySessionScreen(
    navController: NavController,
    subject: String,
    tasks: String,
    timerMinutes: Int,
    selectedColor: Color,
    textColor: Color
) {
    var studyMode by remember { mutableStateOf(true) }
    val taskList = remember {
        tasks.split(",").filter { it.isNotBlank() }.map { Task(it) }
            .toMutableStateList()
    }
    var timeRemaining by remember { mutableStateOf(timerMinutes * 60) }
    var timerRunning by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = timerRunning, key2 = timeRemaining) {
        if (timerRunning && timeRemaining > 0) {
            delay(1000L)
            timeRemaining -= 1
        }
        else if (timeRemaining == 0) {
            showDialog = true
        }
    }
    val formattedTime = remember(timeRemaining) {
        val minutes = timeRemaining / 60
        val seconds = timeRemaining % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (studyMode) selectedColor else selectedColor.copy(alpha = .5f))
    ) {
        if (studyMode) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = subject,
                    color = textColor,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .border(2.dp, textColor, shape = RoundedCornerShape(4.dp))
                        .background(color = Color.White.copy(alpha = .5f))
                        .padding(20.dp)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(taskList) { index, task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillParentMaxWidth()
                                    .background(selectedColor)

                            ) {
                                Checkbox(
                                    checked = task.isChecked,
                                    onCheckedChange = {
                                        taskList[index] = task.copy(isChecked = it)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = textColor.copy(alpha = 0.5f),
                                        uncheckedColor = textColor,
                                        checkmarkColor = textColor,
                                    )
                                )
                                Text(text = task.description, fontSize = 18.sp, color = textColor)
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.Center),
            ) {
                Text(
                    text = "Break Mode",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                        .offset(y = -60.dp),
                    color = textColor
                )
            }
        }
        // Timer
        Box(
            modifier = Modifier
                .size(150.dp, 50.dp)
                .align(Alignment.Center)
                .border(2.dp, textColor, shape = RoundedCornerShape(20.dp))
                .background(
                    selectedColor.copy(alpha = 0f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Text(
                text = formattedTime,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Switch(
                checked = studyMode,
                onCheckedChange = {
                    studyMode = it
                    timerRunning = it
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = textColor,
                    uncheckedThumbColor = textColor,
                    checkedTrackColor = Color.White.copy(alpha = 0.5f)
                    ,
                    uncheckedTrackColor = selectedColor,

                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate(
                        route = "HomeScreen"
                    )
                },
                modifier = Modifier.fillMaxWidth()
                    .border(1.dp, color = textColor, shape = RectangleShape)
                    .background(selectedColor,shape = RectangleShape),
                colors = ButtonDefaults.buttonColors(containerColor = selectedColor)
            ) {
                Text("Quit", color = textColor)
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    navController.popBackStack()
                },
                title = { Text("Timer Finished!") },
                text = { Text("Your session is complete.") },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        navController.popBackStack()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

data class Task(val description: String, var isChecked: Boolean = false)

@Composable
fun ColorPicker(selectedColor: Color, onColorSelected: (Color) -> Unit,textColor: Color) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {

            },
            title = { Text("Choose a color") },
            text = {
                ColorPickerDialog(
                    initialColor = selectedColor,
                    onColorSelected = { color ->
                        onColorSelected(color)
                        showDialog = false
                    }
                )
            }
        )
    }

    Button(onClick = { showDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = selectedColor)) {
        Text("Select Color",color = textColor)
    }
}


@Composable
fun ColorPickerDialog(initialColor: Color, onColorSelected: (Color) -> Unit) {
    var selectedColor by remember { mutableStateOf(initialColor) }

    Column {
        // You can use any color picker library here, for example:
        // 1. Compose Material You: https://github.com/lgvalle/compose-material-you
        // 2. AndroidX Color Picker: https://developer.android.com/jetpack/androidx/releases/compose-material3#1.2.0-alpha01
        // Here's a simple example using a Slider:
        Slider(
            value = selectedColor.red,
            onValueChange = { selectedColor = selectedColor.copy(red = it) },
            valueRange = 0f..1f
        )
        Slider(
            value = selectedColor.green,
            onValueChange = { selectedColor = selectedColor.copy(green = it) },
            valueRange = 0f..1f
        )
        Slider(
            value = selectedColor.blue,
            onValueChange = { selectedColor = selectedColor.copy(blue = it) },
            valueRange = 0f..1f
        )

        // Display the selected color
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(selectedColor)
        )

        Button(onClick = { onColorSelected(selectedColor) }) {
            Text("Confirm")
        }
    }
}