#!/bin/bash
docker kill pushgateway
docker kill prometheus
docker rm -v pushgateway
docker rm -v prometheus

