package com.example.khatmusalawattime.domain.usecase.counter

import com.example.khatmusalawattime.data.local.entity.CounterEntity
import com.example.khatmusalawattime.domain.repository.CounterRepository
import javax.inject.Inject

class UpdateCounterUseCase @Inject constructor(
    private val repository: CounterRepository
) {
    suspend operator fun invoke(count: Int) {
        val counter = CounterEntity(
            id = 0,
            count = count
        )
        repository.saveCounter(counter)
    }
} 