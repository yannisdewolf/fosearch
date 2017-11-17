import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

//example url "http://fallout.wikia.com/wiki/Chemistry_station"
fun consumeFalloutCraftable(url: String): List<ChemistryCraftingRecipe> {
    val document: Document = Jsoup.connect(url).get()
    val tableElements: Elements = document.getElementsByClass("va-table")

    val elements = mutableListOf<ChemistryCraftingRecipe>()

    for (tableElement in tableElements) {
        val tbodyElement = tableElement.child(0)
        val rows = tbodyElement.getElementsByTag("tr")
        val chemistryCraftableItems = rows
                .filterIndexed { index, element -> index > 0 }
                .map { rowContent -> toChemistryCraftingRecipe(rowContent) }
        elements.addAll(chemistryCraftableItems)

    }
    return elements
}

fun main(args: Array<String>) {
    val consumeFalloutCraftable = consumeFalloutCraftable("http://fallout.wikia.com/wiki/Chemistry_station");
    consumeFalloutCraftable.forEach{ println(it.description())}


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

data class ChemistryCraftingRecipe(
        val name: String,
        val xpGain: String,
        val requiredItems: List<RequiredItem>
)

fun ChemistryCraftingRecipe.description(): String {
    val title = this.name
    val xpGain = this.xpGain
    val requiredItemsDescription = this.requiredItems.map{ it.description() }

    return "$title gains $xpGain by building and needs $requiredItemsDescription"

}

fun Element.toRequiredItem(): RequiredItem {
    val toConvert: Element = this;
    val needed = toConvert.getElementsByTag("a")[0].text()

    val lastIndex = toConvert.text().lastIndexOf(" x")

    val amountNeeded: String = if (lastIndex == -1) "1" else toConvert.text().substring(lastIndex + 2)
    return RequiredItem(amountNeeded, needed)
}

data class RequiredItem(val amountNeeded: String, val elementNeeded: String)

fun RequiredItem.description(): String = this.amountNeeded + " " + this.elementNeeded

fun tovalue(toConvert: Element): String {
    val needed = toConvert.getElementsByTag("a").get(0).text()

    val lastIndex = toConvert.text().lastIndexOf("x")

    val amountNeeded: String = if (lastIndex == -1) "1" else toConvert.text().substring(lastIndex + 1)
    return "$amountNeeded $needed"
}