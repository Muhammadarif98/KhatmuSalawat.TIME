package com.example.khatmusalawattime.domain.usecase.counter

import javax.inject.Inject

data class CounterUseCases @Inject constructor(
    val getCounter: GetCounterUseCase,
    val updateCounter: UpdateCounterUseCase,
    val recordSession: RecordCounterSessionUseCase,
    val getStats: GetCounterStatsUseCase,
    val createGoal: CreateCounterGoalUseCase,
    val updateGoalProgress: UpdateGoalProgressUseCase,
    val getActiveGoals: GetActiveGoalsUseCase,
    val deleteGoal: DeleteCounterGoalUseCase,
    val resetGoalProgress: ResetGoalProgressUseCase
)