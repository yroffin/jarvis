/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { JarvisResourceSnapshotComponent } from './jarvis-resource-snapshot.component';

describe('JarvisResourceSnapshotComponent', () => {
  let component: JarvisResourceSnapshotComponent;
  let fixture: ComponentFixture<JarvisResourceSnapshotComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JarvisResourceSnapshotComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JarvisResourceSnapshotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
