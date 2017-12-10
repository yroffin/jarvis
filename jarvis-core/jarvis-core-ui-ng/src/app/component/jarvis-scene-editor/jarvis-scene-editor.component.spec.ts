import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { JarvisSceneEditorComponent } from './jarvis-scene-editor.component';

describe('JarvisSceneEditorComponent', () => {
  let component: JarvisSceneEditorComponent;
  let fixture: ComponentFixture<JarvisSceneEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JarvisSceneEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JarvisSceneEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
