import React, { useEffect, useState } from 'react'
import type { AttributeDto, ItemDto, LinkDto, UUID } from '../../../service/inventory/types'
import { getItemById } from '../../../service/inventory/InventorySync'
import PageTitle from '../../../components/PageTitle'
import { useParams } from 'react-router'
import TextInput from '../../../components/form/TextInput'

const ItemAttributes = ({attributes, initialAttributes, readonly} : {attributes: AttributeDto[], initialAttributes: AttributeDto[], readonly?: boolean}) => {
  return (
    <div>
      {attributes && Object.entries(attributes).map(([key, attribute]) =>
        <TextInput key={key} label={attribute.label} value={String(attribute.value)} initialValue={String(initialAttributes[key].value)} readonly={readonly} />
      )}
    </div>
  )
}

const ItemLinks = ({ links }: { links: LinkDto[] }) => {
  return (
    <>
      <table className="w-full text-sm text-left rtl:text-right text-body">
        <thead className="text-sm text-body bg-neutral-secondary-soft border-b rounded-base border-default">
          <tr>
            <th>Source Item ID</th>
            <th>Link Type</th>
            <th>Description</th>
            <th>Last Modified Date</th>
          </tr>
        </thead>
        <tbody>
          {links && links.map((link, key) => (
            <tr key={key} className="bg-neutral-primary-soft border-b border-default hover:bg-neutral-secondary-medium">
              <td>{link.sourceItemId}</td>
              <td>{link.linkType.label}</td>
              <td>{link.description}</td>
              <td>{link.lastModifiedDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  )
}

const ItemDetailsPage = () => {
  const params = useParams();
  const [initialItem, setInitialItem] = useState<ItemDto>()
  const [item, setItem] = useState<ItemDto>()
  const [editFields, setEditFields] = useState<boolean>(false)

  const updateItemField = (e: React.ChangeEvent<HTMLInputElement>) => {
    const _item = {...item} as ItemDto
    const field: keyof ItemDto = e.target.id.replace('-input', '') as keyof ItemDto
    _item[field] = e.target.value
    setItem(_item)
  }

  useEffect(() => {
    const searchedItemUuid: UUID = params.itemUuid as UUID
    getItemById(searchedItemUuid).then(fetchedItem => {
      setItem(fetchedItem)
      setInitialItem(fetchedItem)
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
              <TextInput label="name" initialValue={initialItem?.name} value={item.name} readonly={!editFields} onChange={(e) => updateItemField(e)}/>
              <TextInput label="description" initialValue={initialItem?.description} value={item.description} readonly={!editFields} onChange={(e) => updateItemField(e)}/>
              <TextInput label="label" initialValue={initialItem?.type?.label} value={item.type?.label} readonly={!editFields} onChange={(e) => updateItemField(e)}/>
              <TextInput label="createdBy" initialValue={initialItem?.createdBy} value={item.createdBy} readonly={!editFields} onChange={(e) => updateItemField(e)}/>
              <TextInput label="createdDate" initialValue={initialItem?.createdDate} value={item.createdDate} readonly={!editFields} onChange={(e) => updateItemField(e)}/>
              <TextInput label="lastModifiedBy" initialValue={initialItem?.lastModifiedBy} value={item.lastModifiedBy} readonly={!editFields} onChange={(e) => updateItemField(e)}/>
              <TextInput label="lastModifiedDate" initialValue={initialItem?.lastModifiedDate} value={item.lastModifiedDate} readonly={!editFields} onChange={(e) => updateItemField(e)}/>
            </section>
            <section className=" mb-6">
              <h3 className="font-medium text-heading">Attributes</h3>
              <ItemAttributes readonly={!editFields} attributes={item.attributes || []} initialAttributes={initialItem?.attributes || []} />
            </section>
            <section className="mb-6">
              <h3 className="font-medium text-heading">Incoming link</h3>
              <ItemLinks links={item.incomingLinks || []} />
            </section>
            <section className="">
              <h3 className="font-medium text-heading">Outgoing link</h3>
              <ItemLinks links={item.outgoingLinks || []} />
            </section>
          </>
        )}
      </section>
    </>
  )
}

export default ItemDetailsPage