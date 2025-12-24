import { useEffect } from "react"
import { useDispatch, useSelector } from "react-redux"
import { useNavigate } from "react-router"
import PageTitle from "../../../components/PageTitle"
import type { UUID } from "../../../service/inventory/types"
import type { AppDispatch } from "../../../store"
import { loadItems, type ItemState } from "../../../store/item.slice"

const ItemExplorer = () => {
  const dispatch = useDispatch<AppDispatch>()
  const navigate = useNavigate()
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

  const openItemDetails = (itemUuid: UUID) => {
    navigate(`/inventory-service/items/${itemUuid}`)
  }

  return (
    <>
      <PageTitle title="Item explorer" />
      <section>
        {items.content.map((item) => (
          <div key={item.uuid} className="mb-4 p-4 border rounded" onClick={() => openItemDetails(item.uuid!)} >
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