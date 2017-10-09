/* 
 * Copyright 2016 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import * as _ from 'lodash';
import { State, Store } from '@ngrx/store';

import { SelectItem, UIChart } from 'primeng/primeng';
import { JarvisMqttService } from '../../service/jarvis-mqtt.service';

import { Observable } from 'rxjs/Observable';
import { MessageBean } from '../../model/broker/message-bean';

@Component({
  selector: 'app-jarvis-broker',
  templateUrl: './jarvis-broker.component.html',
  styleUrls: ['./jarvis-broker.component.css']
})
export class JarvisBrokerComponent implements OnInit {

  @Input() myMessages: MessageBean[] = <MessageBean[]> [];
  public messageObservable: Observable<MessageBean> = new Observable<MessageBean>();

  private ids: number = 0;
  
  constructor(
    private store: Store<State<MessageBean>>,
    private mqttService: JarvisMqttService
  ) {
    /**
      * register to store
      */
      this.messageObservable = this.store.select<MessageBean>('Broker');
      /**
       * register to store update
       */
      this.messageObservable
        .filter(item => {
          if (item) {
            if(item.topic && item.body) {
              return true;
            }
            return false
          } else {
            return false
          }
        })
        .subscribe((item) => {
          let msg = new MessageBean();
          msg.id = ''+(this.ids++);
          msg.topic = item.topic;
          try {
            msg.body = JSON.stringify(item.body, null, '  ');
          } catch (Exc) {
            msg.body = item.body;
          }
          if(this.myMessages.length > 20) {
            this.myMessages.shift()
          }
          this.myMessages.push(msg);
        });
    }

  ngOnInit() {
  }

}
