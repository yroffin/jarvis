import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { JarvisBrokerComponent } from './jarvis-broker.component';

describe('JarvisBrokerComponent', () => {
  let component: JarvisBrokerComponent;
  let fixture: ComponentFixture<JarvisBrokerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JarvisBrokerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JarvisBrokerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
