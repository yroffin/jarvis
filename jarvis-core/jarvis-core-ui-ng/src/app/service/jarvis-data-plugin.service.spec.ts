/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { JarvisDataPluginService } from './jarvis-data-plugin.service';

describe('JarvisDataPluginService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JarvisDataPluginService]
    });
  });

  it('should ...', inject([JarvisDataPluginService], (service: JarvisDataPluginService) => {
    expect(service).toBeTruthy();
  }));
});
