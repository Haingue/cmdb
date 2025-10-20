import React from 'react'
import type { ItemDto } from '../../service/inventory/types'

const ItemForm = () => {
  const [item, setItem] = React.useState<ItemDto>({
    label: '',
    description: '',
    attributes: [],
  })

  const updateItemField = (field: keyof ItemDto, value: string) => {
    setItem({
      ...item,
      [field]: value,
    })
  }

  return (
    <>
      <form className="space-y-4">
        <label className="block">
          <span className="text-gray-700">Label</span>
          <input
            type="text"
            className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
            placeholder="Enter item type label"
            value={item.label}
            onChange={(e) => updateItemField('label', e.target.value)}
          />
        </label>
        <label className="block">
          <span className="text-gray-700">Description</span>
          <input
            type="text"
            className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
            placeholder="Enter item type description"
            value={item.description}
            onChange={(e) => updateItemField('description', e.target.value)}
          />
        </label>
      </form>
    </>
  )
}

export default ItemForm