#!/bin/bash
echo "Creating prometheus config file from prometheus.tmpl"
cp -rf prometheus.tmpl prometheus.yml

echo "Running Push Gateway"
docker run -d --name=pushgateway -p 9091:9091 prom/pushgateway

PUSH_GATEWAY_IP=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" pushgateway)
echo "Add pushgateway IP ["$PUSH_GATEWAY_IP"] to prometheus configuration"
sed -i -e "s/PUSH_GATEWAY_IP/$PUSH_GATEWAY_IP/g" prometheus.yml

echo "Running Prometheus"
docker run -d -p 9090:9090 -v $(pwd):/etc/prometheus/  --name prometheus prom/prometheus

# Do not run docker exporter because it is not necessary
#echo "Running Docker Exporter"
#docker run -d -p 9104:9104 --name=docker-exporter -v /sys/fs/cgroup:/cgroup -v /var/run/docker.sock:/var/run/docker.sock prom/container-exporter prom/container-exporter

#DOCKER_EXPORTER_IP=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" pushgateway)
#echo "Add docker exporter IP ["$DOCKER_EXPORTER_IP"] to prometheus configuration"
#sed -i -e "s/DOCKER_EXPORTER_IP/$DOCKER_EXPORTER_IP/g" prometheus.yml

./sbt run
