#!/bin/sh

# Pulls the latest images and restarts the updated ones
# Details: https://stackoverflow.com/questions/31466428/how-to-restart-a-single-container-with-docker-compose
docker-compose pull && docker-compose up -d --build