import { PickerBean } from '../model/picker-bean';

export interface JarvisToolbarAction {
    close(): void;
    save(): void;
    remove(): void;
    duplicate(): void;
    next(): void;
    task(task: string): void;
    pick(picker: PickerBean): void;
}
