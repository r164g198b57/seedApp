package com.example.seedapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.seedapp.ui.theme.SeedAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SeedAppTheme {
                SeedConverterScreen()
            }
        }
    }
}

@Composable
private fun SeedConverterScreen() {
    var input by rememberSaveable { mutableStateOf("") }
    var output by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Канвертар BIP-39",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Пераўтварэнне слоў BIP-39 у чатырохзначныя індэксы і наадварот.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Уваходныя даныя") },
                placeholder = { Text("Увядзіце словы або індэксы") },
                minLines = 4,
                maxLines = 8,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Text
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val raw = input.trim()
                        if (raw.isEmpty()) {
                            output = ""
                            Toast.makeText(context, "Увядзіце словы або індэксы", Toast.LENGTH_SHORT).show()
                        } else {
                            output = convertInput(raw, context)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Пераўтварыць")
                }
                TextButton(
                    onClick = {
                        input = ""
                        output = ""
                    },
                    modifier = Modifier.weight(0.65f)
                ) {
                    Text("Ачысціць")
                }
            }

            OutlinedTextField(
                value = output,
                onValueChange = { output = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Вынік") },
                readOnly = true,
                minLines = 4,
                maxLines = 10,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace)
            )

            Button(
                onClick = {
                    if (output.isBlank()) {
                        Toast.makeText(context, "Няма выніку для капіявання", Toast.LENGTH_SHORT).show()
                    } else {
                        val clip = ClipData.newPlainText("BIP-39", output)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Скапіявана", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Копировать результат")
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Фармат індэксаў: 0001–2048. Калі ўводзіце некалькі слоў, раздзяляйце іх прабеламі або пераносамі радкоў.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun convertInput(raw: String, context: Context): String {
    val normalized = raw.lowercase().trim().split(Regex("\\s+")).joinToString(" ")
    val words = normalized.split(' ')

    // Лічбавы рэжым: кожны індэкс павінен складацца роўна з чатырох лічбаў.
    if (normalized.all { it.isDigit() }) {
        if (normalized.length % 4 != 0) {
            Toast.makeText(context, "Лічбавыя індэксы павінны складацца з груп па 4 лічбы", Toast.LENGTH_LONG).show()
            return ""
        }
        val chunks = normalized.chunked(4)
        val converted = mutableListOf<String>()
        for (chunk in chunks) {
            val index = chunk.toIntOrNull()
            if (index == null || index !in 1..Bip39.array.size) {
                Toast.makeText(context, "Недапушчальны індэкс: $chunk (дапушчальны дыяпазон — 0001–2048)", Toast.LENGTH_LONG).show()
                return ""
            }
            converted += Bip39.array[index - 1]
        }
        return converted.joinToString(" ")
    }

    // Тэкставы рэжым: пошук кожнага слова ў слоўніку.
    val indices = mutableListOf<String>()
    for (word in words) {
        val index = Bip39.array.indexOf(word)
        if (index < 0) {
            Toast.makeText(context, "Слова няма ў слоўніку: $word", Toast.LENGTH_LONG).show()
            return ""
        }
        indices += (index + 1).toString().padStart(4, '0')
    }
    return indices.joinToString("")
}
