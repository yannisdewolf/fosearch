package be.dewolf.fosearch.import.junkitems

import be.dewolf.fosearch.model.FoItem
import com.mongodb.MongoClient
import org.bson.types.ObjectId
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component


@ComponentScan("be.dewolf.fosearch.import.junkitems")
@EnableMongoRepositories
class ImportJunkItems {

    @Bean
    fun commandLineRunner(
            importJunkItemService: ImportJunkItemService)
            = CommandLineRunner {
        importJunkItemService.clearCollection()
        importJunkItemService.getItems()
    }

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

    //@Bean
    // fun importJunkItemService(): ImportJunkItemService {
    //    return ImportJunkItemService()
    //}


}

fun main(args: Array<String>) {

    SpringApplicationBuilder(ImportJunkItems::class.java).web(false).run(*args)


}

interface FoItemRepository : CrudRepository<FoItem, ObjectId>

@Component
class ImportJunkItemService(val foItemRepository: FoItemRepository) {

    fun getItems() {

        println("=== START general information ===")

        val document = Jsoup.connect("http://fallout.wikia.com/wiki/Fallout_4_junk_items").get()
        val generalInformationh3 = document.getElementById("General_information_2").parent()

        val nextelement = generalInformationh3.nextElementSibling()

        //println("nextelement: $nextelement")

        var table: Element? = null
        do {
            val select = nextelement.select("table")
            //  println("select: $select")
            table = select.first()
        } while (nextelement != null && table == null)

        val tableContent = if (table != null) getContentRows(table) else listOf()

        tableContent
                .onEach { println(it) }
                .map { toObject(it) }
                .forEach { foItemRepository.save(it)}



        println("=== STOP general information ===")
    }

    private fun toObject(it: Array<String>): FoItem {
        return FoItem(name = it[0] )
    }

    fun getContentRows(table: Element): List<Array<String>> {

        return table
                .getElementsByTag("tr")
                .filter { it.getElementsByTag("td").isNotEmpty() }
                .map { it.children() }
                .filter { it.isNotEmpty() }
                .map { toStringElements(it) }
    }

    private fun toStringElements(tdElements: Elements): Array<String> {

        val name = tdElements[0].text()
        val value = tdElements[1].text()

        val arrayOf = arrayOf(name, value)
        println("convert $tdElements to ${arrayOf[0]}, ${arrayOf[1]}")
        return arrayOf
    }

    fun clearCollection() {
        foItemRepository.deleteAll()
    }


}