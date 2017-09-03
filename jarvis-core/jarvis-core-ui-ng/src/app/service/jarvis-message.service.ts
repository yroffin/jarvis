import { Injectable } from '@angular/core';
import { State, Store } from '@ngrx/store';
import { Message } from 'primeng/primeng';

@Injectable()
export class JarvisMessageService {

  constructor(
    private store: Store<State<any>>
  ) {

  }

  /**
   * push a new message
   * @param message
   */
  public push(message: Message): void {
    this.store.dispatch({
      type: 'push',
      payload: {
        id: message.id,
        detail: message.detail,
        summary: message.summary,
        severity: message.severity
      }
      });
    }
}
