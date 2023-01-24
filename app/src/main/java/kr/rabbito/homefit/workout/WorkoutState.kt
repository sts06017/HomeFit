package kr.rabbito.homefit.workout

class WorkoutState {
    companion object {
        // 앱 상태
        var playing = false
        var rest = false
        var finished = true

        // 현재 운동 진행 상태
        var count = 0
        var min = 0
        var sec = 0
        var calorieBurned = 0
    }
}