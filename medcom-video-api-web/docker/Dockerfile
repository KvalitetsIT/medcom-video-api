FROM adoptopenjdk:11-jre-openj9

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y gettext-base tzdata inetutils-telnet

COPY   entrypoint.sh /
RUN chmod +x entrypoint.sh
COPY configtemplates  /configtemplates

RUN mkdir /app
ADD /maven/medcom-video-api-web.jar /app/medcom-video-api-web.jar
WORKDIR /app

ENTRYPOINT [ "/entrypoint.sh" ]