package com.d3vly.feature.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.d3vly.core.designsystem.theme.D3vlyTestAppTheme
import com.d3vly.feature.listing.navigation.ListingNavigationContract
import com.d3vly.feature.listing.navigation.SelectedUniversityArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListingFragment : Fragment() {
    private val viewModel: ListingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                D3vlyTestAppTheme {
                    ListingScreen(
                        viewModel = viewModel,
                        onUniversitySelected = { university ->
                            publishUniversitySelected(university)
                        },
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>(ListingNavigationContract.REFRESH_REQUESTED_KEY)
            ?.observe(viewLifecycleOwner) { shouldRefresh ->
                if (shouldRefresh) {
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<Boolean>(
                        ListingNavigationContract.REFRESH_REQUESTED_KEY,
                    )
                    refresh()
                }
            }
    }

    fun refresh() {
        viewModel.onIntent(ListingIntent.Refresh)
    }

    private fun publishUniversitySelected(university: SelectedUniversityArgs) {
        ListingNavigationContract.publishSelectedUniversity(
            fragmentManager = parentFragmentManager,
            university = university,
        )
    }
}
