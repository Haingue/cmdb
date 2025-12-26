import React, { useEffect, useState, type ChangeEvent, type ChangeEventHandler } from 'react'
import { useParams } from 'react-router'
import DatetimeInput from '../../../components/form/DatetimeInput'
import TextInput from '../../../components/form/TextInput'
import PageTitle from '../../../components/PageTitle'
import { getItemById, updateItem } from '../../../service/inventory/InventorySync'
import type { AttributeDto, ItemDto, LinkDto, UUID } from '../../../service/inventory/types'

const ItemAttributes = ({attributes, initialAttributes, readonly, onChange} : {attributes: AttributeDto[], initialAttributes: AttributeDto[], readonly?: boolean, onChange?: ChangeEventHandler<HTMLInputElement>}) => {
  return (
    <div>
      {attributes && Object.entries(attributes).map(([key, attribute], index) =>
        <TextInput key={`${key}-${index}`} name={attribute.label} label={attribute.label} value={String(attribute.value)} initialValue={String(initialAttributes[index].value)} readonly={readonly} onChange={onChange} />
      )}
    </div>
  )
}

const ItemLinks = ({ links, isCollapsed }: { links: LinkDto[], isCollapsed: boolean }) => {
  return (
    <>
      <div className={`${isCollapsed ? 'max-h-0 opacity-0 overflow-auto' : 'max-h-100 opacity-100 overflow-auto'} transition-all delay-75 duration-300 ease-in-out rounded-sm border border-solid border-neutral-200 dark:border-gray-700 flex flex-col`}>
        <table className="w-full">
          <thead className="sticky top-0 text-sm text-body bg-neutral-100 dark:bg-gray-800 border-b border-neutral-200 dark:border-gray-700 rounded-base border-default">
            <tr>
              <th>Source Item ID</th>
              <th>Destination Item ID</th>
              <th>Link Type</th>
              <th>Description</th>
              <th>Last Modified Date</th>
            </tr>
          </thead>
          <tbody>
            {links && links.map((link, key) => (
              <tr key={key} className="border-b border-default border-neutral-200 dark:border-gray-700 hover:bg-neutral-50 dark:hover:bg-gray-600">
                <td title={link.sourceItemId}>{link.sourceItemName}</td>
                <td title={link.targetItemId}>{link.targetItemName}</td>
                <td>{link.linkType.label}</td>
                <td>{link.description}</td>
                <td>{link.lastModifiedDate}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}

const ItemDetailsPage = () => {
  const params = useParams();
  const [initialItem, setInitialItem] = useState<ItemDto>()
  const [item, setItem] = useState<ItemDto>()
  const [editFields, setEditFields] = useState<boolean>(false)
  const [collapseIncomingLinks, setCollapseIncomingLinks] = useState<boolean>(true)
  const [collapseOutgoingLinks, setCollapseOutgoingLinks] = useState<boolean>(true)

  const updateItemField = (event: ChangeEvent<HTMLInputElement>) => {
    const _item = {...item} as ItemDto
    const field: keyof ItemDto = event.target.id.replace('-input', '') as keyof ItemDto
    _item[field] = event.target.value as never
    setItem(_item)
  }

  const updateItemAttribute = (event: ChangeEvent<HTMLInputElement>) => {
    const _item = {...item} as ItemDto
    for (const [, attribute] of Object.entries(_item.attributes || {})) {
      if (attribute.label === event.target.id.replace('-input', '')) {
        attribute.value = event.target.value
      }
    }
    setItem(_item)
  }

  const isChangedItem = (): boolean => {
    return JSON.stringify(initialItem) !== JSON.stringify(item)
  }

  const saveModifiedItem = () => {
    console.debug('Saving modified item:', item)
    updateItem(item as ItemDto)
      .then(updatedItem => {
        setItem(updatedItem)
        setInitialItem(updatedItem)
        setEditFields(false)
      })
      .catch(error => {
        console.error('Error updating item:', error)
      })
  }

  useEffect(() => {
    const searchedItemUuid: UUID = params.itemUuid as UUID
    getItemById(searchedItemUuid).then(fetchedItem => {
      setItem({...fetchedItem, attributes: fetchedItem.attributes})

      const attributes: AttributeDto[] = fetchedItem.attributes ? fetchedItem.attributes.map(attr => ({...attr})) : []
      setInitialItem({...fetchedItem, attributes: attributes})
    }).catch(error => {
      console.error('Error fetching item details:', error)
    })
  }, [])

  return (
    <>
      <PageTitle title="Item details" />
      <div className="absolute top-2 right-2">
        <label className="inline-flex items-center cursor-pointer">
          <input type="checkbox" className="sr-only peer" checked={editFields} onChange={e => setEditFields(e.target.checked)} />
          <div className="relative w-9 h-5 bg-neutral-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-brand-soft dark:peer-focus:ring-brand-soft rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-buffer after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-brand"></div>
          <span className="select-none ms-3 text-sm font-medium text-heading">Unlock</span>
        </label>
      </div>
      <section className="mt-4 px-4">
        {item && (
          <>
            <section className="grid lg:grid-cols-2 gap-x-4 gap-y-2.5 mb-6">
              <TextInput name="name" label="name" initialValue={initialItem?.name} value={item.name} readonly={!editFields} onChange={updateItemField}/>
              <TextInput name="description" label="description" initialValue={initialItem?.description} value={item.description} readonly={!editFields} onChange={updateItemField}/>
              <TextInput name="label" label="type" initialValue={initialItem?.type?.label} value={item.type?.label} readonly={!editFields} onChange={updateItemField}/>
              <div/>
              <TextInput name="createdBy" label="createdBy" initialValue={initialItem?.createdBy} value={item.createdBy} readonly={true} onChange={updateItemField}/>
              <DatetimeInput name="createdDate" label="createdDate" initialValue={initialItem?.createdDate} value={item.createdDate} readonly={true} onChange={updateItemField}/>
              <TextInput name="lastModifiedBy" label="lastModifiedBy" initialValue={initialItem?.lastModifiedBy} value={item.lastModifiedBy} readonly={true} onChange={updateItemField}/>
              <DatetimeInput name="lastModifiedDate" label="lastModifiedDate" initialValue={initialItem?.lastModifiedDate} value={item.lastModifiedDate} readonly={true} onChange={updateItemField}/>
            </section>
            <section className=" mb-6">
              <h3 className="font-medium text-heading">Attributes</h3>
              <ItemAttributes readonly={!editFields} attributes={item?.attributes || []} initialAttributes={initialItem?.attributes || []} onChange={updateItemAttribute} />
            </section>
            <section className="mb-6">
              <h3 className="font-medium text-heading cursor-pointer" title="Click to expand/collapse incoming links" onClick={()=>setCollapseIncomingLinks(!collapseIncomingLinks)}>Incoming link <span className="ms-2 text-xs text-neutral-500">({item.incomingLinks?.length})</span></h3>
              <ItemLinks links={item.incomingLinks || []} isCollapsed={collapseIncomingLinks} />
            </section>
            <section className="">
              <h3 className="font-medium text-heading cursor-pointer" title="Click to expand/collapse outgoing links" onClick={()=>setCollapseOutgoingLinks(!collapseOutgoingLinks)}>Outgoing link <span className="ms-2 text-xs text-neutral-500">({item.outgoingLinks?.length})</span></h3>
              <ItemLinks links={item.outgoingLinks || []} isCollapsed={collapseOutgoingLinks} />
            </section>
            <section className="fixed bottom-4 right-4 p-3">
              <button
                className={`mt-4 px-4 py-2 transition-opacity delay-75 duration-300 opacity-100 disabled:opacity-0 rounded-md bg-brand text-white hover:bg-brand-strong`}
                disabled={isChangedItem() === false}
                onClick={saveModifiedItem}
              >
                Save changes
              </button>
            </section>
          </>
        )}
      </section>
    </>
  )
}

export default ItemDetailsPage