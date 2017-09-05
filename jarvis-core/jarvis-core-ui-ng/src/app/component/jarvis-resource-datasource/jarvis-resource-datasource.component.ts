/* 
 * Copyright 2017 Yannick Roffin.
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

import { Component, Input, ViewChild, OnInit, ElementRef } from '@angular/core';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Http, Response, Headers } from '@angular/http';

import * as _ from 'lodash';

import { Router, ActivatedRoute, Params } from '@angular/router';
import { SelectItem, UIChart } from 'primeng/primeng';

import { JarvisPickerComponent } from '../../dialog/jarvis-picker/jarvis-picker.component';
import { JarvisConfigurationService } from '../../service/jarvis-configuration.service';
import { JarvisResourceLink } from '../../class/jarvis-resource-link';

import { JarvisDataMeasureService } from '../../service/jarvis-data-measure.service';
import { JarvisDataDatasourceService } from '../../service/jarvis-data-datasource.service';
import { LoggerService } from '../../service/logger.service';

/**
 * class
 */
import { JarvisResource } from '../../class/jarvis-resource';
import { NotifyCallback } from '../../class/jarvis-resource';

/**
 * data model
 */
import { ResourceBean } from '../../model/resource-bean';
import { PickerBean } from '../../model/picker-bean';
import { DataSourceBean } from '../../model/connector/datasource-bean';
import { MeasureBean } from '../../model/connector/measure-bean';

@Component({
  selector: 'app-jarvis-resource-datasource',
  templateUrl: './jarvis-resource-datasource.component.html',
  styleUrls: ['./jarvis-resource-datasource.component.css']
})
export class JarvisResourceDatasourceComponent extends JarvisResource<DataSourceBean> implements NotifyCallback<ResourceBean>, OnInit {

  @Input() myDataSource: DataSourceBean;
  @ViewChild('pickMeasures') pickMeasures: JarvisPickerComponent;
  @ViewChild('chart') chart: UIChart;

  date1: Date;

  /**
   * internal
   */
  private jarvisMeasureLink: JarvisResourceLink<MeasureBean>;
  private chartData: any;
  private beginDate: Date = new Date();
  private endDate: Date = new Date();
  private truncateDate: number = 16;
  private timeTemplate: string = "timestamp";
  private valueTemplate: string = "base";
  private delta: boolean = true;

  /**
   * constructor
   */
  constructor(
    private _http: Http,
    private _route: ActivatedRoute,
    private _router: Router,
    private _jarvisConfigurationService: JarvisConfigurationService,
    private _datasourceService: JarvisDataDatasourceService,
    private logger: LoggerService,    
    private _measureService: JarvisDataMeasureService) {
    super('/datasources', [], _datasourceService, _route, _router);
    this.jarvisMeasureLink = new JarvisResourceLink<MeasureBean>(this.logger);
  }

  /**
   * load device and related data
   */
  ngOnInit() {
    this.init(this);

    this.chartData = {
            labels: [],
            datasets: [
{
                    label: 'max',
                    data: [],
                    fill: false,
                    backgroundColor: '#FFFFFF',
                    borderColor: '#4bc0c0'
                },
                {
                    label: 'average',
                    data: [],
                    fill: false,
                    backgroundColor: '#FFFFFF',
                    borderColor: '#565656'
                }
            ]
        }
  }

  /**
   * change action
   */
  public handleChange(): void {
    this.updateBody(this.delta, this.timeTemplate, this.valueTemplate, this.beginDate, this.endDate, this.truncateDate);
  }

  /**
   * format date
   */
  private dateISODate(ldate: Date): String {
    function pad(number) {
      var r = String(number);
      if ( r.length === 1 ) {
        r = '0' + r;
      }
      return r;
    }

    return ldate.getUTCFullYear()
      + '-' + pad( ldate.getUTCMonth() + 1 )
      + '-' + pad( ldate.getUTCDate() )
      + 'T' + pad( ldate.getUTCHours() )
      + ':' + pad( ldate.getUTCMinutes() )
      + ':' + pad( ldate.getUTCSeconds() )
      + '.' + String( (ldate.getUTCMilliseconds()/1000).toFixed(3) ).slice( 2, 5 )
      + 'Z';
  }

  /**
   * change action
   */
  private updateBody(delta: boolean, timestamp: string, base: string, ldate: Date, rdate: Date, trunc: number): void {
    // fix parameters
    this.myDataSource.body = JSON.stringify(
        {
          "minDate": ldate,
          "maxDate": rdate,
          "truncate":trunc
        }
    );
    this.execute(delta);
  }

  /**
   * task action
   */
  public execute(delta: boolean): void {
    let myData = JSON.parse(this.myDataSource.body);
    let myOutputData = {}
    this._datasourceService.Task(this.myDataSource.id, 'execute', myData)
      .subscribe(
      (result: any) => this.myDataSource.resultset = result,
      error => console.log(error),
      () => {
        let labels = [];
        let avg = [];
        let max = [];
        let min = [];
        // then push in graph data
        _.each(this.myDataSource.resultset, (element) => {
          labels.push(element.label);
          if(delta) {
              max.push(element.max);
              min.push(element.min);
              avg.push(element.avg);
            } else {
              max.push(element.maxref);
              min.push(element.minref);
              avg.push(element.avgref);
            }
          });
          this.chartData.labels = labels;
          this.chartData.datasets[0].data = max;
          this.chartData.datasets[1].data = avg;
          console.log("data", this.chartData)
          this.chart.refresh();
        });
  }

  /**
   * drop link
   */
  public dropMeasureLink(linked: MeasureBean): void {
    this.jarvisMeasureLink.dropLink(linked, this.myDataSource.id, this.myDataSource.measures, this._datasourceService.allLinkedMeasures);
  }

  /**
   * goto link
   */
  public gotoMeasureLink(linked: MeasureBean): void {
    this._router.navigate(['/measures/' + linked.id]);
  }

  /**
   * notify to add new resource
   */
  public notify(picker: PickerBean, resource: ResourceBean): void {
    if( picker.action === 'measures') {
      this.jarvisMeasureLink.addLink(this.getResource().id, resource.id, this.getResource().measures, {"order": "1", href: "HREF"}, this._datasourceService.allLinkedMeasures);
    }
    if (picker.action === 'complete') {
      this.myDataSource = <DataSourceBean>resource;
      this.myDataSource.measures = [];
      (new JarvisResourceLink<MeasureBean>(this.logger)).loadLinksWithCallback(resource.id, this.myDataSource.measures, this._datasourceService.allLinkedMeasures, (measures) => {
        console.log("measures", measures);
      });
    }
  }

  /**
   * pick datasources
   */
  public pick(picker: PickerBean): void {
    /**
     * find measures
     */
    if (picker.action === 'measures') {
      this.pickMeasures.open(this, 'Measure');
    }
  }
}
