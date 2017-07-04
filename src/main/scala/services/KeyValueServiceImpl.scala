package services

import services.kv._

import scala.concurrent.Future
import scala.util.Random

class KeyValueServiceImpl extends KeyValueServiceGrpc.KeyValueService {

  var store = Map (
    "bob.emailAddress" -> "bob@whisk.co.uk",
    "bob.country" -> "UK",
    "bob.active" -> "true",
    "karen.emailAddress" -> "karen@whisk.co.uk",
    "karen.country" -> "France",
    "karen.active" -> "true",
    "john.emailAddress" -> "john@whisk.co.uk",
    "john.country" -> "Portugal",
    "john.active" -> "false")

  override def put(request: PutRequest): Future[PutResponse] = {
    store += (request.key  -> request.value)
    Future.successful(PutResponse())
  }

  override def get(request: GetRequest): Future[GetResponse] = {

    val random = new Random

    try
      Thread.sleep(random.nextInt(100))
    catch {
      case ignored: InterruptedException => {
      }
    }

    val value : String = String.valueOf(store.get(request.key))

    Future.successful(GetResponse(key = request.key, value = value))
  }
}
