/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { JarvisToolbarResourceComponent } from './jarvis-toolbar-resource.component';

describe('JarvisToolbarResourceComponent', () => {
  let component: JarvisToolbarResourceComponent;
  let fixture: ComponentFixture<JarvisToolbarResourceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JarvisToolbarResourceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JarvisToolbarResourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
