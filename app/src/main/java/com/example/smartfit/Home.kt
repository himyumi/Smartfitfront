package com.example.smartfit


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.filled.*
import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.*
// ^ Ensure icons are imported


class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemTheme) }
            SmartfitTheme(darkTheme = isDarkTheme) {
                MainScreen(
                    onLogout = { /* Handle logout */ },
                    onThemeChange = { isDarkTheme = it },
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun MainScreen(onLogout: () -> Unit, onThemeChange: (Boolean) -> Unit, isDarkTheme: Boolean) {
    val navController = rememberNavController()
    val activities = remember { mutableStateOf(listOf<ActivityItem>()) }
    var stepsGoal by remember { mutableStateOf(0) }
    var caloriesGoal by remember { mutableStateOf(0) }
    var waterGoal by remember { mutableStateOf(0) }
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
                            indicatorColor = MaterialTheme.colorScheme.primary
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
            composable("home") {
                HomeScreen(
                    navController = navController,
                    bmiCategoryFromProfile = "",
                    activities = activities.value,
                    stepsGoal = stepsGoal
                )
            }
            composable("home/{bmiCategory}") { backStackEntry ->
                HomeScreen(
                    navController = navController,
                    bmiCategoryFromProfile = backStackEntry.arguments?.getString("bmiCategory") ?: "",
                    activities = activities.value,
                    stepsGoal = stepsGoal
                )
            }
            composable("goals") {
                DailyGoalsScreen(
                    navController = navController,
                    onSaveGoals = { steps, calories, water ->
                        stepsGoal = steps
                        caloriesGoal = calories
                        waterGoal = water
                    }
                )
            }
            composable("activity_log") {
                ActivityLogScreen(
                    activities = activities.value,
                    onActivitiesChange = { activities.value = it }
                )
            }

            composable("profile") { ProfileScreen(navController, onLogout, isDarkTheme, onThemeChange) }
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
fun HomeScreen(
    navController: NavController,
    bmiCategoryFromProfile: String,
    activities: List<ActivityItem>,
    stepsGoal: Int,
    viewModel: SuggestionViewModel = viewModel()
) {

    LaunchedEffect(bmiCategoryFromProfile) {
        if (bmiCategoryFromProfile.isNotEmpty()) {
            viewModel.fetchSuggestions(bmiCategoryFromProfile)
        }
    }

    val bmiCategoryFromProfile =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<String>("bmiCategory") ?: bmiCategoryFromProfile

    val scrollState = rememberScrollState()
    var steps by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.background2),
            contentDescription = "App Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {

        // Welcome Text Only
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user1),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Hi,",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = "James",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.user1), // add image to drawable
                contentDescription = "Profile",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }


        Spacer(modifier = Modifier.height(45.dp))

        // Steps Tracker Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = cardColors(containerColor = MaterialTheme.colorScheme.primary),
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

                Text(
                    text = "$steps steps",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (stepsGoal > 0) {
                    Text(
                        text = "Goal: $steps / $stepsGoal",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                if (steps >= stepsGoal && stepsGoal > 0) {
                    Text(
                        text = "Goal achieved! 🎉",
                        fontSize = 16.sp,
                        color = Color.Green
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(onClick = { if (steps > 0) steps-- }) {
                        Text("-")
                    }

                    Button(onClick = { steps++ }) {
                        Text("+")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(34.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Summary",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                SummaryTab(activities = activities)
            }
        }

        Spacer(modifier = Modifier.height(34.dp))

        Text(
            text = "Your Plan",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        var selectedTab by remember { mutableStateOf("Activity") }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(50.dp))
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Activity", "Suggestions").forEach { tab ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            if (selectedTab == tab)
                                MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable { selectedTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == tab) Color.White else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (selectedTab == "Activity") {
            if (activities.isEmpty()) {
                Text(
                    text = "No activities yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                activities.takeLast(3).reversed().forEach { activity ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                activity.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                "${activity.duration} min • ${activity.calories} cal",
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }



        if (selectedTab == "Suggestions") {

            // 1. Trigger API call when entering this tab
            // We use 'Unit' key to force it to check every time the tab is opened
            LaunchedEffect(Unit) {
                if (bmiCategoryFromProfile.isNotEmpty()) {
                    viewModel.fetchSuggestions(bmiCategoryFromProfile)
                }
            }

            // 2. DEBUG TEXT: This will tell you if the data arrived
            Text(
                text = if (bmiCategoryFromProfile.isEmpty()) "Status: No BMI Data" else "Status: $bmiCategoryFromProfile",
                color = if (bmiCategoryFromProfile.isEmpty()) Color.Red else Color.Green,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Handle Loading/Error/Success
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            else if (viewModel.errorMessage != null) {
                Text(text = "Error: ${viewModel.errorMessage}", color = Color.Red)
                Button(onClick = { viewModel.fetchSuggestions(bmiCategoryFromProfile) }) { Text("Retry") }
            }
            else if (viewModel.suggestions.isNotEmpty()) {
                // Display the list
                viewModel.suggestions.forEach { suggestion ->
                    var expanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { expanded = !expanded },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier
                            .padding(16.dp)
                            .animateContentSize()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = getIconByName(suggestion.icon ?: ""),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = suggestion.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (expanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = suggestion.description)
                            }
                        }
                    }
                }
            } else {
                // If category exists but list is empty (API loading issue)
                if (bmiCategoryFromProfile.isNotEmpty()) {
                    Text("Loading suggestions...", color = Color.Gray)
                } else {
                    Text("Go to Profile -> Enter Height/Weight -> Click Save.", color = Color.Gray)
                }
            }
        }

    }

}


@Composable
fun DailyGoalsScreen(
    navController: NavController,
    onSaveGoals: (Int, Int, Int) -> Unit
) {
    var stepsGoal by remember { mutableStateOf("") }
    var caloriesGoal by remember { mutableStateOf("") }
    var waterGoal by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Image(
        painter = painterResource(id = R.drawable.background2),
        contentDescription = "App Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Daily Goals", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = cardColors(containerColor = MaterialTheme.colorScheme.primary),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
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
                        onSaveGoals(
                            stepsGoal.toIntOrNull() ?: 0,
                            caloriesGoal.toIntOrNull() ?: 0,
                            waterGoal.toIntOrNull() ?: 0
                        )

                        navController.navigate("savedGoals/${stepsGoal}/${caloriesGoal}/${waterGoal}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                        .copy(contentColor = Color.White)
                ) {
                    Text("Save Goals", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SavedGoalsScreen(stepsGoal: String, caloriesGoal: String, waterGoal: String) {

    Image(
        painter = painterResource(id = R.drawable.background2),
        contentDescription = "App Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

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
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(30.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = cardColors(containerColor = MaterialTheme.colorScheme.primary),
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
fun ActivityLogScreen(activities: List<ActivityItem>,
                      onActivitiesChange: (List<ActivityItem>) -> Unit) {
    var activityName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var editingActivity by remember { mutableStateOf<ActivityItem?>(null) }

    Image(
        painter = painterResource(id = R.drawable.background2),
        contentDescription = "App Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- FORM --- //
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = cardColors(containerColor = MaterialTheme.colorScheme.primary)
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
                                val newActivity = ActivityItem(
                                    name = activityName,
                                    duration = duration,
                                    calories = calories
                                )
                                onActivitiesChange(activities + newActivity)
                                Log.d("ActivityLogScreen", "Activity added: $newActivity")
                            } else { // UPDATE
                                val updatedActivity = editingActivity!!.copy(
                                    name = activityName,
                                    duration = duration,
                                    calories = calories
                                )

                                onActivitiesChange(
                                    activities.map {
                                        if (it.id == editingActivity!!.id) updatedActivity else it
                                    }
                                )
                                Log.d("ActivityLogScreen", "Activity updated: $updatedActivity")
                            }
                            // Reset fields
                            activityName = ""
                            duration = ""
                            calories = ""
                            editingActivity = null
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
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
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
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
                    colors = cardColors(containerColor = MaterialTheme.colorScheme.primary)
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
                                Log.d("ActivityLogScreen", "Edit clicked for activity: $activity")
                            },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Text("Edit", color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { // DELETE
                                onActivitiesChange(activities - activity)
                                Log.d("ActivityLogScreen", "Activity deleted: $activity")
                            },
                                colors = ButtonDefaults.buttonColors(containerColor =  MaterialTheme.colorScheme.error) // Red for delete
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
data class ActivityItem(val id: String = UUID.randomUUID().toString(), val name: String, val duration: String, val calories: String)

@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    // State variables
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }

    // BMI Calculation Logic (Hidden calculation, can be used if needed)
    val bmi by remember {
        derivedStateOf {
            val w = weight.toFloatOrNull()
            val h = height.toFloatOrNull()
            if (w != null && h != null && h > 0f) w / ((h / 100f) * (h / 100f)) else null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Adapts to theme
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. Profile Picture
        Image(
            painter = painterResource(id = R.drawable.user1),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Profile Title
        Text(
            text = "Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 3. Body Information Section
        Text(
            text = "Body Type",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Height Input
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (cm)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            // Using default theme colors handles dark/light mode automatically
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Weight Input
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Save Button
        Button(
            onClick = {
                val category = getBmiCategory(weight, height)
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("bmiCategory", category)

                navController.popBackStack()
                Log.d("ProfileScreen", "BMI Category saved: $category")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Save", fontWeight = FontWeight.Bold)
        }


        Spacer(modifier = Modifier.height(30.dp))

        // 5. Dark / Light Mode Toggle (Custom Row)
        Text(
            text = "Theme",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp))
                .clip(RoundedCornerShape(50.dp))
        ) {
            // Dark Mode Button side
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    // If Dark mode is ON, background is Orange, otherwise Transparent
                    .background(if (isDarkTheme) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onThemeChange(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Dark",
                    fontWeight = FontWeight.Bold,
                    // If selected, text is White. If not, text is Orange.
                    color = if (isDarkTheme) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                )
            }

            // Light Mode Button side
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    // If Dark mode is OFF, background is Orange, otherwise Transparent
                    .background(if (!isDarkTheme) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onThemeChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Light",
                    fontWeight = FontWeight.Bold,
                    color = if (!isDarkTheme) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))



        Spacer(modifier = Modifier.height(16.dp))

        // Logout Text/Button
        Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
            shape = RoundedCornerShape(30.dp)) {
            Text("Log Out", color = Color.White, fontSize = 16.sp)
        }
    }
}

fun getBmiCategory(weight: String, height: String): String {
    val w = weight.toFloatOrNull()
    val h = height.toFloatOrNull()

    if (w == null || h == null || h <= 0f) return ""

    val bmi = w / ((h / 100f) * (h / 100f))

    return when {
        bmi < 18.5f -> "Underweight"
        bmi < 25f -> "Normal"
        bmi < 30f -> "Overweight"
        else -> "Obese"
    }
}


data class Suggestion(val title: String, val description: String)

fun getSuggestionDetails(bmiCategory: String): List<Suggestion> {
    return when (bmiCategory) {

        "Underweight" -> listOf(
            Suggestion(
                "Increase calorie intake",
                "Add healthy snacks like nuts, smoothies, and yogurt between meals."
            ),
            Suggestion(
                "Light strength training",
                "Focus on full-body workouts 2–3 times per week to build muscle safely."
            )
        )

        "Normal" -> listOf(
            Suggestion(
                "Stay consistent",
                "30 minutes of moderate exercise most days keeps metabolism balanced."
            ),
            Suggestion(
                "Balanced strength workout",
                "Alternate upper-body, lower-body, and core exercises each session."
            )
        )

        "Overweight" -> listOf(
            Suggestion(
                "Low-impact cardio",
                "Walking, cycling, or swimming burns fat while protecting joints."
            ),
            Suggestion(
                "Reduce sugar intake",
                "Replace soda and sweets with water, fruit, or unsweetened drinks."
            )
        )

        "Obese" -> listOf(
            Suggestion(
                "Short frequent walks",
                "Start with 10 minutes twice a day, then increase slowly."
            ),
            Suggestion(
                "Healthy portion control",
                "Use smaller plates and fill half your plate with vegetables."
            )
        )

        else -> emptyList()
    }
}

@Composable
fun SummaryTab(activities: List<ActivityItem>, modifier: Modifier = Modifier) {
    val activitiesCount = activities.size
    val totalMinutes = activities.mapNotNull { it.duration.toIntOrNull() }.sum()
    val totalCalories = activities.mapNotNull { it.calories.toIntOrNull() }.sum()

    val labelStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    val valueStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)

    Column(modifier = modifier.fillMaxWidth()) {
        SummaryRow("Activities", "$activitiesCount", labelStyle, valueStyle)
        Spacer(modifier = Modifier.height(8.dp))
        SummaryRow("Total Duration", "$totalMinutes min", labelStyle, valueStyle)
        Spacer(modifier = Modifier.height(8.dp))
        SummaryRow("Calories Burned", "$totalCalories cal", labelStyle, valueStyle)
    }
}

// kotlin
@Composable
private fun SummaryRow(label: String, value: String, labelStyle: androidx.compose.ui.text.TextStyle, valueStyle: androidx.compose.ui.text.TextStyle) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "•", style = labelStyle, color = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: ", style = labelStyle, color = Color.White)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, style = valueStyle, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SmartfitTheme {
        MainScreen(onLogout = {}, onThemeChange = {}, isDarkTheme = false)
    }
}

fun getIconByName(iconName: String?): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "Restaurant" -> androidx.compose.material.icons.Icons.Filled.Restaurant
        "FitnessCenter" -> androidx.compose.material.icons.Icons.Filled.FitnessCenter
        "Egg" -> androidx.compose.material.icons.Icons.Filled.Egg
        "Balance" -> androidx.compose.material.icons.Icons.Filled.Balance
        "WaterDrop" -> androidx.compose.material.icons.Icons.Filled.WaterDrop
        "Bedtime" -> androidx.compose.material.icons.Icons.Filled.Bedtime
        "TrendingDown" -> androidx.compose.material.icons.Icons.Filled.TrendingDown
        "DirectionsRun" -> androidx.compose.material.icons.Icons.Filled.DirectionsRun
        "NoFood" -> androidx.compose.material.icons.Icons.Filled.NoFood
        "MedicalServices" -> androidx.compose.material.icons.Icons.Filled.MedicalServices
        "Pool" -> androidx.compose.material.icons.Icons.Filled.Pool
        "MenuBook" -> androidx.compose.material.icons.Icons.Filled.MenuBook
        else -> androidx.compose.material.icons.Icons.Filled.Info
    }
}