#!/bin/bash

sbt "+ play-initializer/publishSigned" \
    "+ play-dbapi-adapter/publishSigned" \
    "+ play-fixture/publishSigned"
