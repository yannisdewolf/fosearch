package be.dewolf.fosearch

import be.dewolf.fosearch.import.ChemistryCraftingRecipe
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.repository.CrudRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@SpringBootApplication
class FosearchApplication {

/*    @Bean
    fun commandLineRunner(
            soupie: ConsumeFalloutService,
            repo: CraftableRepository) = CommandLineRunner {
        val consumeFalloutCraftable = soupie.consumeFalloutCraftable("http://fallout.wikia.com/wiki/Chemistry_station")
        consumeFalloutCraftable.forEach {
            repo.save(it)
            println(it.description())
        }

    }
    */
}

interface CraftableRepository: CrudRepository<ChemistryCraftingRecipe, String>

@Controller
class HomeController(val craftableRepository: CraftableRepository) {
    @GetMapping("/")
    fun home(): String {
        return "myindex"
    }

    @GetMapping("/craftable")
    fun displayCraftable(model: MutableMap<String, Any>): ModelAndView {
        model.put("craftables", craftableRepository.findAll())
        return ModelAndView("myindex", model)
    }
}


@RestController
@RequestMapping("/api")
class FosearchApi(val craftableRepository: CraftableRepository) {

    @PostMapping("/{id}/type")
    fun setType(@PathVariable id: String, @RequestBody newType: UpdateCraftableToType) : ResponseEntity<Unit>{
        println("received a post for updating element with id $id to type ${newType.newType}")
        return ResponseEntity.accepted().build()
    }

    @GetMapping("/welcomehome")
    fun welcomeHome(
    ): String {
        return "hello world"
    }

    @GetMapping("/allCraftables")
    fun allCraftables(): Any = craftableRepository.findAll()

}

data class UpdateCraftableToType(val newType: String? = "")

fun main(args: Array<String>) {
    SpringApplication.run(FosearchApplication::class.java, *args)
}

