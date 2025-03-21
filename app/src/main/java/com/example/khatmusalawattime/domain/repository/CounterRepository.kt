package com.example.khatmusalawattime.domain.repository

import com.example.khatmusalawattime.data.local.entity.CounterEntity

interface CounterRepository {
    suspend fun getCounter(): CounterEntity?
    suspend fun saveCounter(counter: CounterEntity)
}