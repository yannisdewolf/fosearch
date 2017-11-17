package be.dewolf.fosearch

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class FosearchApplication

fun main(args: Array<String>) {
    SpringApplication.run(FosearchApplication::class.java, *args)
}
