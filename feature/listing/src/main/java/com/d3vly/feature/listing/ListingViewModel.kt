package com.d3vly.feature.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d3vly.core.domain.model.University
import com.d3vly.core.domain.model.UniversityLoadResult
import com.d3vly.core.domain.model.UniversityLoadSource
import com.d3vly.core.domain.usecase.GetUniversitiesUseCase
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
class ListingViewModel @Inject constructor(
    private val getUniversitiesUseCase: GetUniversitiesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ListingState())
    val state: StateFlow<ListingState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ListingEffect>()
    val effects: SharedFlow<ListingEffect> = _effects.asSharedFlow()

    private var hasRequestedInitialLoad = false
    private var pendingRefresh = false

    fun onIntent(intent: ListingIntent) {
        when (intent) {
            ListingIntent.Load -> loadUniversitiesIfNeeded()
            ListingIntent.Refresh -> refreshUniversities()
            is ListingIntent.UniversityClicked -> openDetails(intent.university)
        }
    }

    private fun loadUniversitiesIfNeeded() {
        if (hasRequestedInitialLoad) return

        hasRequestedInitialLoad = true
        loadUniversities()
    }

    private fun refreshUniversities() {
        hasRequestedInitialLoad = true

        if (_state.value.isLoading) {
            pendingRefresh = true
            return
        }

        loadUniversities()
    }

    private fun loadUniversities() {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageRes = null, warningMessageRes = null) }

            getUniversitiesUseCase()
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            universities = result.universities,
                            errorMessageRes = null,
                            warningMessageRes = result.toWarningMessageRes(),
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = R.string.listing_error_unable_load,
                            warningMessageRes = null,
                        )
                    }
                }
            runPendingRefreshIfNeeded()
        }
    }

    private fun runPendingRefreshIfNeeded() {
        if (pendingRefresh) {
            pendingRefresh = false
            loadUniversities()
        }
    }

    private fun openDetails(university: University) {
        viewModelScope.launch {
            _effects.emit(ListingEffect.OpenDetails(university))
        }
    }

    private fun UniversityLoadResult.toWarningMessageRes(): Int? {
        if (cacheWriteFailed) return R.string.listing_warning_cache_write_failed

        return when (source) {
            UniversityLoadSource.Remote -> null
            UniversityLoadSource.Cache -> R.string.listing_warning_cached_data
        }
    }
}
