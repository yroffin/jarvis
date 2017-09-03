import { TestBed, inject } from '@angular/core/testing';

import { JarvisMessageService } from './jarvis-message.service';

describe('JarvisMessageService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisMessageService]
    });
  });

  it('should be created', inject([JarvisMessageService], (service: JarvisMessageService) => {
    expect(service).toBeTruthy();
  }));
});
