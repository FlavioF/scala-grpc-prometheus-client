# scala-grpc-prometheus-client

## TL;DR
```
./run-all.sh
```

### Running Prometheus

```
docker run -d -p 9090:9090 -v $(pwd):/etc/prometheus/  --name prometheus prom/prometheus
```

### Sample configurations

You may need to update the IPs in prometheus.yml according to your docker default gateway or container IP.
To refresh the prometheus configuration after change the file:

```
curl -X POST http://localhost:9090/-/reload
```

### Exporters

Running  push gateway (since the application will send data to here)
```
docker run -d --name=pushgateway -p 9091:9091 prom/pushgateway
```

Running docker exporter (just for fun)

```
docker run -d -p 9104:9104 --name=docker-exporter -v /sys/fs/cgroup:/cgroup -v /var/run/docker.sock:/var/run/docker.sock prom/container-exporter prom/container-exporter
```

### Running the application

```
./sbt run
```

### Exploring the metrics

http://localhost:9090

