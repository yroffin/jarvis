/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { JarvisResourceDevicePluginComponent } from './jarvis-resource-device-plugin.component';

describe('JarvisResourceDevicePluginComponent', () => {
  let component: JarvisResourceDevicePluginComponent;
  let fixture: ComponentFixture<JarvisResourceDevicePluginComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JarvisResourceDevicePluginComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JarvisResourceDevicePluginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
