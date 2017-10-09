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

import { MessageBean } from '../model/broker/message-bean';

export const message = 'message';

/**
 * main store for this application
 */
export class BrokerStore {

    /**
     * slides reducer
     * @param state 
     * @param action 
     */
    public static brokerReducer(state: MessageBean = new MessageBean(), action: Action): MessageBean {
        switch (action.type) {
            /**
             * message incomming
             */
            case message:
                {
                    let newState = new MessageBean();
                    newState.topic = action.payload.topic;
                    try {
                        newState.body = JSON.parse(action.payload.body);
                    } catch(Exc) {
                        newState.body = action.payload.body;
                    }
                    return newState;
                }

            default:
                return state;
        }
    }

}