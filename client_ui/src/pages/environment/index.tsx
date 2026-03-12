import { useEffect, useState } from "react"
import { useDispatch } from "react-redux"
import ButtonInput from "../../components/form/ButtonInput"
import PageTitle from "../../components/PageTitle"
import SimpleTable from "../../components/table-simple"
import { searchItems, searchItemTypes } from "../../service/inventory/InventorySync"
import type { ItemDto, ItemTypeDto } from "../../service/inventory/types"
import type { AppDispatch } from "../../store"
import { addAlert } from "../../store/alert.slice"
import { useNavigate } from "react-router"

const EnvironmentIndexPage = () => {
  const navigate = useNavigate()
  const dispatch = useDispatch<AppDispatch>()
  const [environmentItemType, setEnvironmentItemType] = useState<ItemTypeDto>({})
  const [environments, setEnvironments] = useState<ItemDto[]>([])

  const [project, setProject] = useState({
    uuid: '',
    shortname: '',
  })
  const [type, setType] = useState('Acceptance')
  const [name, setName] = useState(`${project?.shortname || 'Project'}-${type}`)

  useEffect(() => {
    searchItemTypes("Environment")
      .then((_itemTypes) => {
        console.debug("Environment Item Type fetched:", _itemTypes)
        setEnvironmentItemType(_itemTypes.content[0])
      })
      .catch((error) => {
        console.error("Error fetching Environment Item Type:", error);
        dispatch(addAlert({ type: "error", message: "Failed to fetch Environment Item Type.", details: error }))
      })
      searchItems({ itemType: "Environment" })
      .then((_environments) => {
        console.debug("Environments fetched:", _environments);
        setEnvironments(_environments.content)
      })
      .catch((error) => {
        console.error("Error fetching Environments:", error);
        dispatch(addAlert({ type: "error", message: "Failed to fetch Environments.", details: error }))
      })
  }, [])

  return (
    <>
      <PageTitle title="Environments" />
      <section className="mt-4">
        <ButtonInput label="Create Environment" name="create-environment" onClick={() => navigate("/environment/details")} />
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Existing Projects</h3>
        <SimpleTable
          columns={[
            { name: 'uuid', label: 'uuid' },
            { name: 'name', label: 'name' },
            { name: 'description', label: 'description' },
            ...(environmentItemType && environmentItemType?.attributes?.map(attr => ({ name: attr.label, label: attr.label }))) || [],
            { name: 'lastModifiedDate', label: 'lastModifiedDate' },
            { name: 'actions', label: 'Actions' },
          ]}
          rows={environments?.map(item => ({
            uuid: {content: item.uuid},
            name: {content: item.name},
            description: {content: item.description},
            ...item.attributes && { ...item.attributes.reduce((acc, attr) => ({ ...acc, [attr.label]: { content: attr.value } }), {}) },
            lastModifiedDate: {content: item.lastModifiedDate},
            actions: {content: <ButtonInput key={`editBtn-${item.uuid}`} name={`edit-environment-${item.uuid}`} label="Edit" onClick={() => navigate(`/environment/details?environmentUuid=${item.uuid}`)} /> }
          })) || []}
          isCollapsed={false}
        />
      </section>
    </>
  )
}

export default EnvironmentIndexPage