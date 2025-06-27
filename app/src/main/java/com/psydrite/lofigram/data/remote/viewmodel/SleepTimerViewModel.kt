package com.psydrite.lofigram.data.remote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psydrite.lofigram.utils.MediaPlayerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//very small viewmodel so no dependency injection requiered
class SleepTimerViewModel: ViewModel() {

    //for timer time
    private val _timerSeconds= MutableStateFlow(0)
    val timerSeconds : StateFlow<Int> = _timerSeconds.asStateFlow()

    //for timer modal
    private val _showSleepTimerModal = MutableStateFlow(false)
    val showSleepTimerModal: StateFlow<Boolean> = _showSleepTimerModal.asStateFlow()

    //to know if timer is running or not
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer(min: Int){
        _timerSeconds.value = min * 60
        _isTimerRunning.value = true
        _showSleepTimerModal.value = false

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerSeconds.value > 0 && _isTimerRunning.value) {
                delay(1000L)
                _timerSeconds.value = _timerSeconds.value - 1
            }

            if (_timerSeconds.value == 0 && _isTimerRunning.value) {
                onTimerFinished()
            }
        }
    }

    //logic to stop current timer if running
    fun stopTimer(){
        _isTimerRunning.value=false
        _timerSeconds.value=0
        timerJob?.cancel()
    }

    fun showModal(){
        _showSleepTimerModal.value=true
    }

    fun hideModal(){
        _showSleepTimerModal.value=false


//        if(_isTimerRunning.value){
//            _isTimerRunning.value=false
//            timerJob?.cancel()
//        }
    }

    private fun onTimerFinished(){
        _isTimerRunning.value=false
        _timerSeconds.value=0

        MediaPlayerManager.StopSong()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }


}