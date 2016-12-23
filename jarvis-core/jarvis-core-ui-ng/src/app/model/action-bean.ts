/**
 * default task model
 */
export class TaskBean {
    public label: string;
    public icon: string;
    public action: string;
    public task: () => void;
    public args: any;
}

/**
 * default picker model
 */
export class PickerTaskBean {
    public label: string;
    public icon: string;
    public action: string;
    public picker: any;
}

