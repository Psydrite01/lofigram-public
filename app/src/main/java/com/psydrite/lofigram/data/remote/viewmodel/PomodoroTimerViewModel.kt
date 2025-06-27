package com.psydrite.lofigram.data.remote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psydrite.lofigram.utils.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PomodoroState {
    WORK, SHORT_BREAK, LONG_BREAK, STOPPED
}

data class PomodoroSettings(
    val workMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val sessionsUntilLongBreak: Int = 4,
    val testMinutes: Int = 1,
    val testBreakMinutes: Int = 1
)

//small viewmodel, no dependency injection required
class PomodoroTimerViewModel(
    private val notificationHelper: NotificationHelper? = null
): ViewModel() {

    //variable for timer state
    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _currentState = MutableStateFlow(PomodoroState.STOPPED)
    val currentState: StateFlow<PomodoroState> = _currentState.asStateFlow()

    //variable for modal state
    private val _showPomodoroModal = MutableStateFlow(false)
    val showPomodoroModal: StateFlow<Boolean> = _showPomodoroModal.asStateFlow()

    //session tracking
    private val _completedSessions = MutableStateFlow(0)
    val completedSessions: StateFlow<Int> = _completedSessions.asStateFlow()


    private val _settings = MutableStateFlow(PomodoroSettings())
    val settings: StateFlow<PomodoroSettings> = _settings.asStateFlow()

    private val _totalSeconds = MutableStateFlow(0)
    val totalSeconds: StateFlow<Int> = _totalSeconds.asStateFlow()

    private var timerJob: Job? = null

    fun startWorkSession() {
        startTimer(PomodoroState.WORK, _settings.value.workMinutes)
    }

    fun startShortBreak() {
        startTimer(PomodoroState.SHORT_BREAK, _settings.value.shortBreakMinutes)
    }

    fun startLongBreak() {
        startTimer(PomodoroState.LONG_BREAK, _settings.value.longBreakMinutes)
    }


    private fun startTimer(state: PomodoroState, minutes: Int) {
        val seconds = minutes * 60
        _timerSeconds.value = seconds
        _totalSeconds.value = seconds
        _currentState.value = state
        _isTimerRunning.value = true
        _showPomodoroModal.value = false

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

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resumeTimer() {
        if (_timerSeconds.value > 0) {
            _isTimerRunning.value = true
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
    }

    fun stopTimer() {
        _isTimerRunning.value = false
        _timerSeconds.value = 0
        _totalSeconds.value = 0
        _currentState.value = PomodoroState.STOPPED
        timerJob?.cancel()
    }

    fun showModal() {
        _showPomodoroModal.value = true
    }

    fun hideModal() {
        _showPomodoroModal.value = false
    }

    private fun onTimerFinished() {

        val currentState= _currentState.value

        notificationHelper?.sendPomodoroFinishedNotification(currentState)


        when (currentState) {
            PomodoroState.WORK -> {
                _completedSessions.value = _completedSessions.value + 1
                //autostart break based on completed sessions
                if (_completedSessions.value % _settings.value.sessionsUntilLongBreak == 0) {
                    startLongBreak()
                } else {
                    startShortBreak()
                }
            }
            PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK -> {
                //breakdone
                _currentState.value = PomodoroState.STOPPED
                _isTimerRunning.value = false
                _timerSeconds.value = 0
                _totalSeconds.value = 0
            }
            PomodoroState.STOPPED -> {
                //should not happen, as timer over
            }
        }
    }

    fun updateSettings(newSettings: PomodoroSettings) {
        _settings.value = newSettings
    }

    fun resetSessions() {
        _completedSessions.value = 0
    }

    fun getProgress(): Float {
        return if (_totalSeconds.value > 0) {
            1f - (_timerSeconds.value.toFloat() / _totalSeconds.value.toFloat())
        } else {
            0f
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}