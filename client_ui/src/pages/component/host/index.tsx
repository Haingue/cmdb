import { useNavigate } from "react-router"
import PageTitle from "../../../components/PageTitle"
import SimpleTable from "../../../components/table-simple"
import { useDispatch } from "react-redux"
import type { AppDispatch } from "../../../store"
import type { ItemDto, ItemTypeDto } from "../../../service/inventory/types"
import { useEffect, useState } from "react"
import { addAlert } from "../../../store/alert.slice"
import { searchItems, searchItemTypes } from "../../../service/inventory/InventorySync"
import ButtonInput from "../../../components/form/ButtonInput"
import { ItemTypeLabel } from "../../../service/backend/constants"
import { MeasureCard } from "../../home/Dashboard"

const HostIndexPage = () => {
  const navigate = useNavigate()
  const dispatch = useDispatch<AppDispatch>()
  
  const [hostItemType, setHostItemType] = useState<ItemTypeDto|undefined>(undefined)
  const [hosts, setHosts] = useState<ItemDto[]>([])

  useEffect(() => {
    searchItemTypes(ItemTypeLabel.HOST)
      .then((_itemTypes) => {
        console.debug("Component Item Type fetched:", _itemTypes)
        setHostItemType(_itemTypes.content[0])
      })
      .catch((error) => {
        console.error("Error fetching Component Item Type:", error);
        dispatch(addAlert({ type: "error", message: "Failed to fetch Component Item Type.", details: error }))
      })
      searchHosts()
  }, [])

  const searchHosts = async () => {
    const hosts = await searchItems({ itemType: ItemTypeLabel.HOST })
    setHosts(hosts.content)
  }

  const editHost = (component: ItemDto) => {
    navigate(`/component/host/details?componentUuid=${component.uuid}`)
  }

  const totalHost = hosts.length
  const commonHostNumber = hosts.filter(host => host.attributes?.find(attr => attr.label === 'Domain')?.value === 'COMMON').length || 0
  const manufacturingHostNumber = hosts.filter(host => host.attributes?.find(attr => attr.label === 'Domain')?.value === 'MANUFACTURING').length || 0
  const unknownHostNumber = totalHost - commonHostNumber - manufacturingHostNumber

  return (
    <>
      <PageTitle title="Hosts" />
      <section className="mt-4 flex flex-row gap-4 max-h-32">
        <MeasureCard title="COMMON" value={`${commonHostNumber}`} />
        <MeasureCard title="MANUFACTURING" value={`${manufacturingHostNumber}`} />
        <MeasureCard title="UNKNOWN" value={`${unknownHostNumber}/${totalHost}`} />
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Existing hosts</h3>
        <SimpleTable
          columns={[
            { name: 'uuid', label: 'uuid' },
            { name: 'name', label: 'name' },
            { name: 'description', label: 'description' },
            { name: 'type', label: 'type' },
            ...(hostItemType && hostItemType?.attributes?.map(attr => ({ name: attr.label, label: attr.label }))) || [],
            { name: 'lastModifiedDate', label: 'lastModifiedDate' },
            { name: 'actions', label: 'Actions' },
          ]}
          rows={hosts?.map(item => ({
            uuid: {content: item.uuid},
            name: {content: item.name},
            description: {content: item.description},
            type: {content: item.type?.label},
            ...item.attributes && { ...item.attributes.reduce((acc, attr) => ({ ...acc, [attr.label]: { content: attr.value } }), {}) },
            lastModifiedDate: {content: item.lastModifiedDate},
            actions: {content: <ButtonInput key={`editBtn-${item.uuid}`} name={`edit-component-${item.uuid}`} label="Edit" onClick={() => editHost(item)} /> }
          })) || []}
          isCollapsed={false}
        />
      </section>
    </>
  )
}

export default HostIndexPage