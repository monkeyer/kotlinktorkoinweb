package fan.zheyuan.service

import fan.zheyuan.domain.Parser
import fan.zheyuan.domain.model.Beer
import fan.zheyuan.domain.repository.BeerRepository
import org.koin.core.KoinComponent

class BeerService(val beerRepository: BeerRepository) : KoinComponent {
    fun importBeers() {
        val beers = Parser.parseBeers()
        beerRepository.createTable()
        beerRepository.insertList(beers)
    }
    fun getAllBeers() = beerRepository.getAll()
    fun createBeer(beer: Beer) = beerRepository.createBeer(beer)
}