/**
 * default me bean
 */
export class MeBeanAttr {
    public email: string;
    public token: string;
    public roles: any;
    public permissions: any;
    public location: string;
    public displayName: string;
    public gender: string;
    public firstName: string;
    public pictureUrl: string;
    public locale: string;
    public username: string;
    public familyName: string;
    public typedId: string;
    public remembered: string;
}

export class MeBean {
    public id: string;
    public attributes: MeBeanAttr;
}

/**
 * default oauth2
 */
export class Oauth2Bean {
    public key: string;
    public url: string;
    public redirect: string;
    public type: string;
}

