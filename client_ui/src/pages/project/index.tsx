import { useDispatch } from "react-redux"
import ButtonInput from "../../components/form/ButtonInput"
import FormSection from "../../components/form/FormSection"
import SelectInput from "../../components/form/SelectInput"
import TextInput from "../../components/form/TextInput"
import VersionInput from "../../components/form/VersionInput"
import PageTitle from "../../components/PageTitle"
import type { AppDispatch } from "../../store"
import { useEffect, useState, type ReactNode } from "react"
import { createProject, searchBusinessService } from "../../service/backend/BackendSync"
import { addAlert } from "../../store/alert.slice"
import { type BusinessService } from "../../service/backend/types"
import SimpleTable from "../../components/table-simple"
import type { ItemDto, ItemTypeDto } from "../../service/inventory/types"
import { searchItems, searchItemTypes } from "../../service/inventory/InventorySync"
import { useNavigate } from "react-router"

const ProjectIndexPage = () => {
  const navigate = useNavigate()
  const dispatch = useDispatch<AppDispatch>()
  const [businessServices, setBusinessServices] = useState<BusinessService[]>([])
  const [projects, setProjects] = useState<ItemDto[]>([])
  const [projectItemType, setProjectItemType] = useState<ItemTypeDto>({} as ItemTypeDto)

  const [name, setName] = useState("")
  const [description, setDescription] = useState("")
  const [fullname, setFullname] = useState("")
  const [shortname, setShortname] = useState("")
  const [version, setVersion] = useState("")
  const [business_service, setBusiness_service] = useState("")
  const [owners, setOwners] = useState("")
  const [maintainers, setMaintainers] = useState("")
  
  useEffect(() => {
    searchItemTypes("Project")
    .then((_itemTypes) => {
      console.debug("Project Item Type fetched:", _itemTypes)
      setProjectItemType(_itemTypes.content[0])
    })
    .catch((error) => {
      console.error("Error fetching Project Item Type:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Project Item Type.", details: error }))
    })
    searchItems(undefined, "Project")
    .then((_projects) => {
      console.debug("Projects fetched:", _projects);
      setProjects(_projects.content)
    })
    .catch((error) => {
      console.error("Error fetching Projects:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Projects.", details: error }))
    })
    searchBusinessService()
    .then((_businessServices) => {
      console.debug("Business Services fetched:", _businessServices);
      setBusinessServices(_businessServices.content.filter(item => item?.uuid !== null).map((item) => ({
        uuid: item.uuid || "",
        name: item.name || "Unnamed Business Service",
        abbreviation: item.attributes?.find(attr => attr.label === "abbreviation")?.value as string || "N/A"
      })))
    })
    .catch((error) => {
      console.error("Error fetching Business Services:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Business Services.", details: error }))
    });

    // TODO: fetch owners and maintainers groups
  }, [])

  const clearForm = () => {
    setName("")
    setDescription("")
    setFullname("")
    setShortname("")
    setVersion("")
    setBusiness_service("")
    setOwners("")
    setMaintainers("")
  }

  const validForm = () => {
    const businessService = businessServices.find(bs => bs.uuid === business_service)
    if (!businessService) {
      dispatch(addAlert({ type: "error", message: "Invalid Business Service selected." }))
      return
    }
    const project = {
      uuid: null,
      name,
      description,
      fullname,
      shortname,
      version,
      businessService,
      owners,
      maintainers
    }
    createProject({
      uuid: null,
      requestor: null,
      requestTimestamp: new Date().toISOString(),
      project,
      businessService
    })
    .then((item) => {
      console.debug("Project created:", item);
      clearForm()
      dispatch(addAlert({ type: "success", message: "Project created successfully." }))
    })
    .catch((error) => {
      console.error("Error creating Project:", error);
      dispatch(addAlert({ type: "error", message: "Failed to create Project.", details: error }))
    });
  }

  const handleEditProject = (projectUuid: string) => {
    console.debug("Edit Project clicked for UUID:", projectUuid)
    // Navigate to Project Details page
    navigate(`/project-details?projectUuid=${projectUuid}`)
  }
  const createEditButton = (projectUuid: string): ReactNode => {
    return (
      <ButtonInput key={`editBtn-${projectUuid}`} name={`edit-project-${projectUuid}`} label="Edit" onClick={() => handleEditProject(projectUuid)} />
    )
  }

  return (
    <>
      <PageTitle title="Projects" />
      <section className="mt-4">
        <ButtonInput name="create-new-project" label="Create New Project" onClick={() => navigate('/project-details')} />
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Existing Projects</h3>
        <SimpleTable
          columns={[
            { name: 'uuid', label: 'uuid' },
            { name: 'name', label: 'name' },
            { name: 'description', label: 'description' },
            ...(projectItemType && projectItemType?.attributes?.map(attr => ({ name: attr.label, label: attr.label }))) || [],
            { name: 'lastModifiedDate', label: 'lastModifiedDate' },
            { name: 'actions', label: 'Actions' },
          ]}
          rows={projects?.map(item => ({
            uuid: {content: item.uuid},
            name: {content: item.name},
            description: {content: item.description},
            ...item.attributes && { ...item.attributes.reduce((acc, attr) => ({ ...acc, [attr.label]: { content: attr.value } }), {}) },
            lastModifiedDate: {content: item.lastModifiedDate},
            actions: {content: createEditButton(item.uuid!)}
          })) || []}
          isCollapsed={false}
        />
      </section>
    </>
  )
}

export default ProjectIndexPage