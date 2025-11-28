import { useEffect } from "react"
import { useDispatch, useSelector } from "react-redux"
import PageTitle from "../../../components/PageTitle"
import { loadItems, type ItemState } from "../../../store/item.slice"
import type { AppDispatch } from "../../../store"

const ItemExplorer = () => {
  const dispatch = useDispatch<AppDispatch>()
  const {items, isLoading, error} = useSelector<ItemState>((state) => state.items) as ItemState

  useEffect(() => {
    dispatch(loadItems())
  }, [dispatch])

  if (error) {
    return <div className="text-red-500">Error: {error}</div>
  }

  if (isLoading) {
    return <div>Loading item types...</div>
  }

  return (
    <>
      <PageTitle title="Item explorer" />
      <section>
        {items.content.map((item) => (
          <div key={item.uuid} className="mb-4 p-4 border rounded">
            <h2 className="text-xl font-bold mb-2">{item.name}</h2>
            <div>{item.description}</div>
            <div>{item.type?.label}</div>
            <div>{item.createdBy}</div>
            <div>{item.outgoingLinks?.length}</div>
          </div>
        ))}
      </section>
    </>
  )
}

export default ItemExplorer