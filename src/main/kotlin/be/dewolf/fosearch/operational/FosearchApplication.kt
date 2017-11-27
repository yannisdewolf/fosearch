package be.dewolf.fosearch.operational

import com.samskivert.mustache.Mustache
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mustache.MustacheEnvironmentCollector
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment

@SpringBootApplication
class FosearchApplication {


    @Bean
    fun compiler(mustacheTemplateLoader: Mustache.TemplateLoader,
                 environment: Environment): Mustache.Compiler {
        val collector = MustacheEnvironmentCollector()
        collector.setEnvironment(environment)

        return Mustache.compiler().defaultValue("")
                .withLoader(mustacheTemplateLoader)
                .withCollector(collector)
    }

}








fun main(args: Array<String>) {
    SpringApplication.run(FosearchApplication::class.java, *args)
}

