package services

import com.google.common.base.Strings
import com.google.common.collect.Iterators
import io.grpc.{ManagedChannelBuilder, ServerBuilder, ServerInterceptors}
import io.prometheus.client.CollectorRegistry
import me.dinowernli.grpc.prometheus.{Configuration, MonitoringClientInterceptor, MonitoringServerInterceptor}
import services.kv.{GetRequest, KeyValueServiceGrpc}
import services.user.{UserRequest, UserServiceGrpc}
import io.prometheus.client.exporter.PushGateway

import scala.concurrent.ExecutionContext

object Main extends App {
  var DOCKER_IP = System.getProperty("docker.ip")
  if (Strings.isNullOrEmpty(DOCKER_IP)) DOCKER_IP = "localhost"

  val registryKv = new CollectorRegistry()
  val registryUser = new CollectorRegistry()


  val kvPort = 15001
  val userPort = 15002

  val kvSrv = ServerBuilder
    .forPort(kvPort)
    .addService(
      ServerInterceptors.intercept(
        KeyValueServiceGrpc.bindService(new KeyValueServiceImpl, ExecutionContext.global),
        MonitoringServerInterceptor.create(Configuration.allMetrics().withCollectorRegistry(registryKv))
      )
    )
    .build()

  val kvChannel = ManagedChannelBuilder.forAddress("localhost", kvPort).usePlaintext(true).build()
  val kvStub = KeyValueServiceGrpc.blockingStub(kvChannel)
    .withInterceptors(MonitoringClientInterceptor.create(Configuration.allMetrics().withCollectorRegistry(registryKv)))

  val userSrv = ServerBuilder
    .forPort(userPort)
    .addService(
      ServerInterceptors.intercept(
        UserServiceGrpc.bindService(new UserServiceImpl(kvStub), ExecutionContext.global),
        MonitoringServerInterceptor.create(Configuration.allMetrics().withCollectorRegistry(registryUser))
      )
    )
    .build()

  val userChannel = ManagedChannelBuilder.forAddress("localhost", userPort).usePlaintext(true).build()
  val userStub = UserServiceGrpc.blockingStub(userChannel)
    .withInterceptors(MonitoringClientInterceptor.create(Configuration.allMetrics().withCollectorRegistry(registryUser)))

  kvSrv.start()
  userSrv.start()

  while (true) {

    val users = Iterators.cycle("karen", "bob", "john")
    (1 to 50).foreach { idx =>
      println(userStub.getUser(UserRequest(users.next())))
    }

    var collector = CollectorRegistry.defaultRegistry
    println("requests completed")

    new PushGateway(DOCKER_IP + ":9091").pushAdd(registryKv, "KeyValueServiceBatch");
    new PushGateway(DOCKER_IP + ":9091").pushAdd(registryUser, "UserServiceBatch");

    //waiting a bit for the next iteration of data
    Thread.sleep(5000)
  }

}
