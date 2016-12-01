/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { JarvisResourceDeviceRenderComponent } from './jarvis-resource-device-render.component';

describe('JarvisResourceDeviceRenderComponent', () => {
  let component: JarvisResourceDeviceRenderComponent;
  let fixture: ComponentFixture<JarvisResourceDeviceRenderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JarvisResourceDeviceRenderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JarvisResourceDeviceRenderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
