FROM openjdk:8

# Env variables
ENV SCALA_VERSION 2.12.3
ENV SBT_VERSION 1.0.2

RUN apt-get update

# Scala expects this file
RUN touch /usr/lib/jvm/java-8-openjdk-amd64/release

# Install Scala
## Piping curl directly in tar
RUN \
  curl -fsL https://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
  echo >> /root/.bashrc && \
  echo "export PATH=~/scala-$SCALA_VERSION/bin:$PATH" >> /root/.bashrc

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

# Add application from disk
WORKDIR /app
ADD . /app

# RUN sbt test
RUN sbt dist
RUN set -x && unzip -d svc target/universal/soulmates-backend* && mv svc/*/* svc/ && rm svc/bin/*.bat && mv svc/bin/* svc/bin/start

EXPOSE 9000 9443


CMD svc/bin/start -Dhttps.port=9443 -Dplay.http.secret.key=$JWT_APPLICATION_SECRET