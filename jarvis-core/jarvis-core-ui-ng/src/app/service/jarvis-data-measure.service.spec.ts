/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { JarvisDataMeasureService } from './jarvis-data-measure.service';

describe('JarvisDataMeasureService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisDataMeasureService]
    });
  });

  it('should ...', inject([JarvisDataMeasureService], (service: JarvisDataMeasureService) => {
    expect(service).toBeTruthy();
  }));
});
