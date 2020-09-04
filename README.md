## CLI to simulate retail banking interactions

Developed by: Buddhi Prabhath

This application has been developed using below tech stack

* Java 8
* Maven 3.5.2
* Spring Boot 2.3.3
* H2 Embedded Database
* lombok 

#### How to run the program.

Assuming JAVA_HOME environment variable already set to Java 8 sdk in your environment and you have maven installed.

###### 1. using IDE

- you can use your favorite IDE (ex. IntelliJ IDEA)
- extract the zip file and import the project sources as a maven project
- create a Spring Boot running config (Intellij IDEA will create automatically when import the sources)
- click the run button to run the Application, it will start the CLI application

###### 2. using maven commandline tool on linux, mac, windows

extract the zip file

```
# go to the zip extracted directory
$ cd <extracted dir>

# build the jar using maven
$ mvn package

# go to target directory
$ cd target

# run the application using java -jar command
$ java -jar banking-0.0.1-SNAPSHOT.jar
```

Application uses the Embedded H2 database as the DB.

##### if successfully started the application you should see in the console: Started Application ...

```
Started Application ...
```

#### How to run all the unit tests.

```$ mvn test```

#### H2 Database

You can access the H2 Embedded Database after starting the application

http://localhost:8080/h2-console
```
jdbc url:   jdbc:h2:mem:testd
user name:  sa
password:   password
```

database is initialized using the schema.sql in resources directory

####  Assumptions and special cases handling

Amount values assumed as double values to support 2 decimal places precision in the future.

#### DB Column related info

##### Transaction Types

1. Credit: C
2. Debit: D
3. Owe: O

##### Owe Status 

settled flag : Y

##### RefId

this is to identify owe user id

## Txn table after all operations in the problem

```
SELECT * FROM BANK.TXN;
ID      AMOUNT  REF_ID  STATUS  TXN_ID  TYPE  USER_ID  
1	100.0	null	null	null	C	1
2	80.0	null	null	null	C	2
3	50.0	null	null	3	D	2
4	50.0	null	null	3	C	1
5	30.0	null	null	5	D	2
6	30.0	null	null	5	C	1
7	70.0	1	Y	null	O	2
8	30.0	null	null	null	C	2
9	30.0	null	null	9	D	2
10	30.0	null	null	9	C	1
11	40.0	1	Y	null	O	2
12	10.0	1	Y	null	O	2
13	100.0	null	null	null	C	2
14	10.0	null	null	14	D	2
15	10.0	null	null	14	C	1
```

# Problem Details
# Bank Command Line Interface

Develop command line interface (CLI) to simulate interaction with a retail bank. Implement the following commands:

| Command                           | Description                                                                                  |
| --------------------------------- | -------------------------------------------------------------------------------------------- |
| login `<client>`                  | Login as `client`. Creates a new client if not yet exists.                                   |
| topup `<amount>`                  | Increase logged-in client balance by `amount`.                                               |
| pay `<another_client>` `<amount>` | Pay `amount` from logged-in client to `another_client`, maybe in parts, as soon as possible. |

Your code should handle edge cases and be covered with tests.
Please document your assumptions.

## 'Pay' Command Examples

Given clients Alice and Bob with initial balances (100, 80):

| Action               | Result Balances                                        |
| -------------------- | ------------------------------------------------------ |
| Bob pays Alice 50    | (150, 30)                                              |
| Bob pays Alice 100   | (180, 0) with Bob owing 70                             |
| Bob tops up 30       | (210, 0) with Bob owing 40                             |
| Alice pays 30 to Bob | (210, 0) with Bob owing 10. Debt has further decreased |
| Bob tops up 100      | (220, 90)                                              |

## Test Session

Console output of your implementation should contain as least all of the output of the following scenario.
Feel free to add extra output as you see fit.

```text
> login Alice
Hello, Alice!
Your balance is 0.

> topup 100
Your balance is 100.

> login Bob
Hello, Bob!
Your balance is 0.

> topup 80
Your balance is 80.

> pay Alice 50
Transferred 50 to Alice.
Your balance is 30.

> pay Alice 100
Transferred 30 to Alice.
Your balance is 0.
Owing 70 to Alice.

> topup 30
Transferred 30 to Alice.
Your balance is 0.
Owing 40 to Alice.

> login Alice
Hello, Alice!
Owing 40 from Bob.
Your balance is 210.

> pay Bob 30
Owing 10 from Bob.
Your balance is 210.

> login Bob
Hello, Bob!
Your balance is 0.
Owing 10 to Alice.

> topup 100
Transferred 10 to Alice.
Your balance is 90.
```