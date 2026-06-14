package com.d3vly.feature.listing

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.d3vly.core.domain.model.University
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListingFragment : Fragment() {
    private var listener: Listener? = null
    private var refreshSignal by mutableIntStateOf(0)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    ListingScreen(
                        refreshSignal = refreshSignal,
                        onUniversitySelected = { university ->
                            listener?.onUniversitySelected(university)
                        },
                    )
                }
            }
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    fun refresh() {
        refreshSignal += 1
    }

    interface Listener {
        fun onUniversitySelected(university: University)
    }

    companion object {
        const val TAG = "ListingFragment"

        fun newInstance(): ListingFragment = ListingFragment()
    }
}
