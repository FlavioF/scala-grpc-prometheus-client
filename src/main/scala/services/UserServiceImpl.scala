package services

import com.google.common.base.Preconditions
import services.kv.{GetRequest, KeyValueServiceGrpc}
import services.user.{UserRequest, UserResponse, UserServiceGrpc}

import scala.concurrent.Future

class UserServiceImpl extends UserServiceGrpc.UserService {

  var keyValueServiceStub: KeyValueServiceGrpc.KeyValueServiceBlockingClient = null

  def this(keyValue: KeyValueServiceGrpc.KeyValueServiceBlockingClient) {
    this()
    this.keyValueServiceStub = Preconditions.checkNotNull(keyValue)
  }

  override def getUser(request: UserRequest): Future[UserResponse] = {

    Future.successful(
      UserResponse(
        name = request.name,
        emailAddress = keyValueServiceStub.get(GetRequest(key = request.name + ".emailAddress")).value,
        country = keyValueServiceStub.get(GetRequest(key = request.name + ".country")).value
      )
    )
  }

}
