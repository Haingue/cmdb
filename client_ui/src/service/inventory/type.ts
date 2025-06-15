export interface StrapiResponse {
    data: StrapiResult[];
    meta: Meta;
}

export interface Meta {
    pagination: Pagination;
}

export interface Pagination {
    page:      number;
    pageSize:  number;
    pageCount: number;
    total:     number;
}

export interface StrapiResult {
    id:               number;
    documentId:       string;
    kind:           string;
    collectionName: string;
    info:           Info;
    options:        Options;
    pluginOptions:  PluginOptions;
    attributes:     Project;
}

export interface Project {
    shortName:        Description;
    fullName:         Description;
    business_service: BusinessService;
    Description:      Description;
    environments:     BusinessService;
    maintainers:      BusinessService;
    owners:           BusinessService;
}

export interface Description {
    type: string;
}

export interface BusinessService {
    type:        string;
    relation:    string;
    target:      string;
    inversedBy?: string;
    mappedBy?:   string;
}

export interface Info {
    singularName: string;
    pluralName:   string;
    displayName:  string;
}

export interface Options {
    draftAndPublish: boolean;
}

export interface PluginOptions {
}
