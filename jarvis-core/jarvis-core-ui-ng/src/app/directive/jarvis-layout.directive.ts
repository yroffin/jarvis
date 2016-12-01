import { Directive, Input, HostBinding } from '@angular/core';

@Directive({
  selector: '[layout]'
})
export class JarvisLayoutDirective {
  @Input() layout: string;
  @HostBinding('style.display') display = 'flex';

  @HostBinding('style.flex-direction')
  get direction() {
    return (this.layout === 'column') ? 'column' : 'row';
  }
}
