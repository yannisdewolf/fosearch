package be.dewolf.fosearch.model

import be.dewolf.fosearch.operational.import.RequiredItem
import com.mongodb.MongoClient
import org.bson.types.ObjectId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.repository.CrudRepository

@Document
data class ChemistryCraftingRecipe(
        val name: String,
        val xpGain: String = "0",
        val requiredItems: List<RequiredItem> = listOf(),
        val type: String? = "",
        val addon: Boolean? = false,
        @Id val id: String? = null
)

@Document(collection = "FoItems")
data class FoItem(
        val name: String,
        @Field("breaks_into")
        val breaksInto: List<Ingredient> = listOf(),
        @Field("component_of")
        val componentOf: List<Ingredient> = listOf(),
        val xpGain: Int = 0,
        val type: FoItemType = FoItemType("JUNK"),
        val addon: Boolean = false,
        val sellingValue: Int = 0,
        val buyingValue: Int = 0,
        @Id val id: ObjectId? = null
)

data class FoItemType(
        val main: String,
        val subType: String = ""
)

data class Ingredient(
        val name: String,
        val amount: Int
)

interface FoItemRepository : CrudRepository<FoItem, ObjectId>


@Configuration
class MyMongoConfig {
    @Bean
    fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongoDbFactory())
    }

    fun mongoDbFactory(): MongoDbFactory {
        val fac = SimpleMongoDbFactory(mongoClient(), "test")
        return fac
    }

    fun mongoClient(): MongoClient {
        return MongoClient("localhost")
    }
}