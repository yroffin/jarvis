import { ResourceBean } from './resource-bean';
import { NotificationBean } from './notification-bean';

export class CommandBean extends ResourceBean {
    body: string;
    notifications: NotificationBean[];
}
