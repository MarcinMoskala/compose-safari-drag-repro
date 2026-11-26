import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport

/**
 * A scrollable list of cards. In Safari 27 (iOS and macOS), dragging it slowly with touch makes the cards render on
 * top of each other at the top-left corner of the canvas, with the rest of the canvas empty. See README.md.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "app") {
        MaterialTheme {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF4EFEA)),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
            ) {
                item {
                    Column {
                        Text("Your practice")
                        Text("A list of cards to drag slowly with a finger.")
                    }
                }
                items(40) { index ->
                    Column(Modifier.background(Color.White)) {
                        Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color(0xFFE9C9B4)))
                        Column(Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                            Text("25 MIN · 12 EXERCISES")
                            Text("Card number $index")
                            Text("Some description text so that each card has a few lines of content.")
                            Text("BEGINNER · HIPS · BACK")
                        }
                    }
                }
            }
        }
    }
}
