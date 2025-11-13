import { useEffect } from "react"
import { useDispatch, useSelector } from "react-redux"
import PageTitle from "../../../components/PageTitle"
import type { ItemTypeDto, PaginatedResponseDto } from "../../../service/inventory/types"
import { loadItemTypes, type ItemTypeState } from "../../../store/itemType.slice"
import type { AppDispatch } from "../../../store"

const ItemTypeSection = ({itemTypesPage} : {itemTypesPage: PaginatedResponseDto<ItemTypeDto>}) => {
  if (!itemTypesPage || itemTypesPage.totalElements === 0) {
    return <div>No item types available.</div>
  }
  const ItemTypeElement = ({label, description}: ItemTypeDto) => {
    return (
      <>
        <div className="badge badge-lg border p-4 bg-gray-100 shadow-md rounded-2xl">
          <h3 className="text-xl font-semibold">{label}</h3>
          <p className="text-gray-600">{description}</p>
        </div>
      </>
    )
  }

  return (
    <>
      <h2 className="text-2xl font-semibold mt-4">Item types</h2>
      <div className="flex flex-wrap gap-4 mt-2">
        {itemTypesPage?.content.map((itemType, key) => (
          <ItemTypeElement key={`${key}-${itemType.uuid}`} {...itemType} />
        ))}
      </div>
    </>
  )
}

const ItemTypeExplorer = () => {
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
      <PageTitle title="Item type explorer" />
      <section>
        <ItemTypeSection itemTypesPage={itemTypes!} />
      </section>
    </>
  )
}

export default ItemTypeExplorer