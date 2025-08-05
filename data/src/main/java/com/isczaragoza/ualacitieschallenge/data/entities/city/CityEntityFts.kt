package com.isczaragoza.ualacitieschallenge.data.entities.city

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey


/**Tabla FTS para indexado y consultas optimizadas que se sincroniza
 *  con los datos de la Entidad principal.
 * La notación @Fts4 crea los triggers que mantienen sincronizados los
 * datos de la tabla CityEntity con esta Fts, ayudando a mantener
 * una busqueda optimizada.
 * */
//@Fts4(contentEntity = CityEntity::class)
@Entity(tableName = "CityEntityFts")
data class CityEntityFts(
    @PrimaryKey
    val name: String
)
