package at.eventful.messless.repositories

import at.eventful.messless.schema.entities.WarehouseEntity
import at.eventful.messless.schema.tables.CompanyTable
import at.eventful.messless.schema.tables.WarehouseTable
import at.eventful.messless.services.warehouse.Warehouse
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class WarehouseRepository {
    suspend fun create(label: String, location: net.postgis.jdbc.geometry.Point): Int = dbQuery {
        WarehouseTable.insert {
            it[this.label] = label
            it[this.location] = location
        }[WarehouseTable.id].value
    }

    @OptIn(ExperimentalTime::class)
    suspend fun findById(id: Int): Warehouse? {
        return dbQuery {
            val query = (WarehouseTable innerJoin CompanyTable)
                .selectAll()
                .where { WarehouseTable.id eq id and WarehouseTable.deletedAt.isNull() }

            val rows = query.toList()
            if (rows.isEmpty()) return@dbQuery null
            val firstRow = rows.first()
            val locationX = firstRow[WarehouseTable.location].x
            val locationY = firstRow[WarehouseTable.location].y

            Warehouse(
                firstRow[WarehouseTable.id].value,
                firstRow[WarehouseTable.label],
                locationX,
                locationY,
                firstRow[CompanyTable.label]
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun findAll(): List<Warehouse>{
        return dbQuery {
            (WarehouseTable innerJoin CompanyTable)
                .selectAll()
                .where { WarehouseTable.deletedAt.isNull() }
                .map { row ->
                    val locationX = row[WarehouseTable.location].x
                    val locationY = row[WarehouseTable.location].y
                    Warehouse(
                        row[WarehouseTable.id].value,
                        row[WarehouseTable.label],
                        locationX,
                        locationY,
                        row[CompanyTable.label]
                    )
                }
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun delete(id: Int) = dbQuery {
        WarehouseEntity.findById(id)?.apply {
            this.deletedAt = Clock.System.now()
            this.flush()
        }
    }

    suspend fun update(id: Int, label: String, location: net.postgis.jdbc.geometry.Point) = dbQuery {
        WarehouseEntity.findById(id)?.apply {
            this.label = label
            this.location = location
            this.flush()
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T = suspendTransaction { block() }

}