import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BottommenuComponent } from './bottommenu.component';

describe('CenterLayoutComponent', () => {
  let component: BottommenuComponent;
  let fixture: ComponentFixture<BottommenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BottommenuComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BottommenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
