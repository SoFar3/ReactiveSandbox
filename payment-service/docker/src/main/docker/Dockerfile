FROM vertx/vertx4

ENV JAVA_OPTS "-agentlib:jdwp=transport=dt_socket,server=y,address=18787,suspend=n"

ENV VERTICLE_NAME ${main.verticle}
ENV VERTICLE_FILE /app

ENV VERTICLE_HOME /usr/verticles

COPY $VERTICLE_FILE $VERTICLE_HOME/

EXPOSE 8080

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME --java-opts=\"$JAVA_OPTS\" -cp $VERTICLE_HOME/*"]
