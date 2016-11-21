/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { JarvisConfigurationService } from './jarvis-configuration.service';

describe('JarvisConfigurationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisConfigurationService]
    });
  });

  it('should ...', inject([JarvisConfigurationService], (service: JarvisConfigurationService) => {
    expect(service).toBeTruthy();
  }));
});
