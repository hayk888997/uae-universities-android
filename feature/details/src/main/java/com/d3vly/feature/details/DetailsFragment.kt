package com.d3vly.feature.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.d3vly.core.navigation.ListingRefreshResultContract
import com.d3vly.feature.details.databinding.FragmentDetailsBinding
import com.d3vly.feature.details.navigation.UniversityDetailsArgs
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

        configureResponsiveLayout()

        binding.refreshButton.setOnClickListener {
            viewModel.onIntent(DetailsIntent.RefreshListingClicked)
        }

        observeViewModel()
        viewModel.onIntent(DetailsIntent.Load(UniversityDetailsArgs.from(arguments)))
    }

    private fun configureResponsiveLayout() {
        val initialLeft = binding.contentContainer.paddingLeft
        val initialTop = binding.contentContainer.paddingTop
        val initialRight = binding.contentContainer.paddingRight
        val initialBottom = binding.contentContainer.paddingBottom

        fun constrainContentWidth() {
            val rootWidth = binding.root.width
            if (rootWidth == 0) return

            val maxWidth = resources.getDimensionPixelSize(R.dimen.details_content_max_width)
            val targetWidth = minOf(rootWidth, maxWidth)
            val layoutParams = binding.contentContainer.layoutParams
            if (layoutParams.width != targetWidth) {
                layoutParams.width = targetWidth
                binding.contentContainer.layoutParams = layoutParams
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.contentContainer.setPadding(
                initialLeft + systemBars.left,
                initialTop + systemBars.top,
                initialRight + systemBars.right,
                initialBottom + systemBars.bottom,
            )
            constrainContentWidth()
            insets
        }

        binding.root.doOnLayout {
            constrainContentWidth()
            ViewCompat.requestApplyInsets(it)
        }
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
                            DetailsEffect.CloseAndRequestListingRefresh -> closeAndRequestRefresh()
                        }
                    }
                }
            }
        }
    }

    private fun render(state: DetailsState) {
        val hasError = state.errorMessage != null
        binding.errorTextView.isVisible = hasError
        binding.nameTextView.isVisible = !hasError
        binding.countryTextView.isVisible = !hasError
        binding.stateProvinceTextView.isVisible = !hasError
        binding.alphaTwoCodeTextView.isVisible = !hasError
        binding.webPagesTextView.isVisible = !hasError
        binding.domainsTextView.isVisible = !hasError

        if (state.errorMessage != null) {
            binding.errorTextView.text = getString(state.errorMessage.stringRes)
            return
        }

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
        binding.webPagesTextView.text = getString(
            R.string.details_web_pages,
            webPages.joinToString(separator = "\n"),
        )
    }

    private fun closeAndRequestRefresh() {
        val navController = findNavController()
        ListingRefreshResultContract.requestRefresh(
            navController.previousBackStackEntry?.savedStateHandle,
        )
        navController.popBackStack()
    }
}

private val DetailsMessage.stringRes: Int
    get() = when (this) {
        DetailsMessage.MissingUniversity -> R.string.details_error_missing_university
    }
