import type { Schema, Struct } from '@strapi/strapi';

export interface NetworkIp extends Struct.ComponentSchema {
  collectionName: 'components_network_ips';
  info: {
    displayName: 'IP';
    icon: 'paperPlane';
  };
  attributes: {
    fisrtNumber: Schema.Attribute.Integer &
      Schema.Attribute.SetMinMax<
        {
          max: 255;
          min: 0;
        },
        number
      > &
      Schema.Attribute.DefaultTo<0>;
    fourthNumber: Schema.Attribute.Integer &
      Schema.Attribute.SetMinMax<
        {
          max: 255;
          min: 0;
        },
        number
      > &
      Schema.Attribute.DefaultTo<0>;
    secondeNumber: Schema.Attribute.Integer &
      Schema.Attribute.SetMinMax<
        {
          max: 255;
          min: 0;
        },
        number
      > &
      Schema.Attribute.DefaultTo<0>;
    thirdNumber: Schema.Attribute.Integer &
      Schema.Attribute.SetMinMax<
        {
          max: 255;
          min: 0;
        },
        number
      > &
      Schema.Attribute.DefaultTo<0>;
  };
}

export interface NetworkVlan extends Struct.ComponentSchema {
  collectionName: 'components_network_vlans';
  info: {
    displayName: 'Vlan';
    icon: 'grid';
  };
  attributes: {
    label: Schema.Attribute.String;
    networkArea: Schema.Attribute.Enumeration<['IT']>;
    number: Schema.Attribute.Integer;
  };
}

export interface TechnologyVersion extends Struct.ComponentSchema {
  collectionName: 'components_technology_versions';
  info: {
    displayName: 'Version';
    icon: 'hashtag';
  };
  attributes: {
    major: Schema.Attribute.Integer;
    minor: Schema.Attribute.Integer;
    patch: Schema.Attribute.Integer;
  };
}

declare module '@strapi/strapi' {
  export module Public {
    export interface ComponentSchemas {
      'network.ip': NetworkIp;
      'network.vlan': NetworkVlan;
      'technology.version': TechnologyVersion;
    }
  }
}
