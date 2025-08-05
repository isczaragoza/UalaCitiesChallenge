package com.isczaragoza.ualacitieschallenge.domain.constants


/**Por buenas prácticas las constantes debería estar separadas por Reglas de Negocio, Logica de Negocio,
 *y los conceptos que abarca el Dominio de tu aplicación. Pero para este caso práctico lo dejarmos aquí
 * en este archivo.*/
const val METADATA_SYNC_CITY_NAME = "city_entity_sync"

const val TWO_MINUTES_IN_MILLISECONDS = 3600000L
const val FIVE_MINUTES_IN_MILLISECONDS = 3600000L
const val HOUR_IN_MILLISECONDS = 3600000L

//CitySyncWorker
const val CITY_SYNC_WORKER_PROGRESS_KEY = "progress"
const val CITY_SYNC_WORKER_SUCCESS_KEY = "success"
const val CITY_SYNC_WORKER_FAILURE_KEY = "failure"