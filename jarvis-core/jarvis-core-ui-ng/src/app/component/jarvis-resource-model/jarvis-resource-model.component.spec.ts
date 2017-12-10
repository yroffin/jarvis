import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { JarvisResourceModelComponent } from './jarvis-resource-model.component';

describe('JarvisResourceModelComponent', () => {
  let component: JarvisResourceModelComponent;
  let fixture: ComponentFixture<JarvisResourceModelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JarvisResourceModelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JarvisResourceModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
