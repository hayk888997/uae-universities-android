package com.d3vly.feature.listing

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.d3vly.core.designsystem.theme.D3vlyTestAppTheme
import com.d3vly.core.domain.model.University
import com.d3vly.feature.listing.navigation.SelectedUniversityArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListingFragment : Fragment() {
    private var refreshSignal by mutableIntStateOf(0)

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
                        refreshSignal = refreshSignal,
                        onUniversitySelected = { university ->
                            publishUniversitySelected(university)
                        },
                    )
                }
            }
        }
    }

    fun refresh() {
        refreshSignal += 1
    }

    private fun publishUniversitySelected(university: University) {
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY,
            Bundle().apply {
                putParcelable(RESULT_SELECTED_UNIVERSITY_ARGS, SelectedUniversityArgs.from(university))
            },
        )
    }

    companion object {
        const val REQUEST_KEY = "com.d3vly.feature.listing.REQUEST"
        private const val RESULT_SELECTED_UNIVERSITY_ARGS = "com.d3vly.feature.listing.SELECTED_UNIVERSITY_ARGS"

        fun getSelectedUniversity(bundle: Bundle): University? {
            return bundle.getSelectedUniversityArgs()?.toUniversity()
        }

        @Suppress("DEPRECATION")
        private fun Bundle.getSelectedUniversityArgs(): SelectedUniversityArgs? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getParcelable(RESULT_SELECTED_UNIVERSITY_ARGS, SelectedUniversityArgs::class.java)
            } else {
                getParcelable(RESULT_SELECTED_UNIVERSITY_ARGS)
            }
        }
    }
}
