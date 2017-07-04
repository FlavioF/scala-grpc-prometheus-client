name := "scala-grpc-prometheus-client"

version := "0.1.0"

scalaVersion := "2.12.1"

scalacOptions ++= Seq("-feature", "-deprecation")

val scalaPbVersion = "0.5.47"

PB.protocVersion := "-v320"

PB.targets in Compile := Seq(
  scalapb.gen(grpc=true) -> (sourceManaged in Compile).value
)

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies ++= Seq(
    "me.dinowernli" % "java-grpc-prometheus" % "0.3.0",
    "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % scalaPbVersion,
    "io.grpc" % "grpc-netty" % "1.1.2",
    "io.grpc" % "grpc-protobuf" % "1.1.2",
    "io.grpc" % "grpc-core" % "1.1.2",
    "io.grpc" % "grpc-protobuf-lite" % "1.1.2",
    "io.grpc" % "grpc-stub" % "1.1.2",
    "io.netty" % "netty-all" % "4.1.8.Final",
    "io.prometheus" % "simpleclient" % "0.0.21",
    "io.prometheus" % "simpleclient_hotspot" % "0.0.21",
    "io.prometheus" % "simpleclient_pushgateway" % "0.0.21",
    "com.trueaccord.scalapb" %% "scalapb-runtime" % scalaPbVersion % "protobuf"
)

dependencyOverrides += "com.google.guava" % "guava" % "20.0"

