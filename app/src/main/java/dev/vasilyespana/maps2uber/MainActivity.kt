package dev.vasilyespana.maps2uber

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.vasilyespana.maps2uber.core.geo.UrlExtractor
import dev.vasilyespana.maps2uber.ui.NavGraph
import dev.vasilyespana.maps2uber.ui.theme.Maps2UberTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cold start with a shared / tapped link goes straight to Resolving.
        // (on rotation the ViewModel keeps the flow state — don't re-trigger.)
        if (savedInstanceState == null) handleIntent(intent)
        setContent {
            Maps2UberTheme {
                NavGraph(viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
                UrlExtractor.firstUrl(text)?.let { viewModel.startWithUrl(it) }
                // No URL in the shared text: stay on Home; the user can paste manually.
            }
            Intent.ACTION_VIEW -> {
                intent.dataString?.let { viewModel.startWithUrl(it) }
            }
        }
    }
}
