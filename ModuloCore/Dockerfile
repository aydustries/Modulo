FROM        openjdk:19-slim

LABEL       author="aytronn"

RUN apt-get update -y \
 && apt-get install -y curl ca-certificates openssl git tar fontconfig tzdata \
 && useradd -d /home/container -m container

WORKDIR     /home/app

COPY        ./ModuloCore.jar /home/app

COPY        start.sh /home/app

CMD         ["/bin/bash", "start.sh"]
