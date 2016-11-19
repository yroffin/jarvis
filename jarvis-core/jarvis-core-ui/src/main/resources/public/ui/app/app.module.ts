import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { NameComponent } from './name.component';
import { OtherNameComponent } from './other-name.component';
import { NameService } from './name.service';
import {ButtonDemo} from './button/button-demo';

@NgModule({
  imports: [BrowserModule],
  declarations: [AppComponent, NameComponent, OtherNameComponent, ButtonDemo],
  providers: [NameService],
  bootstrap: [AppComponent]
})
export class AppModule {}

/*
Copyright 2016 thoughtram GmbH. All Rights Reserved.
Use of this source code is governed by an TTML-style license that
can be found in the license.txt file at http://thoughtram.io/license.txt
*/