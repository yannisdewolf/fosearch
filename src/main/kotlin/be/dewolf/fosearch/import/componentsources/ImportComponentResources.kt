package be.dewolf.fosearch.import.componentsources

import be.dewolf.fosearch.model.FoItem
import be.dewolf.fosearch.model.FoItemRepository
import be.dewolf.fosearch.model.Ingredient
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.stereotype.Component

fun main(args: Array<String>) {
    SpringApplicationBuilder(ImportComponentResources::class.java).web(false).run(*args)
}

@ComponentScan(basePackages = arrayOf("be.dewolf.fosearch.import.componentsources","be.dewolf.fosearch.model"))
@EnableMongoRepositories(basePackages = arrayOf("be.dewolf.fosearch.model"))
class ImportComponentResources {



    @Bean
    fun commandLineRunner(importComponentResourcesService: ImportComponentResourcesService) = CommandLineRunner {
        importComponentResourcesService.getItems()
    }
}

@Component
class ImportComponentResourcesService(val foItemRepository: FoItemRepository) {
    fun getItems() {

        val document = Jsoup.connect("http://fallout.wikia.com/wiki/Fallout_4_junk_items").get()
        val componentResourcesh3 = document.getElementById("Component_sources").parent()

        val table = componentResourcesh3.nextElementSibling()



        getContentRows(table).forEach { foItemRepository.save(it) }

    }

    fun getContentRows(table: Element): List<FoItem> {

        return table
                .getElementsByTag("tr")
                .filter { it.getElementsByTag("td").isNotEmpty() }
                .map { it.children() }
                .filter { it.isNotEmpty() }
                .map { toNameSourceItem(it) }
                .flatten()
                .groupBy { it.sourceItem }
                .mapValues { it.value.map { Ingredient(it.name, 0) } }
                .map { FoItem(it.key, it.value) }


    }

    private fun toNameSourceItem(tdElements: Elements): List<NameSourceItem> {

        val baseComponent = tdElements[0].text()
        val value = tdElements[1].text()

        val elements = value.split(",").map { it.trim() }

        return elements.map { NameSourceItem(baseComponent, it) }
    }


}

data class NameSourceItem(val name: String, val sourceItem: String)