# MS-TRANSACTION-API
An API Rest built with Java 17, unit test with Mockito Junit 5 and H2 on memory.

## About
The aim of this repository is about to participate to Java code challenge.
There is the followings endpoints:

- getAllTransactions
- getTransactionByAccountIban
- saveTransaction
- verifyTransactionStored

## Techinologies

- Spring boot 3.0.0-SNAPSHOT
- Java 17
- H2 Database
- MapStruct
- Maven 3.8.6

## How to run
Download the microservice and in your favorite console run:
```
mvn clean install
```
This will install all dependencies necessaries.

```
mvn spring-boot:run
```

To retrieve a Transaction ordered by ASC or DESC:
```js
http://localhost:8080/transaction/all/{sort}
```

To Get a specifique Transaction filtering by accountIban:
```js
http://localhost:8080/transaction/{accountIban}
```

To persist a new transaction (method Post):
```js
http://localhost:8080/transaction/save
```

Endpoint to verify a transaction status (method Post):
```js
http://localhost:8080/transaction/status
```

Example of request to persist a transaction:
```js
{
    "reference": "09876666666178",
    "accountIban": "ES9820385778983000760237",
    "date": "2023-09-16T16:55:42.000Z",
    "amount": 193.38,
    "fee": 3.18,
    "status": "PENDING",
    "channel": "ATM",
    "description": "Restaurant payment"
}
```

To do a request to an endpoint to verify some transaction an example of body:
```js
{
    "reference": "23498734972934",
    "date": "2023-09-16T16:55:42.000Z",
    "channel": "ATM",
}
```

## Contributors
[@LauroSilveira](https://github.com/LauroSilveira)
