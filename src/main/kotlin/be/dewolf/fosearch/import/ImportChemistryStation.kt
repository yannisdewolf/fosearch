package be.dewolf.fosearch.import

import be.dewolf.fosearch.operational.import.ChemistryCraftingRecipe
import be.dewolf.fosearch.operational.import.description
import be.dewolf.fosearch.operational.import.toRequiredItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Component

@SpringBootApplication
class ImportChemistryStation {

    @Bean
    fun commandLineRunner(
            falloutService: ConsumeFalloutService,
            mongoOperations: MongoOperations
    ) = CommandLineRunner {
        val consumeChemistry = falloutService.consumeChemistry("http://fallout.wikia.com/wiki/Chemistry_station")
        consumeChemistry.forEach{println(it.description())}


    }

}

fun main(args: Array<String>) {
    SpringApplication.run(ImportChemistryStation::class.java, *args)
}


@Component
class ConsumeFalloutService {


    //example url "http://fallout.wikia.com/wiki/Chemistry_station"
    fun consumeChemistry(url: String): List<ChemistryCraftingRecipe> {
        val document: Document = Jsoup.connect(url).get()
        val tableElements: Elements = document.getElementsByClass("va-table")

        val elements = mutableListOf<ChemistryCraftingRecipe>()

        tableElements
                .map { it.child(0) }
                .map { it.getElementsByTag("tr") }
                .map {
                    it
                            .filterIndexed { index, _ -> index > 0 }
                            .map { toChemistryCraftingRecipe(it) }
                }
                .forEach { elements.addAll(it) }
        return elements
    }


    fun toChemistryCraftingRecipe(trElement: Element): ChemistryCraftingRecipe {
        val tds = trElement.children()
        val title = tds[0].text()
        val xpGain = tds[1].text()
        val perksNeeded = tds[2].text()


        val itemsNeeded = tds[3]
        val listItemsNeeded: Elements = itemsNeeded.getElementsByTag("li")

        val requireItems = listItemsNeeded.map(Element::toRequiredItem)

        return ChemistryCraftingRecipe(title, xpGain, requireItems)
    }
}