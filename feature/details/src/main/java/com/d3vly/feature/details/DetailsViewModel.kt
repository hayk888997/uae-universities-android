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
            DetailsIntent.RefreshListingClicked -> closeAndRequestListingRefresh()
        }
    }

    private fun showUniversity(args: UniversityDetailsArgs?) {
        if (args == null) {
            _state.value = DetailsState(errorMessage = DetailsMessage.MissingUniversity)
            return
        }

        _state.value = DetailsState(
            name = args.name,
            country = args.country,
            alphaTwoCode = args.alphaTwoCode,
            stateProvince = args.stateProvince,
            webPages = args.webPages,
            domains = args.domains,
            errorMessage = null,
        )
    }

    private fun closeAndRequestListingRefresh() {
        viewModelScope.launch {
            _effects.emit(DetailsEffect.CloseAndRequestListingRefresh)
        }
    }
}
