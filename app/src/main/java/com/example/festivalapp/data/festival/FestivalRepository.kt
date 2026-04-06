package com.example.festivalapp.data.festival

import com.example.festivalapp.data.APIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class FestivalRepository(
    private val festivalDao: FestivalDao,
    private val tariffZoneDao: TariffZoneDao,
    private val apiService: APIService
) {
    // ========== READ OPERATIONS (Local First) ==========
    
    fun getAllFestivals(): Flow<List<Festival>> {
        return festivalDao.getAllFestivals()
            .map { entities ->
                entities.map { entity ->
                    entity.toFestival(
                        tariffZoneDao.getZonesByFestival(entity.name).map { it.toTariffZone() }
                    )
                }
            }
            .catch { 
                emit(emptyList())
            }
    }

    fun getFestivalByName(name: String): Flow<Festival?> {
        return festivalDao.getFestivalByName(name)
            .map { entity ->
                entity?.let {
                    it.toFestival(
                        tariffZoneDao.getZonesByFestival(it.name).map { zone -> zone.toTariffZone() }
                    )
                }
            }
            .catch { 
                emit(null)
            }
    }

    fun getCurrentFestival(): Flow<Festival?> {
        return festivalDao.getCurrentFestival()
            .map { entity ->
                entity?.let {
                    it.toFestival(
                        tariffZoneDao.getZonesByFestival(it.name).map { zone -> zone.toTariffZone() }
                    )
                }
            }
            .catch { 
                emit(null)
            }
    }

    // ========== WRITE OPERATIONS (Local First, then API) ==========

    suspend fun createFestival(festival: Festival): Result<Festival> {
        return try {
            // 1️⃣ Sauvegarder localement IMMÉDIATEMENT
            val entity = festival.toEntity()
            festivalDao.insertFestival(entity)
            
            // 2️⃣ Sauvegarder les zones localement
            festival.tariffZones.forEach { zone ->
                tariffZoneDao.insertZone(zone.toEntity())
            }
            
            // 3️⃣ En background: envoyer à l'API
            try {
                val apiResult = apiService.createFestival(festival)
                // 4️⃣ Si succès API: mettre à jour BD locale avec réponse
                festivalDao.insertFestival(apiResult.toEntity())
                apiResult.tariffZones.forEach { zone ->
                    tariffZoneDao.insertZone(zone.toEntity())
                }
                Result.success(apiResult)
            } catch (apiError: Exception) {
                // 5️⃣ Si erreur API: garder les données locales
                Result.success(festival)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFestival(originalName: String, festival: Festival): Result<Festival> {
        return try {
            // 1️⃣ Mettre à jour localement IMMÉDIATEMENT
            val entity = festival.toEntity()
            festivalDao.updateFestival(entity)
            
            // 2️⃣ Supprimer anciennes zones et en insérer de nouvelles
            tariffZoneDao.deleteZonesByFestival(festival.name)
            festival.tariffZones.forEach { zone ->
                tariffZoneDao.insertZone(zone.toEntity())
            }
            
            // 3️⃣ En background: envoyer à l'API
            try {
                val apiResult = apiService.updateFestivalByName(originalName, festival)
                // 4️⃣ Si succès API: mettre à jour BD locale
                festivalDao.updateFestival(apiResult.toEntity())
                tariffZoneDao.deleteZonesByFestival(apiResult.name)
                apiResult.tariffZones.forEach { zone ->
                    tariffZoneDao.insertZone(zone.toEntity())
                }
                Result.success(apiResult)
            } catch (apiError: Exception) {
                // 5️⃣ Si erreur API: garder les données locales
                Result.success(festival)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFestival(name: String): Result<Unit> {
        return try {
            // 1️⃣ Supprimer localement IMMÉDIATEMENT
            tariffZoneDao.deleteZonesByFestival(name)
            festivalDao.deleteFestivalByName(name)
            
            // 2️⃣ En background: envoyer suppression à l'API
            try {
                apiService.deleteFestival(name)
                Result.success(Unit)
            } catch (apiError: Exception) {
                // 5️⃣ Si erreur API: données locales déjà supprimées
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setCurrentFestival(name: String): Result<Festival> {
        return try {
            // 1️⃣ Chercher le festival en local
            val festival = festivalDao.getFestivalByName(name).map { entity ->
                entity?.let {
                    it.toFestival(
                        tariffZoneDao.getZonesByFestival(it.name).map { zone -> zone.toTariffZone() }
                    )
                }
            }
            
            // 2️⃣ En background: envoyer à l'API
            try {
                val apiResult = apiService.setCurrentFestival(name)
                // 3️⃣ Mettre à jour BD locale
                festivalDao.updateFestival(apiResult.toEntity())
                Result.success(apiResult)
            } catch (apiError: Exception) {
                // Garder données locales
                Result.success(festival.value ?: Festival())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== HELPER CONVERSION FUNCTIONS ==========

    private fun Festival.toEntity(): FestivalEntity {
        return FestivalEntity(
            name = name,
            nbSmallTables = nbSmallTables,
            nbLargeTables = nbLargeTables,
            nbCityHallTables = nbCityHallTables,
            beginDate = beginDate,
            endDate = endDate,
            isCurrent = isCurrent,
            creationDate = creationDate
        )
    }

    private fun FestivalEntity.toFestival(zones: List<TariffZone>): Festival {
        return Festival(
            name = name,
            nbSmallTables = nbSmallTables,
            nbLargeTables = nbLargeTables,
            nbCityHallTables = nbCityHallTables,
            isCurrent = isCurrent,
            tariffZones = zones,
            creationDate = creationDate,
            beginDate = beginDate,
            endDate = endDate
        )
    }

    private fun TariffZone.toEntity(): TariffZoneEntity {
        return TariffZoneEntity(
            idTZ = idTZ,
            name = name,
            festivalName = festivalName,
            nbSmallTables = nbSmallTables,
            nbLargeTables = nbLargeTables,
            nbCityHallTables = nbCityHallTables,
            smallTablePrice = smallTablePrice,
            largeTablePrice = largeTablePrice,
            cityHallTablePrice = cityHallTablePrice,
            squareMeterPrice = squareMeterPrice
        )
    }

    private fun TariffZoneEntity.toTariffZone(): TariffZone {
        return TariffZone(
            idTZ = idTZ,
            name = name,
            festivalName = festivalName,
            nbSmallTables = nbSmallTables,
            nbLargeTables = nbLargeTables,
            nbCityHallTables = nbCityHallTables,
            smallTablePrice = smallTablePrice,
            largeTablePrice = largeTablePrice,
            cityHallTablePrice = cityHallTablePrice,
            squareMeterPrice = squareMeterPrice
        )
    }
}