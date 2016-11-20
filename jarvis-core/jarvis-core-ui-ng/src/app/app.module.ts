import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { JarvisDlgComponentComponent } from './jarvis-dlg-component/jarvis-dlg-component.component';
import { MdButtonModule } from '@angular2-material/button';

@NgModule({
  declarations: [
    AppComponent,
    JarvisDlgComponentComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    MdButtonModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
