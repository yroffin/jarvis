/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { JarvisDataTriggerService } from './jarvis-data-trigger.service';

describe('JarvisDataTriggerService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisDataTriggerService]
    });
  });

  it('should ...', inject([JarvisDataTriggerService], (service: JarvisDataTriggerService) => {
    expect(service).toBeTruthy();
  }));
});
