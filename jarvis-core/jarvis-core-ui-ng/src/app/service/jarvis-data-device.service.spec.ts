/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { JarvisDataDeviceService } from './jarvis-data-device.service';

describe('JarvisDataDeviceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisDataDeviceService]
    });
  });

  it('should ...', inject([JarvisDataDeviceService], (service: JarvisDataDeviceService) => {
    expect(service).toBeTruthy();
  }));
});
