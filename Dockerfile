FROM vertx/vertx4

ENV VERTICLE_NAME com.yteplyi.sandbox.reactive.SimpleVerticle
ENV VERTICLE_FILE target/

ENV VERTICLE_HOME /usr/verticles

COPY $VERTICLE_FILE $VERTICLE_HOME/

EXPOSE 8080

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]
