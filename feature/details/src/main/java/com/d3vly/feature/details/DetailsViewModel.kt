package com.d3vly.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d3vly.feature.details.navigation.UniversityDetailsArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DetailsEffect>()
    val effects: SharedFlow<DetailsEffect> = _effects.asSharedFlow()

    fun onIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.Load -> showUniversity(intent.args)
            DetailsIntent.RefreshClicked -> closeAndRefreshListing()
        }
    }

    private fun showUniversity(args: UniversityDetailsArgs?) {
        if (args == null) {
            _state.update {
                DetailsState(errorMessageRes = R.string.details_error_missing_university)
            }
            return
        }

        _state.update {
            DetailsState(
                name = args.name,
                country = args.country,
                alphaTwoCode = args.alphaTwoCode,
                stateProvince = args.stateProvince,
                webPages = args.webPages,
                domains = args.domains,
                errorMessageRes = null,
            )
        }
    }

    private fun closeAndRefreshListing() {
        viewModelScope.launch {
            _effects.emit(DetailsEffect.CloseAndRefreshListing)
        }
    }
}
