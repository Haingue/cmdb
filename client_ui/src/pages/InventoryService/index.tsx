import { useEffect, useState } from "react"
import InventoryServiceSync, { type ItemTypeDto } from "./InventoryServiceSync"

const ItemTypeSection = ({itemTypes} : {itemTypes: ItemTypeDto[]}) => {
  const ItemTypeElement = (itemType: any) => {
    return (
      <>
        <h3 className="text-xl font-semibold">{itemType.name}</h3>
        <p className="text-gray-600">{itemType.description}</p>
      </>
    )
  }

  return (
    <>
      <h2 className="text-2xl font-semibold mt-4">Item types</h2>
      <div className="flex flex-wrap gap-4 mt-2">
        {itemTypes.map((itemType) => (
          <ItemTypeElement key={itemType.id} {...itemType} />
        ))}
      </div>
    </>
  )
}

const InventoryService = () => {
  const [itemTypes, setItemTypes] = useState<ItemTypeDto[]>([])

  useEffect(() => {
    InventoryServiceSync.fetchItemTypes()
      .then((data) => setItemTypes(data))
  }, [])

  return (
    <>
      <h1 className="text-3xl font-bold underline">Inventory Service Page</h1>
      <section>
        <ItemTypeSection itemTypes={itemTypes} />
      </section>
    </>
  )
}

export default InventoryService