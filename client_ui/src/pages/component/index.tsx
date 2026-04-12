import { useNavigate } from "react-router"
import PageTitle from "../../components/PageTitle"
import SimpleTable from "../../components/table-simple"
import { useDispatch } from "react-redux"
import type { AppDispatch } from "../../store"
import type { ItemDto, ItemTypeDto } from "../../service/inventory/types"
import { useEffect, useState } from "react"
import { addAlert } from "../../store/alert.slice"
import { searchItems, searchItemTypes } from "../../service/inventory/InventorySync"
import ButtonInput from "../../components/form/ButtonInput"
import { ItemTypeLabel } from "../../service/backend/constants"

const ComponentIndexPage = () => {
  const navigate = useNavigate()
  const dispatch = useDispatch<AppDispatch>()
  
  const [componentItemType, setComponentItemType] = useState<ItemTypeDto|undefined>(undefined)
  const [components, setComponents] = useState<ItemDto[]>([])

  useEffect(() => {
    searchItemTypes(ItemTypeLabel.COMPONENT)
      .then((_itemTypes) => {
        console.debug("Component Item Type fetched:", _itemTypes)
        setComponentItemType(_itemTypes.content[0])
      })
      .catch((error) => {
        console.error("Error fetching Component Item Type:", error);
        dispatch(addAlert({ type: "error", message: "Failed to fetch Component Item Type.", details: error }))
      })
      searchComponents()
  }, [])

  const searchComponents = async () => {
    const components = await searchItems({ itemType: ItemTypeLabel.COMPONENT })
    const hosts = await searchItems({ itemType: ItemTypeLabel.HOST })
    const softwares = await searchItems({ itemType: ItemTypeLabel.SOFTWARE })
    setComponents([ ...components.content, ...hosts.content, ...softwares.content ])
  }

  const editComponent = (component: ItemDto) => {
    const componentType: string = component.type?.label || ItemTypeLabel.COMPONENT
    navigate(`/component/${componentType.toLowerCase()}/details?componentUuid=${component.uuid}`)
  }

  return (
    <>
      <PageTitle title="Components" />
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Existing components</h3>
        <SimpleTable
          columns={[
            { name: 'uuid', label: 'uuid' },
            { name: 'name', label: 'name' },
            { name: 'description', label: 'description' },
            { name: 'type', label: 'type' },
            ...(componentItemType && componentItemType?.attributes?.map(attr => ({ name: attr.label, label: attr.label }))) || [],
            { name: 'lastModifiedDate', label: 'lastModifiedDate' },
            { name: 'actions', label: 'Actions' },
          ]}
          rows={components?.map(item => ({
            uuid: {content: item.uuid},
            name: {content: item.name},
            description: {content: item.description},
            type: {content: item.type?.label},
            ...item.attributes && { ...item.attributes.reduce((acc, attr) => ({ ...acc, [attr.label]: { content: attr.value } }), {}) },
            lastModifiedDate: {content: item.lastModifiedDate},
            actions: {content: <ButtonInput key={`editBtn-${item.uuid}`} name={`edit-component-${item.uuid}`} label="Edit" onClick={() => editComponent(item)} /> }
          })) || []}
          isCollapsed={false}
        />
      </section>
    </>
  )
}

export default ComponentIndexPage