import { Injectable } from '@angular/core';
import { State, Store } from '@ngrx/store';

import { Paho } from 'ng2-mqtt/mqttws31';
import { WindowRef } from '../service/jarvis-utils.service';
import { LoggerService } from '../service/logger.service';

@Injectable()
export class JarvisMqttService {
  private _client: Paho.MQTT.Client;

  constructor(
    private store: Store<State<any>>,
    private window: WindowRef,
    private logger: LoggerService
  ) {
    this._client = new Paho.MQTT.Client(this.window.getHostname(), 8000, "clientId-front");

    let that = this;
    this._client.onConnectionLost = (responseObject: Object) => {
      that.logger.error('Connection lost.', responseObject);
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
    this.logger.info('Connected to broker and subscribe for #');
    this._client.subscribe("#", {});
  }
}
