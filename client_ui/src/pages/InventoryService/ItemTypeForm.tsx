import React from 'react'
import type { ItemTypeDto } from '../../service/inventory/types'
import PageTitle from '../../components/PageTitle'

const ItemTypeForm = () => {
  const [itemType, setItemType] = React.useState<ItemTypeDto>({
    label: '',
    description: '',
    attributes: [],
  })

  const updateItemTypeField = (field: keyof ItemTypeDto, value: string) => {
    setItemType({
      ...itemType,
      [field]: value,
    })
  }

  return (
    <>
      <PageTitle title="Create new Item Type" />
      <form className="space-y-4">
        <label className="block">
          <span className="text-gray-700">Label</span>
          <input
            type="text"
            className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
            placeholder="Enter item type label"
            value={itemType.label}
            onChange={(e) => updateItemTypeField('label', e.target.value)}
          />
        </label>
        <label className="block">
          <span className="text-gray-700">Description</span>
          <input
            type="text"
            className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
            placeholder="Enter item type description"
            value={itemType.description}
            onChange={(e) => updateItemTypeField('description', e.target.value)}
          />
        </label>
      </form>
    </>
  )
}

export default ItemTypeForm