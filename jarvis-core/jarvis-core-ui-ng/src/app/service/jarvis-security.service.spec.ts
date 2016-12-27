/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { JarvisSecurityService } from './jarvis-security.service';

describe('JarvisSecurityService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisSecurityService]
    });
  });

  it('should ...', inject([JarvisSecurityService], (service: JarvisSecurityService) => {
    expect(service).toBeTruthy();
  }));
});
