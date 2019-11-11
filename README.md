# Sensu Go to Sensu Core Gateway

To enable a step by step migration from Sensu Core to Sensu Co this application
allows to forward events from Sensu Go to Sensu Core.

## Build

* Create distributions using `./gradlew distTar distZip`
* The distributions will be saved in `build/distributions`

## Run

* run `./bin/sensu-gateway` in an extracted distribution
* run `./gradlew run` directly

## Configuration

### Sensu Go

Register the handler:

`sensuctl handler create sensu-gateway --socket-host gateway-hostname --socket-port 4446 -t udp`

### Sensu Core

Allow the gateway to connect to the Sensu Core socket.

If it's not on the same machine, you will have to change the bind parameter:

**Example: listen all addresses**
```json
{
  "client": {   
    "socket": {
      "bind": "0.0.0.0"
    }
  }
}
```

### SensuGateway

Create the file `app.properties` where you run the appliction to override the defaults.

| property | default | description |
|----------|---------|-------------|
| core.to-go-enabled | false | Enable Sensu Core to Go processing |
| core.receiver.port | 4445 | Listening UDP Port for Sensu Core events |
| core.target.host | localhost | Hostname of Sensu Core client socket |
| core.target.port | 3030 | Port of Sensu Core client socket |
| go.to-core-enabled | false | Enable Sensu Go to Core processing |
| go.receiver.port | 4446 | Listening UDP Port for Sensu Go events |
| go.api.base | `null` | Sensu Go API URL base |
| go.api.username | `null` | Sensu Go API Username |
| go.api.password | `null` | Sensu Go API Password |

For permanent/bundled settings, you might change `src/main/resources/app.properties`