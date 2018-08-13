FROM openjdk:8u111-jre-alpine
RUN apk update && \
    apk add --update gettext tzdata

COPY   entrypoint.sh /
RUN chmod +x entrypoint.sh
COPY configtemplates  /configtemplates

RUN mkdir /app
ADD /maven/medcom-video-api-web.jar /app/medcom-video-api-web.jar
WORKDIR /app

ENTRYPOINT [ "/entrypoint.sh" ]