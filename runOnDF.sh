#!/bin/bash
GOOGLE_APPLICATION_CREDENTIALS=/Users/deepak.kumar/Downloads/Keys/blp-prod.json
RUNNER=DataflowRunner
INPUT_FILE="gs://mongodb-historical-dump/tenMinAggData_collections/*.json"
TEMP_LOCATION="gs://mongodb-historical-dump/tmp/"
STAGING_LOCATION="gs://mongodb-historical-dump/staging/"

mvn compile
mvn exec:java -Dexec.mainClass=com.blp.ingestion.Main \
-Dexec.args="--runner=$RUNNER \
--inputFile=$INPUT_FILE \
--project=platform-prod-blp \
--tempLocation=$TEMP_LOCATION \
--stagingLocation=$STAGING_LOCATION \
--diskSizeGb=100 \
--workerMachineType=n1-highmem-4"

