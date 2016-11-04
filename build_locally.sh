#!/bin/sh

###################
# Config
###################
# Aborts the script if a command fails
set -e

###################
# Vars and helpers
###################
SP='********';
function info () {
  echo "";
  echo "${SP}${SP}${SP}${SP}${SP}${SP}${SP}${SP}${SP}${SP}${SP}${SP}";
  echo "$SP [SCRIPT INFO] $1";
  echo "${SP}${SP}${SP}${SP}${SP}${SP}${SP}${SP}${SP}${SP}${SP}${SP}";
  echo "";
}

###################
## Go woop woop
###################

###################
info "Check for needed binaries in PATH";
BINS_TO_CHECK=( mvn docker )
for BIN in ${BINS_TO_CHECK[@]}; do
  command -v $BIN >/dev/null 2>&1 || { echo >&2 "I require '$BIN' but it's not installed.  Aborting."; exit 1; };
done
echo "-> Fine √"

###################
info "Check if ENV variables were set"
if [ -z $GROUP ]; then
  echo "'GROUP' ENV var are not set";
  exit 1;
fi

###################
info "Bulding microservices"
MICROSERVICES=( api-gateway )
for MICROSERVICE in ${MICROSERVICES[@]}; do
  info "Bulding '$MICROSERVICE'"
  cd $MICROSERVICE && mvn clean package docker:build && cd ..
done
