package fan.zheyuan.graphql

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.pgutkowski.kgraphql.KGraphQL
import fan.zheyuan.domain.model.Beer
import fan.zheyuan.domain.repository.BeerRepository
import org.koin.core.KoinComponent

class GraphQLModel(private val beerRepository: BeerRepository) : KoinComponent {
    val schema = KGraphQL.schema {
        configure {
            useDefaultPrettyPrinter = true
            objectMapper = jacksonObjectMapper()
        }

        query("beers") {
            resolver { size: Int ->
                beerRepository.getAll(size)
            }.withArgs {
                arg<Int> { name = "size"; defaultValue = 20 }
            }
        }

        query("beer") {
            resolver { id: Int -> beerRepository.findById(id) }
                .withArgs { arg<Int> { name = "id" } }
        }

        mutation("placeholder") {
            resolver { -> listOf(Beer()) }
        }

        type<Beer>()
    }
}