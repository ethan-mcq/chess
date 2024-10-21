# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.

  [server design](https://sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZMAcygQArthgBiC8ACe8bnhjBs2ZCDlIIaPYA7gAWSGBiiKikALQAfOSUNFAAXDAA2tIAogAyWQAqWQC6MAD0xpjCyTDxrOxclGkgblAAFACUmGyc3LC1VaJNLR2VSaI1CbLySirqQyh4I1OKymqqE0Y6adl5hTAAVDAAYgBKAPIAsjBWBlCYyzNrG8bbuQVZB8fnV8BWYCH3bSPdTPLYyN57Q6nS4wMzAAC2YgeqxBMX6YxUaTIVhRGhg+I8CGYARgqhxszxBKpAxUNVq3QaqXI5LWmCpVIZvTpCSqjWZuJgJzU2ACBjZ7PxvL6tSi6DAaQATAAGJUwADeAF8ADpocX4lAIAwwEmUSx3CX45EU7mJESYmAAVhVMFaAEkMJQ0MAELbqlkoGbOhbfeM0QlOXynaq3R6oF6fVKYP7A3r2RHpTykpHnTGInHvb7KEmA9AgxbE2HXNF5Y7nWqAESI9TAMwoetpevJ6AwZoLKBINBmDxeHx+AL1jWp9AcTDT0wWay2OxeqguIVmJCqMBQMeBOyhcKRZByjZStLpAAKZzI+RKpRuRYbD-ziPb9frABoYPXcOogtAODfT9vxQeFgCQBAgMnCtw3qXo0igFANy3ShWnXTc8yFABHKw1DATp01PDEUAQpCMNQ9CUKgbDcK3ToaUMWorTWNJWzAABVW5WmfL1EU6ZjUQSF5jgHDhrluGAdBcHiETEKlvWJQIyVxVNqWIjYBNUJpELkFBONQ59ljLCVNNBNJ3VuZh9KgZZVIJBiNKBXFtIWCIFD+EJWl+f4jLsy0nOtWphIsyhmHcnztD8mQAqeSsGKxFl1CihzK3TNJKMwtQrCJKLCLirMmQyncCBJbEVODCsZWPDBFTresZNfFJ3y-etvJCfIIAAa3QKCp0NQwkAAM3EotvR0jgXBQAAPDDKRMmLUXRO0SJgAAWJUAGYXS7JkxoWCaYHkbq0GM9kUvpOC+XWrbWh2tI9uAA6jvQU6OUujNCyZa7tpLXaEHGlxnpO5KCptWUarWzb1UbNYWzbDs7sJAHDuAY6Jz6o0hskx6YEQnC8JdeFN1UAchwalAv1-VR-ygDgv27UDwIQV77PU1L3rSdbo0RvQxLx2j8Ny96bTPSHud+tJedxlB8bokHqCLStwZrLnoabVQ4fbb8eZx-m8PRqk53koljSU0HTWgKLTPy5a0ijF13TzeNPuLFMKrZi6emzaNHc9AtEx2ln8Typbkjtur1c1hGJZgAB9VoODUEB+2wErAggYaLagdpY4N8tQaV6qa3thtI9bLXOxj+PE9UZPiDT41M9+nO8-1ThZ3b8xLBsexoHYVsYByCANz3A8IkwZWiIVplL2vW8ygMdR-ECJ9bl4+Hmu-KmacApr0ZguovaZBBh4Hbi19kyn9GpgCCOF0PBhgE+R-Pv3ESvv9b9GZbHOmZyPA8nKQgulrKvxfBTGA28v7WyEmCMgbw4D5BGuAj+N9aZfBhM+VMClTakkSnNdk1sH72m3LhF0bUgG+AbspCkQcQy0iYgtLSPYdJuQ8mA9edCYGbHMmgSyyD15fjah1Y6MBXQADl8hnAAf8K2TCRbEXuh5ER6B5ZUFDJ7RkSj-gqN1MGEOmZp5pCHiPfkFI1GKyqtWWqqpV5vw3sBVqyiuo9T3pOQ2-UYBYwoRgYBDdBpM3WK0AcVBvRIDEuTY0sAoG0y4fIm26j7TrQAIw-TNGkKwXoPLQCQAALxQBwOh51YJH05kqVJt0Y6ZLajk-JhShZHwUUYyGFTEbVOyf2OpRSC5WLlGU1JpdYbl2juk64WT-i1IKa3GARsCQ4JJLcaoWc5F-0CsQla9tcx+wTKDQOaiNElK0bWH2sZnYB2bg0xkTSw7HIdqc-2uyLnu2nmDIu4dbEw2bMM7WVcE5JxTg3DOMzm653cQSOcc4u6LnsBYFA6BB7D2sMwfcYRx6T1DnyTIEJigLzWMvchzjREDhgCEfalBVD0QLoc+CT9EV-C8oSl6XR76GMSStZ+SKGU6JccDFKkwmFpETtwNh-wuXtR5fxeJsDXi7A+NCH4HkYAAHUWBZBOB8YRPKYAAF51SauOmC-E8ylL4JWSsNZrLH5lXMc8tlNo0pmNZPo++GKmQmKRY6pKtrLEJGVjY9UhqZmeO8YywIm4vFoFCcgMS3Y0AQGYINaw7dgxEMtUk8paToAZPGSESZ9TbUHMPkclJmamTtImZ0gpdCDGfX6aW7NNTK35vzi8wu1iWlqyGfDH5ozy25qbdM2ZRqTYLPNr9M1wJ1gJMfps32+YdkvL2QWhhmiaWzvuQuv0TyLQ1tFuup2DzF3bolJVX1bzbmDK+d2yuozq7-PrvioFWcW6BshZChcPc7CITEjkDCMAADisl1gosPBPIuU8bnpH-QUO8cJEQEu5USwIpLHrkspa26lfJkBbkA02MVui76NPWWkbDYBcNqHwxK7+drGGrJYrCFAYBwqeX1Uy7hwl4F5EQQI2SGCFX-GVaq9VMjxWiN1WqVjaBA3GrweVC0qb6ErXJvshh-K6NzCfhhcjqgOGyTiepqd0ryAIKQXBlArppBfjHnpC+79JIIFAJ1aygjYSyXEbx+Vrn4MqrVR8azznePifJhqGAZwTj2ccwF+DQXbMoEDVSBT8UMhqjMxZqzqKbP2K-DoBzIAnOxa-GZ9ziINRfgAHQVaKCpxiq6+S-q3I3LzahLlcgSXyZUHyzOqC1ukFLsk0swH8wViLeWosQKK7JUrMAKtlaKPF1mrbekQw69DLrPW+uIgG0NrLI38s7YmyV8rlXX3BuGpJrx6wQlhJnCm+JxGO2VN7TmvN3Sf7s1KQ9tpz2m3Vpdayq6GbHtZrGY2vJVaLEfT9R2y9Gtvk3uB32vNg7k1zJHWbF5yzbsGeuTOnMc6zmPLdi2mjtWmT7u2S7JdO6-u1tuVs+dlPj1nR6We9tJdPmw+vYjO9tcAWPqboGUFU5O7vu7kubAVgoDeEMHAVhhhyPBAy2B6sEHMVXhvLB3jDYDsOJC5RpDJKyVQApdRxWmGmTJ1cigcj+v0CFbc3p5lRG00rUt7pG3kn7eImKygdDNG1Pmvo2xZjtvgbsbgSZnj8HPNtUE75kTuidV6tDdJtHsmbXybuy7jJsXqu-0Dxpt3EQbepcs4NjLY3su5b2ygprPv9MF8MzwsR4j4EnFM-1sv23a85ci8NnXLepFNYnQKad9pS95-e0chXReCktZ9bThXFnpaqBFHwuSxOF9Q5W9rzvWtkkKg2qtE7mMzuhouxGqN4SokwDjQmpNN3M-Y7HytEtQOy3fbB82k9Htzd1vfw2h0l-r9kRq6v-l9qDl0hDq8u2iWjDlHD2gjp-nUoOqdtjHzDLALC6FdtGjfkTEvIOHXo7sujVn-mLPWugdLLLILM6qAf9l9DmDrBgdQa9tUG2n0uQfAXDkwVQQLMjo-sOopKSGOmaCPhaopu8ncgepukWFTj-m9qTpIfTgTkekThKLugVEofjoelumoczotqzhwezmXFzr8jXHXKnPzsCoLvwaYKLtCnYE4AaCfEEDAAAFIQDEoK52C955bK4nhgEZAXjsTzylBmbQzeDOCUBwAQAnxQBvgqquiFClAABCOQCgcAAA0o4qXvvofsfghqJvCsSihonMbn7mbkWjSgAFaeFoAe6hqUwOZOFQAxFxFe7mbSCEZXL3a1EDgNGIZ26QLNHRGxHQAdEWZ+6hgB6TqsSMYh6SaSrP5GacZZDcaRIx6Ko+bCbnbiaSap7MAhqDFhqXaRrXY3534wCJqZICGEJZ4SGfZVLIHg4kH2ocyPFPaQEvHU50G05v4QFAFQHeqQ7npwEc4IHw4f5fGAQn4DTDRSx6wNbBJnG4Hdj4EkyEGREtFtHQBhGd6sGFoOqqwAGUGIk0E-E9GBHEk8FkmsFb6gkRxdoVw0mYH6ywleLDThEPRPSoyqKvG0aN5zFkayStCl4N6TpmTGZcYd6bZd4V7Da+E14uYD6ebhHbEfCl5J4badHzaCEcnDFRGtFjGwDhrckuDXxIBmBoBz7BgJb3FJbfQklmkozHR0JqQKFkGOmIzOlAxukEgaHNJekxw+m8nAy2kLZsFLYqxQxcGmGjIhloy6lBqn7p5OrhnRTP73bhFaDyBRTun+4ZmCnXDYA5mGBYmUAADk6wIAxpUeG+4Z4eaQ7EF40gCgew4R8CSCao5ZRpcRGoY2WpwWcewmmp4mpeSZdpmZ2enqBC-JihM5eZ-pNOos1qTwQoq+oo9Ztpp6VYHBO+SZQ6hIQhiyRYmOT+jeOO9o5ODO5yeh+ZhJ7x15KhuhpY8+H0e6eOG6jOd5EZ9JbOjJV6zJZh96lhJIT6IKthEK7cQAA)
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
