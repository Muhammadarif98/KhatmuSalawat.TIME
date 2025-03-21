package com.example.khatmusalawattime.data.repository

import com.example.khatmusalawattime.data.local.dao.CounterDao
import com.example.khatmusalawattime.data.local.entity.CounterEntity
import com.example.khatmusalawattime.domain.repository.CounterRepository
import javax.inject.Inject

class CounterRepositoryImpl @Inject constructor(
    private val counterDao: CounterDao
) : CounterRepository {
    override suspend fun getCounter(): CounterEntity? {
        return counterDao.getCounter()
    }

    override suspend fun saveCounter(counter: CounterEntity) {
        counterDao.saveCounter(counter)
    }
} 