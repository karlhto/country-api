# Country API

A simple application for fetching and caching country data from a public API.

## Running the application

To start the application:

```bash
$ ./gradlew run
```

By default the application runs on port 8080. Currently only these endpoints are accessible:
```bash
$ curl http://localhost:8080/v1/countries/europe?sort_by=name&sort_order=asc
$ curl http://localhost:8080/v1/currencies
```
