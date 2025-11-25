package com.example.smartfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartfit.ui.theme.SmartfitTheme
import androidx.compose.foundation.layout.PaddingValues

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartfitTheme {
                MenuScreen()
            }
        }
    }
}

@Composable
fun MenuScreen() {

    var weight by remember { mutableStateOf(TextFieldValue("")) }
    var height by remember { mutableStateOf(TextFieldValue("")) }
    var bmiResult by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // Welcome Text Only
        Text(
            "Welcome,",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF9800),
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(70.dp))

        // Steps Tracker Card
        Card(
            shape = RoundedCornerShape(20.dp),
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
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFFFCF9F9), Color(0xFFF5F5F5))
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {}

            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            // BMI Calculator Card
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = cardColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier.weight(1f)
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Height (m)", fontWeight = FontWeight.Bold, color = Color.White)

                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text(text = "Enter your height in meters") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
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
                                    val bmi = weightF / (heightF * heightF)
                                    bmiResult = String.format("%.2f", bmi)
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

                        Text(
                            "You are, $bmiResult",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,

                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    SmartfitTheme {
        MenuScreen()
    }
}
