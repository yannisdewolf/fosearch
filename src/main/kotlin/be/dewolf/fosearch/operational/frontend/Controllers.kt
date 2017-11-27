package be.dewolf.fosearch.operational.frontend

import be.dewolf.fosearch.operational.import.ChemistryCraftingRecipe
import be.dewolf.fosearch.operational.import.RequiredItem
import be.dewolf.fosearch.operational.service.ChemistryCraftingRecipeRepository
import be.dewolf.fosearch.operational.service.CraftablesService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@Controller
class HomeController(val chemistryCraftingRecipeRepository: ChemistryCraftingRecipeRepository,
                     val craftablesService: CraftablesService) {
    @GetMapping("/")
    fun home(): String {
        return "myindex"
    }

    @GetMapping("/craftable")
    fun displayCraftable(model: MutableMap<String, Any>): ModelAndView {
        model.put("craftables", craftablesService.allNonAddons().map { toTo(it) })
        return ModelAndView("myindex", model)
    }
}


@RestController
@RequestMapping("/api")
class FosearchApi(val chemistryCraftingRecipeRepository: ChemistryCraftingRecipeRepository,
                  val craftablesService: CraftablesService) {

    @PostMapping("/{id}/type")
    fun setType(@PathVariable id: String, @RequestBody newType: UpdateCraftableToType) : ResponseEntity<Unit> {
        println(newType)
        val valueOf = CraftableType.valueOf(newType.newType.orEmpty())
        println("received a post for updating element with id $id to type ${newType.newType} $valueOf")

        craftablesService.updateRecipeWithStatus(id, newType.newType!!)

        return ResponseEntity.accepted().build()
    }

    @GetMapping("/welcomehome")
    fun welcomeHome(): String ="hello world"


    @GetMapping("/allCraftables")
    fun allCraftables(): Any = craftablesService.all()

    @GetMapping("/{type}")
    fun byType(@PathVariable type: String): ResponseEntity<Any> {
        return ResponseEntity.ok(craftablesService.getByType(type))
    }

    @GetMapping("/alltypes")
    fun allTypes(): ResponseEntity<Any> = ResponseEntity.ok(craftablesService.allTypes())

    @PutMapping("/craftable/{id}/addon")
    fun markAsAddon(@PathVariable id: String): ResponseEntity<Any> {
        craftablesService.markAsAddon(id)
        return ResponseEntity.ok().build()
    }

}

data class UpdateCraftableToType(val newType: String? = "")

data class ChemistryCraftableTO(val name: String,
                                val type: String? = "",
                                val id: String,
                                val requiredItems: List<RequiredItem>,
                                val change_types_hidden: String
)

fun toTo(chemistryCraftableRecipe: ChemistryCraftingRecipe): ChemistryCraftableTO {

    val p = if (chemistryCraftableRecipe.type.isNullOrBlank()) "visible" else "hidden"

    return ChemistryCraftableTO(
            chemistryCraftableRecipe.name,
            chemistryCraftableRecipe.type,
            chemistryCraftableRecipe.id!!,
            chemistryCraftableRecipe.requiredItems,
            p)
}

enum class CraftableType {

    DRUGS, FIREWORKS, GRENADES, HEALING, MINES, NUKA_NUKE, SYRINGER_AMMO,
    THIRST_ZAPPER_AMMO, TRAPS, UTILITY


}
