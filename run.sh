#!/bin/bash
GOOGLE_APPLICATION_CREDENTIALS=/Users/deepak.kumar/Downloads/Keys/blp.json
RUNNER=DirectRunner

mvn compile
mvn exec:java -Dexec.mainClass=com.blp.ingestion.Main -Dexec.args="--runner=$RUNNER --tempLocation=/tmp/"

