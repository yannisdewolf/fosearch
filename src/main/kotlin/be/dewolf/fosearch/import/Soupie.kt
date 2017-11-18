package be.dewolf.fosearch.import

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.data.annotation.Id
import org.springframework.stereotype.Component


fun main(args: Array<String>) {
    val consumeFalloutCraftables = ConsumeFalloutService().consumeFalloutCraftable("http://fallout.wikia.com/wiki/Chemistry_station");
    consumeFalloutCraftables.forEach { println(it.description()) }


}

@Component
class ConsumeFalloutService {


    //example url "http://fallout.wikia.com/wiki/Chemistry_station"
    fun consumeFalloutCraftable(url: String): List<ChemistryCraftingRecipe> {
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


    fun toChemistryCraftingRecipe(element: Element): ChemistryCraftingRecipe {
        val tds = element.children()
        val title = tds[0].text()
        val xpGain = tds[1].text()
        val perksNeeded = tds[2].text()


        val itemsNeeded = tds[3]
        val listItemsNeeded: Elements = itemsNeeded.getElementsByTag("li")

        val requireItems = listItemsNeeded.map(Element::toRequiredItem)

        return ChemistryCraftingRecipe(title, xpGain, requireItems)
    }
}

fun Element.toRequiredItem(): RequiredItem {
    val toConvert: Element = this;
    val needed = toConvert.getElementsByTag("a")[0].text()

    val lastIndex = toConvert.text().lastIndexOf(" x")

    val amountNeeded: String = if (lastIndex == -1) "1" else toConvert.text().substring(lastIndex + 2)
    return RequiredItem(amountNeeded, needed)
}

@org.springframework.data.mongodb.core.mapping.Document
data class ChemistryCraftingRecipe(
                                            val name: String,
                                            val xpGain: String,
                                            val requiredItems: List<RequiredItem>,
                                            val type: String? = "",
                                            @Id val id: String? = null
                                            )




fun ChemistryCraftingRecipe.description(): String {
    val title = this.name
    val xpGain = this.xpGain
    val requiredItemsDescription = this.requiredItems.map { it.description() }

    return "$title gains $xpGain by building and needs $requiredItemsDescription"

}


data class RequiredItem(val amountNeeded: String, val elementNeeded: String)

fun RequiredItem.description(): String = this.amountNeeded + " " + this.elementNeeded

