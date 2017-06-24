import { Injectable } from '@angular/core';
import { State, Store } from '@ngrx/store';

import { Paho } from 'ng2-mqtt/mqttws31';

@Injectable()
export class JarvisMqttService {
  private _client: Paho.MQTT.Client;

  constructor(
    private store: Store<State<any>>
  ) {
    this._client = new Paho.MQTT.Client("192.168.1.12", 8000, "clientId-front");

    this._client.onConnectionLost = (responseObject: Object) => {
      console.log('Connection lost.', responseObject);
    };

    this._client.onMessageArrived = (message: Paho.MQTT.Message) => {
      this.store.dispatch({
        type: 'message',
        payload: {
          topic: message.destinationName,
          body: message.payloadString
        }
      });
    };

    this._client.connect({
      onSuccess: this.onConnected.bind(this)
    });
  }

  private onConnected(): void {
    console.log('Connected to broker.');
    this._client.subscribe("#", {});
  }
}
