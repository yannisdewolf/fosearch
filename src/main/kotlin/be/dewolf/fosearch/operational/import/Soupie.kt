package be.dewolf.fosearch.operational.import

import org.jsoup.nodes.Element
import org.springframework.data.annotation.Id


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
        val addon: Boolean? = false,
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

