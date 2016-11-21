import 'hammerjs';

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';

import { MdCoreModule } from '@angular2-material/core';
import { MdButtonModule } from '@angular2-material/button';
import { MdInputModule } from '@angular2-material/input';
import { MdCardModule } from '@angular2-material/card';
import { MdToolbarModule } from '@angular2-material/toolbar';
import { MdCheckboxModule } from '@angular2-material/checkbox';
import { MdListModule } from '@angular2-material/list';
import { MdIconModule, MdIconRegistry } from '@angular2-material/icon';

import { JarvisDialogComponent } from './jarvis-dialog/jarvis-dialog.component';
import { JarvisConfigurationService } from './service/jarvis-configuration.service';
import { JarvisDataDeviceService } from './service/jarvis-data-device.service';

@NgModule({
  declarations: [
    AppComponent,
    JarvisDialogComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    MdCoreModule,
    MdButtonModule,
    MdCardModule,
    MdToolbarModule,
    MdInputModule,
    MdCheckboxModule,
    MdListModule,
    MdIconModule
  ],
  providers: [
    MdIconRegistry,
    JarvisConfigurationService,
    JarvisDataDeviceService],
  bootstrap: [AppComponent]
})
export class AppModule { }
