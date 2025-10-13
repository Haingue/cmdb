import { useEffect, useState } from "react"
import type { ItemTypeDto, PaginatedResponseDto } from "../../service/inventory/type"
import { searchItemTypes } from "../../service/inventory/InventorySync"

const ItemTypeSection = ({itemTypesPage} : {itemTypesPage: PaginatedResponseDto<ItemTypeDto>}) => {
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

const InventoryService = () => {
  const [itemTypesPage, setItemTypesPage] = useState<PaginatedResponseDto<ItemTypeDto>>(undefined!)

  useEffect(() => {
    searchItemTypes()
      .then((PaginatedResponseDto) => setItemTypesPage(PaginatedResponseDto))
  }, [])

  return (
    <>
      <h1 className="text-3xl font-bold underline">Inventory Service Page</h1>
      <section>
        <ItemTypeSection itemTypesPage={itemTypesPage} />
      </section>
    </>
  )
}

export default InventoryService