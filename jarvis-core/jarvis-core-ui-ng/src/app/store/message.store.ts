/* 
 * Copyright 2017 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ActionReducer, Action, State } from '@ngrx/store';
import * as _ from 'lodash';

import { Message } from 'primeng/primeng';

export const push = 'push';

/**
 * main store for this application
 */
export class MessageStore {

    /**
     * slides reducer
     * @param state 
     * @param action 
     */
    public static messageReducer(state: Message = <Message> {}, action: Action): Message {
        switch (action.type) {
            /**
             * message incomming
             */
            case push:
                {
                    let newState = <Message> {};
                    newState.id = action.payload.id;
                    newState.severity = action.payload.severity;
                    newState.summary = action.payload.summary;
                    newState.detail = action.payload.detail;
                    return newState;
                }

            default:
                return state;
        }
    }

}