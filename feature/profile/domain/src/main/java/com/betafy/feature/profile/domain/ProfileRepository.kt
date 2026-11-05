package com.betafy.feature.profile.domain

import com.betafy.feature.profile.domain.model.FeedbackType
import com.betafy.feature.profile.domain.model.Genre
import com.betafy.feature.profile.domain.model.ReaderProfile
import com.betafy.feature.profile.domain.model.WriterProfile

interface ProfileRepository {
    suspend fun getReaderProfile(userId: String): Result<ReaderProfile?>
    suspend fun getWriterProfile(userId: String): Result<WriterProfile?>
    suspend fun saveReaderProfile(profile: ReaderProfile): Result<Unit>
    suspend fun saveWriterProfile(profile: WriterProfile): Result<Unit>
    suspend fun getGenres(): Result<List<Genre>>
    suspend fun getFeedbackTypes(): Result<List<FeedbackType>>
}
