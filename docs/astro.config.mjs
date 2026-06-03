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
