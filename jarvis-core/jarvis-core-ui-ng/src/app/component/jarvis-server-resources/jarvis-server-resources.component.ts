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

import { Component, OnInit, ViewChild } from '@angular/core';
import { State, Store } from '@ngrx/store';

import { SelectItem, UIChart } from 'primeng/primeng';
import { JarvisMqttService } from '../../service/jarvis-mqtt.service';

import { Observable } from 'rxjs/Observable';
import { MessageBean } from '../../model/broker/message-bean';

@Component({
  selector: 'app-jarvis-server-resources',
  templateUrl: './jarvis-server-resources.component.html',
  styleUrls: ['./jarvis-server-resources.component.css']
})
export class JarvisServerResourcesComponent implements OnInit {

  public data: any;
  @ViewChild('chart') chart: UIChart;
  public messageObservable: Observable<MessageBean> = new Observable<MessageBean>();

  constructor(
    private store: Store<State<MessageBean>>,
    private mqttService: JarvisMqttService
  ) {
    this.data = {
      labels: ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'],
      datasets: [
        {
          label: 'committedVirtualMemorySize',
          data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
          fill: false,
          borderColor: '#4bc0c0'
        },
        {
          label: 'freePhysicalMemorySize',
          data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
          fill: false,
          borderColor: '#4bc0c0'
        },
        {
          label: 'freeSwapSpaceSize',
          data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
          fill: false,
          borderColor: '#4bc0c0'
        },
        {
          label: 'processCpuLoad',
          data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
          fill: false,
          borderColor: '#4bc0c0'
        },
        {
          label: 'processCpuTime',
          data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
          fill: false,
          borderColor: '#4bc0c0'
        },
        {
          label: 'systemCpuLoad',
          data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
          fill: false,
          borderColor: '#4bc0c0'
        },
        {
          label: 'totalPhysicalMemorySize',
          data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
          fill: false,
          borderColor: '#4bc0c0'
        },
        {
          label: 'totalSwapSpaceSize',
          data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
          fill: false,
          borderColor: '#4bc0c0'
        }
      ]
    }

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
          if(item.body && item.body.classname === "SystemIndicator") {
            return true;
          }
          return false
        } else {
          return false
        }
      })
      .subscribe((item) => {
        this.data.datasets[0].data.push(item.body.data.committedVirtualMemorySize);
        this.data.datasets[0].data.shift();
        this.data.datasets[1].data.push(item.body.data.freePhysicalMemorySize);
        this.data.datasets[1].data.shift();
        this.data.datasets[2].data.push(item.body.data.freeSwapSpaceSize);
        this.data.datasets[2].data.shift();
        this.data.datasets[3].data.push(item.body.data.processCpuLoad);
        this.data.datasets[3].data.shift();
        this.data.datasets[4].data.push(item.body.data.processCpuTime);
        this.data.datasets[4].data.shift();
        this.data.datasets[5].data.push(item.body.data.systemCpuLoad);
        this.data.datasets[5].data.shift();
        this.data.datasets[6].data.push(item.body.data.totalPhysicalMemorySize);
        this.data.datasets[6].data.shift();
        this.data.datasets[7].data.push(item.body.data.totalSwapSpaceSize);
        this.data.datasets[7].data.shift();

        this.chart.refresh();
      });
  }

  ngOnInit() {
  }

}
