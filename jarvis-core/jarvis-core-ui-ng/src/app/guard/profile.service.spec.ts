import { TestBed, inject } from '@angular/core/testing';

import { ProfileGuard } from './profile.service';

describe('ProfileService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ProfileGuard]
    });
  });

  it('should be created', inject([ProfileGuard], (service: ProfileGuard) => {
    expect(service).toBeTruthy();
  }));
});
