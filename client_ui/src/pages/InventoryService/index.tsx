import { useEffect } from "react"
import { useDispatch, useSelector } from "react-redux"
import PageTitle from "../../components/PageTitle"
import type { ItemTypeDto, PaginatedResponseDto } from "../../service/inventory/types"
import { loadItemTypes, type ItemTypeState } from "../../store/itemType.slice"
import ItemForm from "./item-form/ItemForm"
import type { AppDispatch } from "../../store"
import { Link } from "react-router"
import { loadItems, type ItemState } from "../../store/item.slice"
import { loadLinkTypes, type LinkTypeState } from "../../store/linkType.slice"

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

const InventoryService = () => {
  const dispatch = useDispatch<AppDispatch>()
  const {itemTypes} = useSelector<ItemTypeState>((state) => state.itemTypes) as ItemTypeState
  const {items} = useSelector<ItemState>((state) => state.items) as ItemState
  const {linkTypes} = useSelector<LinkTypeState>((state) => state.linkTypes) as LinkTypeState

  useEffect(() => {
    dispatch(loadItemTypes())
    dispatch(loadItems())
    dispatch(loadLinkTypes())
  }, [dispatch])

  return (
    <>
      <PageTitle title="Inventory Service" />
      <section className="mt-4 flex flex-auto gap-4">
        <div className="mb-4 badge badge-lg border p-3 bg-gray-100 shadow-md rounded-xl">
          <h3 className="text-xl font-semibold">Available item types</h3>
          <span className="text-gray-600">Total: {itemTypes?.totalElements}</span>
        </div>
        <div className="mb-4 badge badge-lg border p-3 bg-gray-100 shadow-md rounded-xl">
          <h3 className="text-xl font-semibold">Available items</h3>
          <span className="text-gray-600">Total: {items?.totalElements}</span>
        </div>
        <div className="mb-4 badge badge-lg border p-3 bg-gray-100 shadow-md rounded-xl">
          <h3 className="text-xl font-semibold">Available link types</h3>
          <span className="text-gray-600">Total: {linkTypes.totalElements}</span>
        </div>
        <div className="mb-4 badge badge-lg border p-3 bg-gray-100 shadow-md rounded-xl">
          <h3 className="text-xl font-semibold">Number of item links</h3>
          <span className="text-gray-600">Total: ?</span>
        </div>
      </section>
      <hr className="my-2 border-gray-500"/>
      <section>
        <ul>
          <li><Link className="text-primary font-extrabold hover:underline" to="/inventory-service/item-types">Explore item types</Link></li>
          <li><Link className="text-primary font-extrabold hover:underline" to="/inventory-service/item-type-form">Create new item type</Link></li>
          <li><Link className="text-primary font-extrabold hover:underline" to="/inventory-service/link-types">Explore link types</Link></li>
          <li><Link className="text-primary font-extrabold hover:underline" to="/inventory-service/link-type-form">Create new link type</Link></li>
          <li><Link className="text-primary font-extrabold hover:underline" to="/inventory-service/items">Explorer items</Link></li>
          <li><Link className="text-primary font-extrabold hover:underline" to="/inventory-service/item-form">Create new item</Link></li>
        </ul>
      </section>
    </>
  )
}

export default InventoryService