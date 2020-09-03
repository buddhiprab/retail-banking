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