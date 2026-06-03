// @ts-check
import { defineConfig } from 'astro/config'
import starlight from '@astrojs/starlight'

// https://astro.build/config
export default defineConfig({
  integrations: [
    starlight({
      title: 'CMDB Docs',
      description: 'Configuration Management Database Documentation',
      logo: {
        src: './src/assets/cmdb-logo.svg',
        alt: 'CMDB Logo',
      },
      social: [
        {
          icon: 'github',
          label: 'GitHub',
          href: 'https://github.com/Haingue/cmdb',
        },
      ],
      sidebar: [
        {
          label: 'Overview',
          items: [
            { label: 'Home', slug: '' },
            { label: 'Getting Started', slug: 'getting-started' },
          ],
        },
        {
          label: 'Architecture',
          items: [
            { label: 'Architecture Overview', slug: 'architecture' },
            { label: 'API Reference', slug: 'api' },
          ],
        },
        {
          label: 'CORE Domain',
          items: [
            { label: 'CORE Overview', slug: 'core' },
            { label: 'Business Logic Catalog', slug: 'core/core-business-logic-catalog' },
            { label: 'Domain Business Logic', slug: 'core/core-domain-logic' },
            { label: 'Validation Chains', slug: 'core/core-validation-chains' },
            { label: 'Event Management', slug: 'core/core-event-management' },
            { label: 'Business Rules', slug: 'core/core-business-rules' },
            { label: 'Implementation Guide', slug: 'core/core-implementation-guide' },
          ],
        },
        {
          label: 'Contributing',
          items: [
            { label: 'Contribution Guide', slug: 'contributing' },
          ],
        },
      ],
      lastUpdated: true,
    }),
  ],
})
