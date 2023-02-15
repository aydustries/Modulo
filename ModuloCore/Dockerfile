FROM        openjdk:19-slim

LABEL       author="aytronn"

RUN apt-get update -y \
 && apt-get install -y curl ca-certificates openssl git tar fontconfig tzdata \
 && useradd -d /home/container -m container

WORKDIR     /home/app

COPY        ./ModuloCore.jar /home/app

RUN         echo "#!/bin/bash\nwhile true; do\njava -jar ModuloCore.jar\necho \"Restarting app in 5s...\"\nsleep 5s\n\ndone" > /home/app/start.sh

RUN         chmod +x /home/app/start.sh

CMD         ["/bin/bash", "start.sh"]
