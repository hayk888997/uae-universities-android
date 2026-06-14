package com.d3vly.feature.details

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.d3vly.feature.details.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = checkNotNull(_binding)

    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refreshButton.setOnClickListener {
            viewModel.onIntent(DetailsIntent.RefreshClicked)
        }

        observeViewModel()
        viewModel.onIntent(DetailsIntent.Load(UniversityDetailsArgs.from(arguments)))
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                        ds.color = requireContext().getColor(R.color.tamm_teal)
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
            Toast.makeText(requireContext(), R.string.details_no_browser_found, Toast.LENGTH_SHORT).show()
        }
    }

    private fun closeAndRequestRefresh() {
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY,
            bundleOf(RESULT_REFRESH_REQUESTED to true),
        )
        parentFragmentManager.popBackStack()
    }

    companion object {
        const val REQUEST_KEY = "com.d3vly.feature.details.REQUEST"
        const val RESULT_REFRESH_REQUESTED = "com.d3vly.feature.details.REFRESH_REQUESTED"
        const val TAG = "DetailsFragment"

        fun newInstance(args: UniversityDetailsArgs): DetailsFragment {
            return DetailsFragment().apply {
                arguments = args.toBundle()
            }
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
