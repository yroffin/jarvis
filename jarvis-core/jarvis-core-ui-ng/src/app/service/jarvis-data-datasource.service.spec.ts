/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { JarvisDataDatasourceService } from './jarvis-data-datasource.service';

describe('JarvisDataDatasourceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisDataDatasourceService]
    });
  });

  it('should ...', inject([JarvisDataDatasourceService], (service: JarvisDataDatasourceService) => {
    expect(service).toBeTruthy();
  }));
});
