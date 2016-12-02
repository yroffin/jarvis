/**
 * default class to handle default behaviour or resource
 * component
 */
export class JarvisResource {
    private state: number = 0;
    private maxState: number = 0;

    constructor(maxState: number) {
        this.maxState = maxState;
    }

    /**
     * retrieve current state
     */
    private getState(): number {
        return this.state;
    }

    /**
     * go to next state
     */
    private next(): number {
        this.state++;
        if (this.state > this.maxState) {
            this.state = 0;
        }
        return this.state;
    }
}
