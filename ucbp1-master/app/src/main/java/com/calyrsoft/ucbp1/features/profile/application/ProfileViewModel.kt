package com.calyrsoft.ucbp1.features.profile.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calyrsoft.ucbp1.features.profile.domain.model.ProfileModel
import com.calyrsoft.ucbp1.features.profile.domain.usecase.GetProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    val profileUseCase: GetProfileUseCase
): ViewModel() {
    // UI STATE
    sealed class ProfileUiState {
        object Init: ProfileUiState()
        object Loading: ProfileUiState()
        class Error(val message: String): ProfileUiState()
        class Success(val profile: ProfileModel): ProfileUiState()
    }

    // variable mutable y observable
    private var _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Init)
    val state : StateFlow<ProfileUiState> = _state.asStateFlow()

    // evento o eventos desencadenadores
    fun showProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ProfileUiState.Loading
            val resultProfile = profileUseCase.invoke()
            resultProfile.fold(
                onSuccess = {
                   _state.value = ProfileUiState.Success(it)
                },
                onFailure = {
                    _state.value = ProfileUiState.Error(it.message.toString())
                }
            )
        }
    }
}