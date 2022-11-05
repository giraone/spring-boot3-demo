# Spring Boot 2 Demo

Basically the code from
[Spring Tips: the road to Spring Boot 3: Spring Framework 6](https://spring.io/blog/2022/10/26/spring-tips-the-road-to-spring-boot-3-spring-framework-6).

## Shows

- `@ControllerAdvice` with `ProblemDetail` (RFC 7807).
- Access to jakarta packages.
- Using metrics with `@Observed` and `ObservationRegistry/Observation`
- Interface based `WebClient`

## Content

There is on endpoint `/calculate/{input1}[?input2={input2}]` calculating the fibonacci value directly for `input1`
and optionally and indirectly via a REST call to `/calculate/{input}` to the same service.

## Tests from Command Line

ProblemDetail

```shell
$ curl http://localhost:8080/calculate/0
{"type":"about:blank","title":"Bad Request","status":400,"detail":"PathVariable input1 must be between [1,30]","instance":"/calculate/0"}
```

Calculations and interface based `WebClient`

```shell
curl http://localhost:8080/calculate/10
{"ok":true,"value1":55,"value2":0}

curl http://localhost:8080/calculate/10?input2=10
{"ok":true,"value1":55,"value2":21}
```

Metrics and Observation

```shell
curl http://localhost:8080/actuator/metrics | jq
```

```json
{
  "names": [
    "application.ready.time",
    "application.started.time",
    "demo.calculate",
    "demo.calculate.active",
    "disk.free",
    "disk.total",
    ...
    "http.server.requests",
  ]
}
```

```shell
curl http://localhost:8080/actuator/metrics/demo.calculate | jq
```

```json
{
  "name": "demo.calculate",
  "description": null,
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 436
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 0.038327775
    },
    {
      "statistic": "MAX",
      "value": 0
    }
  ],
  "availableTags": [
    {
      "tag": "error",
      "values": [
        "none"
      ]
    }
  ]
}
``` 
