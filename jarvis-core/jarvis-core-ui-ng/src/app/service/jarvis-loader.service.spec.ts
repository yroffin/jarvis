import { TestBed, inject } from '@angular/core/testing';

import { JarvisLoaderService } from './jarvis-loader.service';

describe('JarvisLoaderService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisLoaderService]
    });
  });

  it('should be created', inject([JarvisLoaderService], (service: JarvisLoaderService) => {
    expect(service).toBeTruthy();
  }));
});
