import React from 'react'
import { useSelector } from 'react-redux'
import PageTitle from '../../../components/PageTitle'
import { createNewItem } from '../../../service/inventory/InventorySync'
import type { ItemDto } from '../../../service/inventory/types'
import { type ItemTypeState } from '../../../store/itemType.slice'

const ItemInputForm = ({propertyName, label, type, value, updateItemField}: {propertyName: string, label: string, type: string, value?: string, updateItemField: (field: keyof ItemDto | string, value: string) => void}) => {
  return (
    <label className="block">
      <span className="text-gray-700">{label[0].toUpperCase()+label.slice(1).toLowerCase()}</span>
      <input
        type={type}
        className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
        placeholder={`Enter item ${label.toLowerCase()}`}
        value={value}
        onChange={(e) => updateItemField(propertyName, e.target.value)}
      />
    </label>
  )
}

const ItemForm = () => {
  const {itemTypes, isLoading, error} = useSelector<ItemTypeState>((state) => state.itemTypes) as ItemTypeState
  const [item, setItem] = React.useState<ItemDto>({})

/*   useEffect(() => {
    dispatch(loadItemTypes())
  }, [dispatch]) */

  const updateItemField = (field: keyof ItemDto | string, value: string) => {
    if (field === 'type') {
      const selectedType = itemTypes?.content.find((it) => it.uuid === value)
      if (selectedType) {
        const itemTypeAttributes = selectedType.attributes.map((attr) => ({
          attributeTypeId: attr.uuid,
          label: attr.label,
          value: undefined,
        }))
        setItem({
          ...item,
          type: selectedType,
          attributes: itemTypeAttributes,
        })
      } else {
        setItem({
          ...item,
          type: undefined,
          attributes: [],
        })
      }
    } else if (field.startsWith('attribute-')) {
      const attrLabel = field.replace('attribute-', '')
      const updatedAttributes = item?.attributes?.map((attr) => {
        if (attr.label === attrLabel) {
          return {
            ...attr,
            value: value,
          }
        }
        return attr
      })
      setItem({
        ...item,
        attributes: updatedAttributes,
      })
    } else {
      setItem({
        ...item,
        [field]: value,
      })
    }
  }

  const submitForm = (e: React.FormEvent) => {
    e.preventDefault()
    console.debug('Submitting item:', item)
    createNewItem(item)
      .then((createdItem) => {
        console.debug('Item created successfully:', createdItem)
        setItem({})
      })
      .catch((err) => {
        console.error('Error creating item:', err)
      })
  }

  return (
    <>
      <PageTitle title="Create New Item" />
      <form className="space-y-4">
        <ItemInputForm propertyName="name" label="name" type="text" value={item.name} updateItemField={updateItemField} />
        <ItemInputForm propertyName="description" label="description" type="text" value={item.description} updateItemField={updateItemField} />
        <label className="block">
          <span className="text-gray-700">Item type</span>
          <select
            className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
            value={item?.type?.uuid}
            onChange={(e) => updateItemField('type', e.target.value)}
            disabled={isLoading || !!error}
          >
            <option value={undefined} className='text-gray-400'>Select an item type</option>
            {itemTypes?.content.map((itemType) => (
              <option key={`opt-${itemType.uuid}`} value={itemType.uuid}>
                {itemType.label}
              </option>
            ))}
          </select>
        </label>

        <div className={item?.type?.uuid || 'hidden'}>
          {item?.attributes?.map((attr, attrKey) => (
            <ItemInputForm
              key={`attr-input-${attrKey}`}
              propertyName={`attribute-${attr.label}`}
              label={attr.label}
              type="text"
              value={String(attr.value || '')}
              updateItemField={updateItemField}
              />
          ))}
        </div>

        <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700" onClick={submitForm}>
          Create Item
        </button>
      </form>
    </>
  )
}

export default ItemForm