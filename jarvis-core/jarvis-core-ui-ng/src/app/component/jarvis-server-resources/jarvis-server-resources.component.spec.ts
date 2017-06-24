import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { JarvisServerResourcesComponent } from './jarvis-server-resources.component';

describe('JarvisServerResourcesComponent', () => {
  let component: JarvisServerResourcesComponent;
  let fixture: ComponentFixture<JarvisServerResourcesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JarvisServerResourcesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JarvisServerResourcesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
