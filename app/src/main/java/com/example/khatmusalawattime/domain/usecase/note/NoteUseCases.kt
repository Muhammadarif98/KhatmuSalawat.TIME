package com.example.khatmusalawattime.domain.usecase.note

import javax.inject.Inject

/**
 * Container for all note-related use cases
 */
data class NoteUseCases @Inject constructor(
    val getAllNoteLists: GetAllNoteListsUseCase,
    val getNotesByList: GetNotesByListUseCase,
    val addNoteList: AddNoteListUseCase,
    val addNote: AddNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val deleteNote: DeleteNoteUseCase,
    val toggleNoteCompletion: ToggleNoteCompletionUseCase
)