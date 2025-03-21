package com.example.khatmusalawattime.domain.usecase.counter

import javax.inject.Inject

data class CounterUseCases @Inject constructor(
    val getCounter: GetCounterUseCase,
    val updateCounter: UpdateCounterUseCase
)