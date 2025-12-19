package com.example.smartfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartfit.ui.theme.SmartfitTheme
import java.util.UUID

data class ActivityItem(val id: String = UUID.randomUUID().toString(), val name: String, val duration: String, val calories: String)

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartfitTheme {
                MainScreen(onLogout = { /* Handled in LoginSignInScreen */ })
            }
        }
    }
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(
                    Triple("home", "Home", Icons.Filled.Home),
                    Triple("goals", "Daily Goals", Icons.Filled.Flag),
                    Triple("activity_log", "Activity", Icons.Filled.DirectionsRun),
                    Triple("profile", "Profile", Icons.Filled.Person)
                )
                items.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFFFF9800)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController, startDestination = "home",
            Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("goals") { DailyGoalsScreen(navController) }
            composable("activity_log") { ActivityLogScreen() }
            composable("profile") { ProfileScreen(navController, onLogout) }
            composable(
                route = "savedGoals/{steps}/{calories}/{water}",
                arguments = listOf(
                    navArgument("steps") { type = NavType.StringType },
                    navArgument("calories") { type = NavType.StringType },
                    navArgument("water") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val steps = backStackEntry.arguments?.getString("steps") ?: ""
                val calories = backStackEntry.arguments?.getString("calories") ?: ""
                val water = backStackEntry.arguments?.getString("water") ?: ""
                SavedGoalsScreen(steps, calories, water)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    var weight by remember { mutableStateOf(TextFieldValue("")) }
    var height by remember { mutableStateOf(TextFieldValue("")) }
    var bmiResult by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var bmiCategory by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Suggestions") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Image(
                painter = painterResource(id = R.drawable.user1),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Hi,",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        // Steps Tracker Card
        Card(
            shape = RoundedCornerShape(17.dp),
            colors = cardColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Steps Tracker",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFCF9F9), Color(0xFFF5F5F5))),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {}
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BMI Calculator Card
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = cardColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Bmi Calculator",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Weight (kg)", fontWeight = FontWeight.Bold, color = Color.White)
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text(text = "Enter your weight in kg") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Height (cm)", fontWeight = FontWeight.Bold, color = Color.White)
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text(text = "Enter your height in centimeters") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {
                            val weightF = weight.text.toFloatOrNull() ?: 0f
                            val heightF = height.text.toFloatOrNull() ?: 0f

                            if (weightF > 0 && heightF > 0) {
                                val heightM = heightF / 100f   // convert cm → meters
                                val bmi = weightF / (heightM * heightM)
                                bmiResult = String.format("%.2f", bmi)

                                bmiCategory = when {
                                    bmi < 18.5 -> "Underweight"
                                    bmi < 24.9 -> "Normal"
                                    bmi < 29.9 -> "Overweight"
                                    else -> "Obese"
                                }
                            }

                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        modifier = Modifier
                            .height(45.dp)
                            .width(120.dp)
                    ) {
                        Text("Calculate", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            "BMI: $bmiResult",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "You are: $bmiCategory",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

// Suggestions / History Section (Separated)
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                // Selector pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            color = Color(0xFFF2F2F2),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SelectorItem(
                            text = "Suggestions",
                            isSelected = selectedTab == "Suggestions",
                            onClick = { selectedTab = "Suggestions" }
                        )
                        SelectorItem(
                            text = "History",
                            isSelected = selectedTab == "History",
                            onClick = { selectedTab = "History" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Empty content area (for now)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
        }

    }
}

@Composable
fun RowScope.SelectorItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected) Color(0xFFFF9800) else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun DailyGoalsScreen(navController: NavController) {
    var stepsGoal by remember { mutableStateOf("") }
    var caloriesGoal by remember { mutableStateOf("") }
    var waterGoal by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Daily Goals", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = cardColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Motivational Quote",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\"Talent without hardwork is nothing\"",
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = painterResource(id = R.drawable.quote),
                    contentDescription = "Quote icon",
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = cardColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxWidth()
            ) {
                Text(
                    text = "Set Your Daily Goals",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Steps Goal", fontWeight = FontWeight.Bold, color = Color.White)
                OutlinedTextField(
                    value = stepsGoal,
                    onValueChange = { stepsGoal = it },
                    singleLine = true,
                    label = { Text("Enter steps goal") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Calories Goal", fontWeight = FontWeight.Bold, color = Color.White)
                OutlinedTextField(
                    value = caloriesGoal,
                    onValueChange = { caloriesGoal = it },
                    singleLine = true,
                    label = { Text("Enter calories goal") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Water Goal (cups)", fontWeight = FontWeight.Bold, color = Color.White)
                OutlinedTextField(
                    value = waterGoal,
                    onValueChange = { waterGoal = it },
                    singleLine = true,
                    label = { Text("Enter water goal") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate("savedGoals/${stepsGoal}/${caloriesGoal}/${waterGoal}")
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                ) {
                    Text("Save Goals", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SavedGoalsScreen(stepsGoal: String, caloriesGoal: String, waterGoal: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Today's Goals",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF9800)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = cardColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Steps Goal: $stepsGoal", color = Color.White, fontSize = 20.sp)
                Text("Calories Goal: $caloriesGoal", color = Color.White, fontSize = 20.sp)
                Text("Water Goal: $waterGoal cups", color = Color.White, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, onLogout: () -> Unit) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Profile Image
        Image(
            painter = painterResource(id = R.drawable.user1), // YOUR PROFILE PIC
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)   // makes it circular
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "Account Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF9800)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Change Password Section
        Text(
            text = "Change Password",
            color = Color(0xFFFF9800),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // New Password
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Save Button
        Button(
            onClick = {
                // TODO: Add password saving logic
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Save Password", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActivityLogScreen() {
    var activityName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var activities by remember { mutableStateOf(listOf<ActivityItem>()) }
    var editingActivity by remember { mutableStateOf<ActivityItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- FORM --- //
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = cardColors(containerColor = Color(0xFFFF9800))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (editingActivity == null) "Add Activity" else "Edit Activity",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                val textFieldColors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )

                OutlinedTextField(
                    value = activityName,
                    onValueChange = { activityName = it },
                    label = { Text("Activity Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories Burned") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(
                        onClick = {
                            if (editingActivity == null) { // ADD
                                val newActivity = ActivityItem(name = activityName, duration = duration, calories = calories)
                                activities = activities + newActivity
                            } else { // UPDATE
                                val updatedActivity = editingActivity!!.copy(name = activityName, duration = duration, calories = calories)
                                activities = activities.map { if (it.id == editingActivity!!.id) updatedActivity else it }
                            }
                            // Reset fields
                            activityName = ""
                            duration = ""
                            calories = ""
                            editingActivity = null
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                    ) {
                        Text(if (editingActivity == null) "Add Activity" else "Update Activity", color = Color.White)
                    }
                    if (editingActivity != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { // CANCEL
                                activityName = ""
                                duration = ""
                                calories = ""
                                editingActivity = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
                        ) {
                            Text("Cancel", color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- LIST --- //
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(activities) { activity ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = cardColors(containerColor = Color(0xFFFF9800))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(activity.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            Text("${activity.duration} min - ${activity.calories} cal", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        }
                        Row {
                            Button(onClick = { // EDIT
                                editingActivity = activity
                                activityName = activity.name
                                duration = activity.duration
                                calories = activity.calories
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                            ) {
                                Text("Edit", color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { // DELETE
                                activities = activities - activity
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF773030)) // Red for delete
                            ) {
                                Text("Delete", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SmartfitTheme {
        MainScreen(onLogout = {})
    }
}
