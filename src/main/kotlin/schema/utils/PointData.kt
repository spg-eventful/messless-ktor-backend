/*
 * Copyright (c) 2024 propertium.io
 * Licensed under the MIT License.
 * For further details please refer to LICENSE-EXPOSED-POSTGIS.txt
 */

package at.eventful.messless.schema.utils

data class PointData(val x: Double, val y: Double, val srid: Int = 4326)