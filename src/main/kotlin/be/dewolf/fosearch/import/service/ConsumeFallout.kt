package be.dewolf.fosearch.import.service

import be.dewolf.fosearch.operational.import.RequiredItem
import org.springframework.data.annotation.Id


@org.springframework.data.mongodb.core.mapping.Document
data class ChemistryCraftingRecipe(
        val name: String,
        val xpGain: String,
        val requiredItems: List<RequiredItem>,
        val type: String? = "",
        val addon: Boolean? = false,
        @Id val id: String? = null
)