package kr.rabbito.homefit.workout

class WorkoutState {
    /*
    구상: count_change와 같은 역할 하도록 구현, 추후에 LiveData 등을 이용해 실시간으로 적용되도록 변경할 필요 있음
     */

    companion object {
        // 앱 상태
        var playing = false
        var rest = false
        var finished = true

        // 현재 운동 정보
        var setCondition = 15 // 몇 개가 한 세트인지
        var setTotal = 3 // 총 몇 세트인지
        var restTotal = 3 // 총 몇번 휴식인지

        // 현재 운동 플래그
        var isUp = false

        // 현재 운동 진행 상태
        var set = 1 // 몇 번째 세트인지
        var count = 0   // 몇 개 했는지
        var elapSec = 0    // 경과 시간
        var remainSec = 120  // 남은 시간
        var calorieBurned = 0   // 소모한 칼로리

        // 현재 자세 진행 상태
        var isPrevUp = false
    }
}