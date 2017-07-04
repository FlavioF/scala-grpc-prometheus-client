FROM java:8-jdk

WORKDIR /app
ADD . /app

CMD bash sbt run