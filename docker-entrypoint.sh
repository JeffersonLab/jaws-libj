#!/bin/sh

echo "----------------------------------------------"
echo "Step 1: Waiting for a JAWS Schema in Registry "
echo "----------------------------------------------"
url=$SCHEMA_REGISTRY
echo "waiting on: $url"
while [ $(curl -s -o /dev/null -w %{http_code} $url/subjects/alarm-overrides-value/versions) -ne 200 ] ; do
  echo -e $(date) " Kafka Registry listener HTTP state: " $(curl -s -o /dev/null -w %{http_code} $url/subjects/alarm-overrides-value/versions) " (waiting for 200)"
  sleep 5
done

sleep infinity
