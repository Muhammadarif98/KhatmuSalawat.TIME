package com.example.khatmusalawattime.domain.usecase.counter

import com.example.khatmusalawattime.data.local.entity.CounterEntity
import com.example.khatmusalawattime.domain.repository.CounterRepository
import javax.inject.Inject

class GetCounterUseCase @Inject constructor(
    private val repository: CounterRepository
) {
    suspend operator fun invoke(): CounterEntity {
        return repository.getCounter() ?: CounterEntity(
            id = 0,
            count = 0
        )
    }
}