import { useDispatch, useSelector } from 'react-redux'
import { loadItems, type ItemState } from '../../store/item.slice'
import type { LinkTypeState } from '../../store/linkType.slice'
import PageTitle from '../../components/PageTitle'
import TrafficMap from './TrafficMap'
import { useEffect } from 'react'
import type { AppDispatch } from '../../store'

const TrafficPage = () => {
  const dispatch = useDispatch<AppDispatch>()
  const {items} = useSelector<ItemState>((state) => state.items) as ItemState
  const {linkTypes} = useSelector<LinkTypeState>((state) => state.linkTypes) as LinkTypeState

  const hosts = items?.content?.filter(item => item.type )

  useEffect(() => {
    dispatch(loadItems())
  }, [])

  return (
    <>
      <PageTitle title="Traffic description" />
      <section className="mt-4">
        <input type="text" list='items-list' placeholder="Source" className="input input-bordered w-full max-w-xs mb-4" />
        <datalist id="items-list">
          {items?.content?.map((item, key) => (
            <option key={`item-${key}`} value={item.name}>{item.name}</option>
          ))}
        </datalist>
      </section>
      <section>
        <div className=" h-[80vh] border border-gray-300 rounded-lg mt-4">
          <TrafficMap items={hosts} />
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