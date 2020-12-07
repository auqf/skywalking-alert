java -jar -Dspring.config.location=application.yml  sky-0.0.1-SNAPSHOT.jar

mvn clean install -Dmaven.test.skip=true
 

curl -H "Content-Type: application/json" -X POST  --data '[{
    "scopeId": 1,
    "scope": "SERVICE",
    "name": "serviceA",
    "id0": 12,
    "id1": 0,
    "ruleName": "service_resp_time_rule",
    "alarmMessage": "alarmMessage xxxx",
    "startTime": 1560524171000
}, {
    "scopeId": 1,
    "scope": "SERVICE",
    "name": "serviceB",
    "id0": 23,
    "id1": 0,
    "ruleName": "service_resp_time_rule",
    "alarmMessage": "alarmMessage yyy",
    "startTime": 1560524171000
}]' http://10.101.96.101:58080/alarm/email

