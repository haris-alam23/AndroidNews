package com.example.androidnews

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.androidnews.ui.theme.AndroidNewsTheme
import com.example.androidnews.ui.theme.MainSearchActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidNewsTheme {
                LogInApp()
            }
        }
    }
}

@Composable
fun LogInApp() {


    var password by remember { mutableStateOf("")}
    val context = LocalContext.current


    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)}
    var username by remember { mutableStateOf(prefs.getString("username","") ?: "Enter your username")}


    val validUsername = username.length  >= 5 && !username.contains(" ")
    val validPassword = password.length  >= 8 && !password.contains(" ")
    val fullyValid = validUsername && validPassword
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(Modifier.height(70.dp))
        Image(painter = painterResource(R.drawable.newsimage),
                contentDescription = null)


        Spacer(Modifier.height(70.dp))
        Text(text = "Welcome!")
        Spacer(Modifier.height(70.dp))


        OutlinedTextField(
            value = username,
            onValueChange = {newText -> username = newText},

            placeholder = {Text("enter username")},
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = {newText ->password = newText},
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()

        )

        Button(onClick = {
            prefs.edit{ putString("username", username)}
            val intent = Intent(context, MainSearchActivity::class.java)
            context.startActivity(intent)

        },
            enabled = fullyValid)
        {Text("Login")}
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidNewsTheme {
        LogInApp()
    }
}