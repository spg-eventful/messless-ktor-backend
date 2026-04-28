package at.eventful.messless.repositories.technicalLogEntries

import at.eventful.messless.repositories.technicalLogEntries.commands.CreateTechnicalLogEntryCmd
import at.eventful.messless.schema.dao.TechnicalLogEntryDao

interface TechnicalLogEntryRepository {
    fun addTechnicalLogEntry(createTechnicalLogEntryCommand: CreateTechnicalLogEntryCmd, userId: Int): TechnicalLogEntryDao
    fun allTechnicalLogEntries(): List<TechnicalLogEntryDao>
    fun removeTechnicalLogEntry(id: Int): TechnicalLogEntryDao?
    fun technicalLogEntryById(id: Int): TechnicalLogEntryDao?
}