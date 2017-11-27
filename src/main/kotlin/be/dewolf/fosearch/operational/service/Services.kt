package be.dewolf.fosearch.operational.service

import be.dewolf.fosearch.operational.import.ChemistryCraftingRecipe
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

@Component
class CraftablesService(
        val mongoOperations: MongoOperations,
        val chemistryCraftingRecipeRepository: ChemistryCraftingRecipeRepository) {

    fun updateRecipeWithStatus(id: String, newType: String) {

        val forId = Query().addCriteria(Criteria.where("id").`is`(id))

        val update = Update()
        update.set("type", newType)

        mongoOperations.updateFirst(forId, update,  ChemistryCraftingRecipe::class.java)

    }

    fun getByType(type: String): List<ChemistryCraftingRecipe> = chemistryCraftingRecipeRepository.getRecipesByType(type)
    fun allTypes(): List<String> = mongoOperations.getCollection("chemistryCraftingRecipe").distinct("type") as List<String>
    fun allNonAddons() = chemistryCraftingRecipeRepository.getNonAddonRecipes()
    fun markAsAddon(id: String)  {
        val forId = Query().addCriteria(Criteria.where("id").`is`(id))
        val update = Update()
        update.set("addon", true)
        mongoOperations.updateFirst(forId, update, ChemistryCraftingRecipe::class.java)
    }

    fun all() = chemistryCraftingRecipeRepository.findAll()

}

interface ChemistryCraftingRecipeRepository : CrudRepository<ChemistryCraftingRecipe, String>,
        ChemistryCraftingRecipeRepositoryCustom {
    fun getRecipesByType(type: String): List<ChemistryCraftingRecipe>

}

interface ChemistryCraftingRecipeRepositoryCustom {

    fun getNonAddonRecipes(): List<ChemistryCraftingRecipe>

}

//@Component
class ChemistryCraftingRecipeRepositoryImpl(val mongoOperations: MongoOperations): ChemistryCraftingRecipeRepositoryCustom {
    override fun getNonAddonRecipes(): List<ChemistryCraftingRecipe> {

        val orOperator = Criteria().orOperator(Criteria.where("addon").exists(false), Criteria.where("addon").`is`(false));

        val query= Query(orOperator)

        return mongoOperations.find(query, ChemistryCraftingRecipe::class.java)
    }

}

