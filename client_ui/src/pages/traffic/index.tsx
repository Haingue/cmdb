import { useDispatch, useSelector } from 'react-redux'
import { loadItems, type ItemState } from '../../store/item.slice'
import type { LinkTypeState } from '../../store/linkType.slice'
import PageTitle from '../../components/PageTitle'
import TrafficMap from './TrafficMap'
import { useEffect, useState } from 'react'
import type { AppDispatch } from '../../store'
import type { ItemDto } from '../../service/inventory/types'
import SimpleButton from '../../components/SimpleButton'

const TrafficPage = () => {
  const dispatch = useDispatch<AppDispatch>()
  const {items} = useSelector<ItemState>((state) => state.items) as ItemState
  const {linkTypes} = useSelector<LinkTypeState>((state) => state.linkTypes) as LinkTypeState

  const [focusItemUuid, setFocusItemUuid] = useState<string | null>(null)
  const [focusItem, setFocusItem] = useState<ItemDto | undefined>(undefined)
  
  const searchFocusItem = () => {
    console.debug('Searching focus item:', focusItemUuid)
    setFocusItem(items?.content?.find(item => item.uuid === focusItemUuid))
  }
  

  const hosts = () => {
    const hostList : ItemDto[] = []
    if (focusItem) {
      hostList.push(focusItem)
    } else {
      return []
    }
    console.debug('Focus item:', focusItem)
    focusItem?.outgoingLinks?.forEach(link => {
      console.log('Focus item link:', link.targetItemId)
      const targetItem : ItemDto | undefined = items?.content?.find(item => item.uuid === link.targetItemId)
      if (targetItem) hostList.push(targetItem)
    });
    focusItem?.incomingLinks?.forEach(link => {
      console.log('Focus item link:', link.targetItemId)
      const targetItem : ItemDto | undefined = items?.content?.find(item => item.uuid === link.targetItemId)
      if (targetItem) hostList.push(targetItem)
    });
    console.debug('List of displayed hosts:', hostList)
    return hostList
  }

  useEffect(() => {
    dispatch(loadItems())
  }, [])

  return (
    <>
      <PageTitle title="Traffic description" />
      <section className="mt-4">
        <h2 className="text-lg font-semibold mb-4">Show traffic between items</h2>
        <div className="flex shadow-xs rounded-base -space-x-0.5">
          <select
            className="rounded-none rounded-e-base block w-full px-3 py-2.5 bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand"
            onChange={(e) => setFocusItemUuid(e.target.value)}
            >
            {items?.content?.map((item, key) => (
              <option key={`item-${key}`} value={item.uuid} title={item.name}>{item.name}</option>
            ))}
          </select>
          <SimpleButton title="Load traffic" onClick={searchFocusItem} />
        </div>
      </section>
      <section>
        <div className=" h-[70vh] border border-gray-300 rounded-lg mt-4">
          <TrafficMap items={hosts()} />
        </div>
      </section>
      <section className="mt-4 hidden">
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