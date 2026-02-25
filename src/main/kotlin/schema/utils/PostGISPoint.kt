package at.eventful.messless.schema.utils

import org.jetbrains.exposed.v1.core.ColumnType

class PostGISPoint: ColumnType<Point>() {
    override fun sqlType(): String = "GEOMETRY(Point, 4326)"

    override fun valueFromDB(value: Any): Point? {
        return when (value) {
            is String -> {
                val coords = value.removePrefix("POINT(").removeSuffix(")").split(" ")
                Point(coords[0].toDouble(), coords[1].toDouble())
            }
            is Point -> value
            else -> null
        }
    }

    override fun notNullValueToDB(value: Point): Any {
        return "ST_GeomFromText('POINT(${value.longitude} ${value.latitude})', 4326)"
    }
}

fun org.jetbrains.exposed.v1.core.Table.point(name: String) = registerColumn<Point>(name, PostGISPoint())