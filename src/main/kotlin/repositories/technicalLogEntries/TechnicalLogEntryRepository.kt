package at.eventful.messless.repositories.technicalLogEntries

import at.eventful.messless.repositories.technicalLogEntries.commands.CreateTechnicalLogEntryCmd
import at.eventful.messless.schema.dao.TechnicalLogEntryDao

interface TechnicalLogEntryRepository {
    fun addTechnicalLogEntry(createTechnicalLogEntryCommand: CreateTechnicalLogEntryCmd): TechnicalLogEntryDao
    fun allTechnicalLogEntries(): List<TechnicalLogEntryDao>
    fun technicalLogEntryById(id: Int): TechnicalLogEntryDao?
    fun removeTechnicalLogEntry(id: Int): TechnicalLogEntryDao?
}