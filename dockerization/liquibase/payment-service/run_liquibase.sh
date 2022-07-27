#!/usr/bin/env bash

liquibase_action="$1"
liquibase_action_args="${*:2}"

echo $liquibase_action_args

case $liquibase_action in
  update)
    printf "executing update\n"
    ;;

  update-sql)
    printf "executing update-sql\n"
    ;;

  update-count)
    printf "executing update-count\n"
    ;;

  update-count-sql)
    printf "executing update-count-sql\n"
    ;;

  rollback)
    printf "executing rollback\n"
    ;;

  rollback-one-changeset)
    printf "executing rollback-one-changeset\n"
    ;;

  *)
    printf "unknown liquibase action\n"
    exit 0
    ;;
esac

w_pwd=$(cygpath -w "$(pwd)")

docker run --rm --network=dockerization_reactive-sandbox \
  -v "$w_pwd":/liquibase/changelog \
  liquibase/liquibase \
  --defaultsFile=./changelog/liquibase.docker.properties "$liquibase_action" $liquibase_action_args