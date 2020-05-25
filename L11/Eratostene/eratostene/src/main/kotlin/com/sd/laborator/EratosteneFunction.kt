package com.sd.laborator;

import io.micronaut.function.executor.FunctionInitializer
import io.micronaut.function.FunctionBean;
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.*;
import java.util.function.Function;


@FunctionBean("eratostene")
class EratosteneFunction : FunctionInitializer(), Function<EratosteneRequest, EratosteneResponse> {
    @Inject
    private lateinit var eratosteneSieveService: EratosteneSieveService

    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)

    override fun apply(req : EratosteneRequest) : EratosteneResponse {
        val response = EratosteneResponse()
        val maxNumber = req.getMaxNumber() + 1

        // se verifica daca numarul maxim nu depaseste maximul acceptat
        if (maxNumber >= eratosteneSieveService.MAX_SIZE) {
            LOG.error("Parametru prea mare! $maxNumber > maximul de ${eratosteneSieveService.MAX_SIZE}")
            response.setMessage("Se accepta doar numere mai mici ca " + eratosteneSieveService.MAX_SIZE)
            return response
        }

        LOG.info("Se calculeaza primele $maxNumber numere prime ...")

        // se face calculul si se seteaza proprietatile pe obiectul cu rezultatul
        val allPrimes = eratosteneSieveService.findPrimesLessThan(maxNumber)
        response.setPrimes(req.getNumbers().filter { allPrimes.contains(it) })
        response.setMessage("Calcul efectuat cu succes!")

        LOG.info("Calcul incheiat!")
        return response
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) { 
    val function = EratosteneFunction()
    function.run(args, { context -> function.apply(context.get(EratosteneRequest::class.java))})
}