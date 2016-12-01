/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { JarvisResourceDeviceGeneralComponent } from './jarvis-resource-device-general.component';

describe('JarvisResourceDeviceGeneralComponent', () => {
  let component: JarvisResourceDeviceGeneralComponent;
  let fixture: ComponentFixture<JarvisResourceDeviceGeneralComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JarvisResourceDeviceGeneralComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JarvisResourceDeviceGeneralComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
