import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { JarvisResourceProcessComponent } from './jarvis-resource-process.component';

describe('JarvisResourceProcessComponent', () => {
  let component: JarvisResourceProcessComponent;
  let fixture: ComponentFixture<JarvisResourceProcessComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JarvisResourceProcessComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JarvisResourceProcessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
