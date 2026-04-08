package com.example.festivalapp.data.festival

import com.example.festivalapp.data.APIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class FestivalRepository(
    private val festivalDao: FestivalDao,
    private val tariffZoneDao: TariffZoneDao,
    private val planZoneDao: PlanZoneDao,
    private val apiService: APIService
) {
    // ========== READ OPERATIONS (Local First) ==========
    
    fun getAllFestivals(): Flow<List<Festival>> {
        return festivalDao.getAllFestivalsWithZones()
            .map { entities ->
                entities.map { it.toFestival() }
            }
            .catch { 
                emit(emptyList())
            }
    }

    fun getFestivalByName(name: String): Flow<Festival?> {
        return festivalDao.getFestivalWithZonesByName(name)
            .map { it?.toFestival() }
            .catch { 
                emit(null)
            }
    }

    fun getCurrentFestival(): Flow<Festival?> {
        return festivalDao.getCurrentFestivalWithZones()
            .map { it?.toFestival() }
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
            
            // 3️⃣ Sauvegarder les zones du plan localement
            festival.planZones.forEach { planZone ->
                planZoneDao.insertPlanZone(planZone.toEntity())
            }
            
            // 4️⃣ En background: envoyer à l'API
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
            
            // 3️⃣ Gérer les zones du plan localement
            planZoneDao.deletePlanZonesByFestival(festival.name)
            festival.planZones.forEach { planZone ->
                planZoneDao.insertPlanZone(planZone.toEntity())
            }
            
            // 4️⃣ En background: envoyer à l'API
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
            planZoneDao.deletePlanZonesByFestival(name)
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
            val localFestival = festivalDao.getFestivalWithZonesByName(name).firstOrNull()?.toFestival()
            
            // 2️⃣ En background: envoyer à l'API
            try {
                val apiResult = apiService.setCurrentFestival(name)
                // 3️⃣ Mettre à jour BD locale
                festivalDao.updateFestival(apiResult.toEntity())
                Result.success(apiResult)
            } catch (apiError: Exception) {
                // Garder données locales
                Result.success(localFestival ?: Festival())
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

    private fun FestivalEntity.toFestival(zones: List<TariffZone>, pZones: List<PlanZone> = emptyList()): Festival {
        return Festival(
            name = name,
            nbSmallTables = nbSmallTables,
            nbLargeTables = nbLargeTables,
            nbCityHallTables = nbCityHallTables,
            isCurrent = isCurrent,
            tariffZones = zones,
            planZones = pZones,
            creationDate = creationDate,
            beginDate = beginDate,
            endDate = endDate
        )
    }

    private fun FestivalWithZones.toFestival(): Festival {
        return festival.toFestival(
            zones.map { it.toTariffZone() },
            planZones.map { it.toPlanZone() }
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

    private fun PlanZone.toEntity(): PlanZoneEntity {
        return PlanZoneEntity(
            id = id,
            name = name,
            nbTables = nbTables,
            festivalName = festivalName,
            tariffZoneId = tariffZoneId
        )
    }

    private fun PlanZoneEntity.toPlanZone(): PlanZone {
        return PlanZone(
            id = id,
            name = name,
            nbTables = nbTables,
            festivalName = festivalName,
            tariffZoneId = tariffZoneId
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
    // ========== SYNC FROM API ==========
    suspend fun syncAllFestivalsFromApi(): List<Festival> {
        return try {
        // Récupère les festivals de l'API
        val festivalsFromApi = apiService.getAllFestivals()
        
        // Sauvegarde chaque festival localement
        festivalsFromApi.forEach { festival ->
            festivalDao.insertFestival(festival.toEntity())
            festival.tariffZones.forEach { zone ->
                tariffZoneDao.insertZone(zone.toEntity())
            }
        }
        
        festivalsFromApi
    } catch (e: Exception) {
        // Si erreur API, retourne ce qui est en local
        getAllFestivals().firstOrNull() ?: emptyList()
    }
}
}
