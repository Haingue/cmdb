import React from 'react'
import { useSelector } from 'react-redux'
import type { ItemState } from '../../store/item.slice'
import type { LinkTypeState } from '../../store/linkType.slice'
import PageTitle from '../../components/PageTitle'

const TrafficPage = () => {
  const {items} = useSelector<ItemState>((state) => state.items) as ItemState
  const {linkTypes} = useSelector<LinkTypeState>((state) => state.linkTypes) as LinkTypeState

  return (
    <>
      <PageTitle title="Traffic description" />
      <section className="mt-4">
        <input type="text" placeholder="Source" className="input input-bordered w-full max-w-xs mb-4" />
        <select className="select select-bordered w-full max-w-xs mb-4 ml-4">
          <option disabled>Add new link type</option>
          {linkTypes?.content?.map((linkType, key) => (
            <option key={`linktype-${key}`} value={linkType.label}>{linkType.label}</option>
          ))}
        </select>
        <input type="text" placeholder="Destination" className="input input-bordered w-full max-w-xs mb-4" />
        <datalist id="items-list">
          {items?.content?.map((item, key) => (
            <option key={`item-${key}`} value={item.name}>{item.name}</option>
          ))}
        </datalist>
      </section>
    </>
  )
}

export default TrafficPage