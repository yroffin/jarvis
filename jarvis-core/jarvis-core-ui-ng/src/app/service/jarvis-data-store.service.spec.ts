/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { JarvisDataStoreService } from './jarvis-data-store.service';

describe('JarvisDataStoreService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisDataStoreService]
    });
  });

  it('should ...', inject([JarvisDataStoreService], (service: JarvisDataStoreService) => {
    expect(service).toBeTruthy();
  }));
});
