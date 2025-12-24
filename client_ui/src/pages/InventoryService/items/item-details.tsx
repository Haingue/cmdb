import React, { useEffect, useState } from 'react'
import type { ItemDto, UUID } from '../../../service/inventory/types'
import { getItemById } from '../../../service/inventory/InventorySync'
import PageTitle from '../../../components/PageTitle'
import { useParams } from 'react-router'

const ItemDetailsPage = () => {
  const params = useParams();
  const [item, setItem] = useState<ItemDto>()

  useEffect(() => {
    const searchedItemUuid: UUID = params.itemUuid as UUID
    getItemById(searchedItemUuid).then(fetchedItem => {
      setItem(fetchedItem)
    }).catch(error => {
      console.error('Error fetching item details:', error)
    })
  }, [])

  return (
    <>
      <PageTitle title="Item details" />
      <section className="mt-4">
        {item && (
          <div>
            <h2>{item.name}</h2>
            <p>{item.description}</p>
            <p>{item.type?.label}</p>
            <p>{item.createdBy}</p>
            <p>{item.createdDate}</p>
            <p>{item.lastModifiedBy}</p>
            <p>{item.lastModifiedDate}</p>
            <section>
              <h3>Attributes</h3>
              <ul>
                {item.attributes && Object.entries(item.attributes).map(([key, value]) => (
                  <li key={key}><strong>{key}-</strong> {String(value.label)}: {String(value.value)}</li>
                ))}
              </ul>
            </section>
            <section>
              <h3>Incoming link</h3>
              {item.incomingLinks && item.incomingLinks.map((link, index) => (
                <div key={index}>
                  <p>{link.sourceItemId} --[{link.linkType.label}]-&gt; {link.targetItemId}</p>
                </div>
              ))}
            </section>
            <section>
              <h3>Outgoing link</h3>
              {item.outgoingLinks && item.outgoingLinks.map((link, index) => (
                <div key={index}>
                  <p>{link.sourceItemId} --[{link.linkType.label}]-&gt; {link.targetItemId}</p>
                </div>
              ))}
            </section>
          </div>
        )}
      </section>
    </>
  )
}

export default ItemDetailsPage