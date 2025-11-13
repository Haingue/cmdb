import { useEffect } from "react"
import { useDispatch, useSelector } from "react-redux"
import PageTitle from "../../../components/PageTitle"
import { loadItemTypes, type ItemTypeState } from "../../../store/itemType.slice"
import type { AppDispatch } from "../../../store"

const ItemExplorer = () => {
  const dispatch = useDispatch<AppDispatch>()
  const {itemTypes, isLoading, error} = useSelector<ItemTypeState>((state) => state.itemTypes) as ItemTypeState

  useEffect(() => {
    dispatch(loadItemTypes())
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
      </section>
    </>
  )
}

export default ItemExplorer