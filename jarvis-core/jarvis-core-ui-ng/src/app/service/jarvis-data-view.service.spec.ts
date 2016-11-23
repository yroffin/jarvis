/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { JarvisDataViewService } from './jarvis-data-view.service';

describe('JarvisDataViewService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisDataViewService]
    });
  });

  it('should ...', inject([JarvisDataViewService], (service: JarvisDataViewService) => {
    expect(service).toBeTruthy();
  }));
});
