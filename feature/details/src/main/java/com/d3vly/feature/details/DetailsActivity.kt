package com.d3vly.feature.details

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.d3vly.feature.details.databinding.ActivityDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsActivity : ComponentActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.refreshButton.setOnClickListener {
            viewModel.onIntent(DetailsIntent.RefreshClicked)
        }

        observeViewModel()
        viewModel.onIntent(DetailsIntent.Load(UniversityDetailsArgs.from(intent)))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        render(state)
                    }
                }
                launch {
                    viewModel.effects.collect { effect ->
                        when (effect) {
                            DetailsEffect.CloseAndRefreshListing -> closeAndRequestRefresh()
                        }
                    }
                }
            }
        }
    }

    private fun render(state: DetailsState) {
        binding.nameTextView.text = state.name
        binding.countryTextView.text = getString(R.string.details_country, state.country)
        binding.stateProvinceTextView.text = getString(
            R.string.details_state_province,
            state.stateProvince ?: getString(R.string.details_state_province_not_available),
        )
        binding.alphaTwoCodeTextView.text = getString(R.string.details_alpha_two_code, state.alphaTwoCode)
        renderWebPages(state.webPages)
        binding.domainsTextView.text = getString(
            R.string.details_domains,
            state.domains.joinToString(separator = "\n"),
        )
    }

    private fun renderWebPages(webPages: List<String>) {
        val text = SpannableStringBuilder(getString(R.string.details_web_pages))
        webPages.forEach { webPage ->
            val start = text.length + 1
            text.append('\n').append(webPage)
            text.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        openBrowser(webPage)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = getColor(R.color.tamm_teal)
                    }
                },
                start,
                start + webPage.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }

        binding.webPagesTextView.text = text
        binding.webPagesTextView.movementMethod = LinkMovementMethod.getInstance()
        binding.webPagesTextView.highlightColor = Color.TRANSPARENT
    }

    private fun openBrowser(webPage: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webPage.toBrowserUrl()))
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.details_no_browser_found, Toast.LENGTH_SHORT).show()
        }
    }

    private fun closeAndRequestRefresh() {
        setResult(
            Activity.RESULT_OK,
            Intent().putExtra(EXTRA_REFRESH_REQUESTED, true),
        )
        finish()
    }

    companion object {
        private const val EXTRA_REFRESH_REQUESTED = "com.d3vly.feature.details.REFRESH_REQUESTED"

        fun createIntent(
            context: Context,
            args: UniversityDetailsArgs,
        ): Intent {
            return args.putInto(Intent(context, DetailsActivity::class.java))
        }

        fun isRefreshRequested(data: Intent?): Boolean {
            return data?.getBooleanExtra(EXTRA_REFRESH_REQUESTED, false) == true
        }
    }
}

private fun String.toBrowserUrl(): String {
    return if (startsWith("http://") || startsWith("https://")) {
        this
    } else {
        "https://$this"
    }
}
