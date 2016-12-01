import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import 'rxjs/add/operator/map'
import { Observable } from 'rxjs/Observable';
import { JarvisConfigurationService } from './jarvis-configuration.service';

/**
 * data model
 */
import { DeviceBean } from './../model/device-bean';
import { TriggerBean } from './../model/trigger-bean';
import { ScriptBean } from './../model/script-bean';

@Injectable()
export class JarvisDataDeviceService {

    private actionUrl: string;
    private headers: Headers;

    constructor(private _http: Http, private _configuration: JarvisConfigurationService) {
        this.actionUrl = _configuration.ServerWithApiUrl + 'devices';

        this.headers = new Headers();
        this.headers.append('Content-Type', 'application/json');
        this.headers.append('Accept', 'application/json');
    }

    /**
     * execute remote task on this device
     */
    public Task = (id: string, task: string): Observable<any> => {
        return this._http.post(this.actionUrl + '/' + id + '?task=' + task, {})
            .map((response: Response) => <any>response.json())
            .catch(this.handleError);
    }

    public GetAll = (): Observable<DeviceBean[]> => {
        return this._http.get(this.actionUrl)
            .map((response: Response) => <DeviceBean[]>response.json())
            .catch(this.handleError);
    }

    public GetSingle = (id: string): Observable<DeviceBean> => {
        return this._http.get(this.actionUrl + '/' + id)
            .map((response: Response) => <DeviceBean>response.json())
            .catch(this.handleError);
    }

    public Add = (itemName: string): Observable<DeviceBean> => {
        let toAdd = JSON.stringify({ ItemName: itemName });

        return this._http.post(this.actionUrl, toAdd, { headers: this.headers })
            .map((response: Response) => <DeviceBean>response.json())
            .catch(this.handleError);
    }

    public Update = (id: string, itemToUpdate: DeviceBean): Observable<DeviceBean> => {
        return this._http.put(this.actionUrl + id, JSON.stringify(itemToUpdate), { headers: this.headers })
            .map((response: Response) => <DeviceBean>response.json())
            .catch(this.handleError);
    }

    public Delete = (id: string): Observable<Response> => {
        return this._http.delete(this.actionUrl + id)
            .catch(this.handleError);
    }

    public GetAllLinkedTrigger = (id: string): Observable<TriggerBean[]> => {
        return this._http.get(this.actionUrl + '/' + id + '/triggers')
            .map((response: Response) => <TriggerBean[]>response.json())
            .catch(this.handleError);
    }

    public GetAllLinkedDevice = (id: string): Observable<DeviceBean[]> => {
        return this._http.get(this.actionUrl + '/' + id + '/devices')
            .map((response: Response) => <DeviceBean[]>response.json())
            .catch(this.handleError);
    }

    public GetAllLinkedPluginScript = (id: string): Observable<ScriptBean[]> => {
        return this._http.get(this.actionUrl + '/' + id + '/plugins/scripts')
            .map((response: Response) => <ScriptBean[]>response.json())
            .catch(this.handleError);
    }

    private handleError(error: Response) {
        console.error(error);
        return Observable.throw(error.json().error || 'Server error');
    }
}
