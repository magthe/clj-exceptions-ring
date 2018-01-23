# simple-exceptions

An example of how to use `ex-info` to achieve early exits due to pre-requisites not be fulfilled or other types of interaction errors.

Furthermore Java's oridinary exceptions are caught and put into responses with a JSON body.

## Prerequisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.0.0 or above installed.

## Running

To start a web server for the application, run:

    lein ring server-headless

Interact using the command line, e.g. `curl`.

### `/div`

    curl http://localhost:3000/div -X POST -i \
        -H 'Content-Type: application/json' \
        -d '{"dividend":4, "divisor":2}'
        
### `/throw-excption`

    curl http://localhost:3000/throw-exception -X POST -i \
        -H 'Content-Type: application/json' \
        -d '{"throw": "false"}'

## License

Copyright © 2018 Zimpler AB
