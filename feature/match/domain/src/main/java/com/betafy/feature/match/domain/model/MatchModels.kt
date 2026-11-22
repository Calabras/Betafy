package com.betafy.feature.match.domain.model

data class ReaderCard(
    val userId: String,
    val experienceLevel: String,
    val preferredLanguage: String,
    val maxWordCount: Int,
    val isAvailable: Boolean,
    val cooperationType: String,
    val genreIds: List<String>,
    val feedbackTypeIds: List<String>
)

data class WorkCard(
    val id: String,
    val writerId: String,
    val title: String,
    val wordCount: Int,
    val language: String,
    val genreIds: List<String>
)

data class RequestCard(
    val id: String,
    val workId: String,
    val cooperationType: String,
    val status: String,
    val deadline: String,
    val feedbackTypeIds: List<String>
)

data class StoredMatch(
    val id: String,
    val requestId: String,
    val readerId: String,
    val status: String
)

data class MatchCatalog(
    val readers: List<ReaderCard>,
    val works: List<WorkCard>,
    val requests: List<RequestCard>,
    val matches: List<StoredMatch>,
    val names: Map<String, String>,
    val genreNames: Map<String, String>,
    val feedbackNames: Map<String, String>
)

data class MatchLine(
    val requestId: String,
    val readerId: String,
    val title: String,
    val counterpart: String,
    val score: Int,
    val caption: String,
    val writerSide: Boolean
) {
    val key: String = "$requestId|$readerId"
}

data class MatchBoard(
    val hasReaderProfile: Boolean,
    val hasOpenRequests: Boolean,
    val writerLines: List<MatchLine>,
    val readerLines: List<MatchLine>
)

data class NewMatch(
    val requestId: String,
    val readerId: String,
    val initiatorId: String,
    val score: Double,
    val createdAt: Long
)
