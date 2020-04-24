import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import kotlin.Exception
import kotlin.random.Random
import kotlin.system.exitProcess

class Identity constructor(val name: String, val email: String, val phone: String)

val identities = listOf(
    Identity("Pop-Ion", "pop_ion@gmail.com", "0743399200"),
    Identity("Pop-Ana", "pop_ana@gmail.com", "0743399201"),
    Identity("Popescu-Stefan", "popescu_stefan@gmail.com", "0743399202"),
    Identity("Popescu-George", "popescu_george@gmail.com", "0743399203"),
    Identity("Turda-Ion", "turda_ion@gmail.com", "0743399204"),
    Identity("Antonie-Vasile", "antonie_vasile@gmail.com", "0743399205"),
    Identity("Marcu-Marin", "marcu_marin@gmail.com", "0743399206"),
    Identity("Puiu-Mircea", "puiu_mircea@gmail.com", "0743399207"),
    Identity("Bogda-Andrei", "bogda_andrei@gmail.com", "0743399208"),
    Identity("Stefan-Iulian", "stefan_iulian@gmail.com", "0743399209")
)

class BidderMicroservice {
    private var auctioneerSocket: Socket
    private var auctionResultObservable: Observable<String>
    private var myIdentity: String = "[BIDDER_NECONECTAT]"

    companion object Constants {
        const val AUCTIONEER_HOST = "localhost"
        const val AUCTIONEER_PORT = 1500
        const val MAX_BID = 10_000
        const val MIN_BID = 1_000
    }

    init {
        try {
            auctioneerSocket = Socket(AUCTIONEER_HOST, AUCTIONEER_PORT)
            println("M-am conectat la Auctioneer!")
            myIdentity = "[${auctioneerSocket.localPort}]"
            // se creeaza un obiect Observable ce va emite mesaje primite printr-un TCP
            // fiecare mesaj primit reprezinta un element al fluxului de date reactiv
            auctionResultObservable = Observable.create<String> {
                    emitter ->
                // se citeste raspunsul de pe socketul TCP
                val bufferReader = BufferedReader(InputStreamReader(auctioneerSocket.inputStream))
                val receivedMessage = bufferReader.readLine()
            // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if (receivedMessage == null) {
                    bufferReader.close()
                    auctioneerSocket.close()
                    emitter.onError(Exception("AuctioneerMicroservice s-a deconectat."))
                    return@create
                }
                // mesajul primit este emis in flux
                emitter.onNext(receivedMessage)
                // deoarece se asteapta un singur mesaj, in continuare se emite semnalul de incheiere al fluxului
                emitter.onComplete()
                bufferReader.close()
                auctioneerSocket.close()
            }
        } catch (e: Exception) {
            println("$myIdentity Nu ma pot conecta la Auctioneer!")
            exitProcess(1)
        }
    }

    private fun bid() {
        // se genereaza o identitate aleatorie pentru bidderul curent
        val identity = identities[Random.nextInt(0, 9)]
        // se genereaza o oferta aleatorie din partea bidderului curent
        val pret = Random.nextInt(MIN_BID, MAX_BID)
        // se creeaza mesajul care incapsuleaza oferta
        val biddingMessage = Message.create(
            "${auctioneerSocket.localAddress}:${auctioneerSocket.localPort}[${identity.name}|${identity.email}|${identity.phone}]",
            "licitez $pret")
        // bidder-ul trimite pretul pentru care doreste sa liciteze
        val serializedMessage = biddingMessage.serialize()
        auctioneerSocket.getOutputStream().write(serializedMessage)
        // exista o sansa din 2 ca bidder-ul sa-si trimita oferta de 2 ori, eronat
        if (Random.nextBoolean()) {
            auctioneerSocket.getOutputStream().write(serializedMessage)
        }
    }

    private fun waitForResult() {
        println("$myIdentity Astept rezultatul licitatiei...")
        // bidder-ul se inscrie pentru primirea unui raspuns la oferta trimisa de acesta
        val auctionResultSubscription = auctionResultObservable.subscribeBy(
            // cand se primeste un mesaj in flux, inseamna ca a sosit rezultatul licitatiei
            onNext = {
                val resultMessage: Message = Message.deserialize(it.toByteArray())
                println("$myIdentity Rezultat licitatie: ${resultMessage.body}")
            },
            onError = {
                println("$myIdentity Eroare: $it")
            }
        )
        // se elibereaza memoria obiectului Subscription
        auctionResultSubscription.dispose()
    }

    fun run() {
        bid()
        waitForResult()
    }
}

fun main(args: Array<String>) {
    val bidderMicroservice = BidderMicroservice()
    bidderMicroservice.run()
}