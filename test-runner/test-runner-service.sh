#!/bin/sh

TEST_PLAN_NAME='fapi-rw-id2-client-test-plan'
VARIANT_URL_PARAM='%7Bclient_auth_type:%22private_key_jwt%22,fapi_profile:%22plain_fapi%22%7D'
BASE_URL='https://localhost:8443/api'

test_results=()

# Getting test plan
echo "Getting test plan. PlanName: $TEST_PLAN_NAME - Variant: $VARIANT_URL_PARAM"
test_plan=curl POST "$BASE_URL/plan?planName=$TEST_PLAN_NAME&variant=$VARIANT_URL_PARAM" \
    -H "accept: application/json" -H "Content-Type: application/json" \
    -d "$(cat /etc/config/fapi-rw-id2-with-private-key-RS256-PS256.json)"
echo "Test plan: ${test_plan}"

# Iterate through tests in test plan
$test_plan | jq -r '.modules.[].testModule' | while read testModule ; do

    # Getting test details & ID
    echo "Getting test detail for $testModule"
    test_detail=curl POST "$BASE_URL/runner?test=$testModule&plan=$($test_plan | jq '.id')" \
        -H "accept: application/json" -H "Content-Type: application/json"
    test_id=$test_detail | jq '.id'
    echo "Test detail:\n$test_detail"

    # Getting test logs/result and adding to results list
    echo "Getting test result for $testModule - ID: $test_id"
    test_result=curl "$BASE_URL/log/$test_id" \
        -H "accept: application/json" -H "Content-Type: application/json"
    echo "Test result:\n$test_result"
    test_results+=test_result
done

echo "Test results:\n$test_results"