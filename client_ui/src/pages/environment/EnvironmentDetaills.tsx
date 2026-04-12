import { useEffect, useState } from "react"
import { useDispatch } from "react-redux"
import { useNavigate, useSearchParams } from "react-router"
import ComingSoonComponent from "../../components/ComingSoonComponent"
import ButtonInput from "../../components/form/ButtonInput"
import FormSection from "../../components/form/FormSection"
import NumberInput from "../../components/form/NumberInput"
import SelectInput from "../../components/form/SelectInput"
import TextInput from "../../components/form/TextInput"
import PageTitle from "../../components/PageTitle"
import SimpleTable from "../../components/table-simple"
import { createEnvironment, searchProject } from "../../service/backend/BackendSync"
import { type Project } from "../../service/backend/types"
import { ItemTypeLabel } from "../../service/backend/constants"
import { getItemById, removeLink, searchItemTypes } from "../../service/inventory/InventorySync"
import type { ItemDto, ItemTypeDto, LinkDto, UUID } from "../../service/inventory/types"
import type { AppDispatch } from "../../store"
import { addAlert } from "../../store/alert.slice"

const EnvironmentDetaillsPage = () => {
  const navigate = useNavigate()
  const dispatch = useDispatch<AppDispatch>()
  const [searchParams] = useSearchParams()
  const projectUuid = searchParams.get("projectUuid") as UUID | undefined
  const environmentUuid = searchParams.get("environmentUuid") as UUID | undefined
  const isNewEnvironment: boolean = !environmentUuid

  const [environmentItemType, setEnvironmentItemType] = useState<ItemTypeDto>()
  const [projects, setProjects] = useState<Project[]>([])
  const [environmentTypes, setEnvironmentTypes] = useState<{label: string, value: string}[]>([])
  const [states, setStates] = useState<{label: string, value: string}[]>([])
  const [locations, setLocations] = useState<{label: string, value: string}[]>([])

  const [project, setProject] = useState<Project>()
  const [type, setType] = useState<string>()
  const [name, setName] = useState<string>()
  const [description, setDescription] = useState<string>()
  const [location, setLocation] = useState<string>()
  const [status, setStatus] = useState<string>()
  const [revision, setRevision] = useState<string>()

  const [components, setComponents] = useState<LinkDto[]>([])

  useEffect(() => {
    searchItemTypes(ItemTypeLabel.ENVIRONMENT)
    .then((_itemTypes) => {
      console.debug("Environment Item Type fetched:", _itemTypes)
      setEnvironmentItemType(_itemTypes.content[0])

      const environmentTypes = environmentItemType?.attributes?.find(attr => attr.label === "Type")?.possibleValues.map((value) => ({label: value, value})) || [
        {label: "Development", value: "DEV"},
        {label: "Acceptance", value: "ACC"},
        {label: "Pre-production", value: "PRE_PROD"},
        {label: "Production", value: "PROD"},
      ]
      setEnvironmentTypes(environmentTypes)
      const states = environmentItemType?.attributes?.find(attr => attr.label === "Status")?.possibleValues.map((value) => ({label: value, value})) || [
        {label: "REQUESTED", value: "REQUESTED"},
        {label: "IN_PROGRESS", value: "IN_PROGRESS"},
        {label: "READY", value: "READY"},
        {label: "DEPLOYED", value: "DEPLOYED"},
        {label: "STOPPED", value: "STOPPED"},
        {label: "DECOMMISSIONED", value: "DECOMMISSIONED"},
      ]
      setStates(states)
      const locations = environmentItemType?.attributes?.find(attr => attr.label === "Location")?.possibleValues.map((value) => ({label: value, value})) || [
        {label: "Plant 1", value: "plant_1"},
        {label: "Plant 2", value: "plant_2"},
      ]
      setLocations(locations)
    }, (error) => {
      console.error("Error fetching Environment Item Type:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Environment Item Type.", details: error }))
    })
    .then (async () => {
      const projects = await loadProjects()
      await loadEnvironment(projects)
    })
  }, [])

  useEffect(() => {
    updateIdentifiers(project, location, type)
  }, [project, location, type])

  const loadProjects = async () => {
      try {
        const _projects = await searchProject()
        console.debug("Projects fetched:", _projects)
        const projects = _projects.content.filter(item => item?.uuid).map((item) => ({
          uuid: item.uuid!,
          name: item.name,
          shortName: item.attributes?.find(attr => attr.label === "ShortName")?.value as string
        }))
        setProjects(projects)
        console.debug("Projects set in state:", projects);
        return projects
      } catch (error) {
        console.error("Error fetching Projects:", error);
        dispatch(addAlert({ type: "error", message: "Failed to fetch Projects.", details: error }))
        return []
      }
  }

  const loadEnvironment = async (projects: Project[]) => {
    if (isNewEnvironment && projectUuid) {
        getAndSetProject(projectUuid, projects)
      } else if (environmentUuid) {
        console.debug("Displaying environment details for UUID:", environmentUuid);
        try {
          const item: ItemDto = await getItemById(environmentUuid)
          console.debug("Environment fetched by UUID:", item)
          // TODO set inputs
          setName(item.name || "")
          setDescription(item.description || "")
          setRevision(item.attributes?.find(attr => attr.label === "Revision")?.value as string || "")
          setType(item.attributes?.find(attr => attr.label === "Type")?.value as string || "")
          setLocation(item.attributes?.find(attr => attr.label === "Location")?.value as string || "")
          setStatus(item.attributes?.find(attr => attr.label === "Status")?.value as string || "")

          const projectUuid: UUID | undefined = item.incomingLinks?.find(links => links.linkType.label === "Composed of")?.sourceItemId
          getAndSetProject(projectUuid!, projects)

          setComponents(item.outgoingLinks?.filter(link => link.linkType.label === "Composed of") || [])

          return item
        } catch (error) {
          console.error("Error fetching Environment by UUID:", error);
          dispatch(addAlert({ type: "error", message: "Failed to fetch Environment by UUID.", details: error }))
        }
      } else {
        console.debug("No environment UUID provided, displaying environment creation form.");
      }
  }

  const getAndSetProject = (projectUuid: UUID, projects: Project[]): Project | undefined => {
    let project = projects?.find(project => project.uuid === projectUuid)
    if (!project) {
      console.error("Not able to find the project:", projectUuid)
      project = {
        uuid: projectUuid,
      }
    }
    setProject(project)
    return project
  }

  const updateIdentifiers = (project?: Project, location?: string, type?: string) => {
    if (project && location && type) {
      setName(`${project!.shortName}-${location}-${type}`)
      setDescription(`${name} is the ${type} environnement at ${location} for the project ${project.name}`)
    }
  }

  const validForm = () => {
    updateIdentifiers(project, location, type)
    if (!project) {
      dispatch(addAlert({ type: "error", message: "Project is required to create an environment." }))
      return
    }
    if (!type) {
      dispatch(addAlert({ type: "error", message: "Type is required to create an environment." }))
      return
    }
    if (!location) {
      dispatch(addAlert({ type: "error", message: "Location is required to create an environment." }))
      return
    }

    createEnvironment({
      uuid: null,
      requestor: null, // TODO: get current user UUID
      requestTimestamp: new Date().toISOString(),
      projectUuid: project.uuid,
      environment: {
        uuid: null,
        name: name,
        description: description,
        type: type,
        location: location,
        status: status,
      }
    })
    .then((createdEnvironment) => {
      console.debug("Environment created:", createdEnvironment)
      dispatch(addAlert({ type: "success", message: `Environment ${createdEnvironment.name} created successfully.` }))
    })
    .catch((error) => {
      console.error("Error creating Environment:", error);
      dispatch(addAlert({ type: "error", message: "Failed to create Environment.", details: error }))
    })
  }

  const disconnectComponent = (link: LinkDto) => {
    removeLink(link)
    .then(() => {
      dispatch(addAlert({ type: "success", message: "Component disconnected successfully." }))
      setComponents(components.filter(component => component.uuid !== link.uuid))
    })
    .catch((error) => {
      console.error("Error disconnecting component:", error);
      dispatch(addAlert({ type: "error", message: "Failed to disconnect component.", details: error }))
    })
  }

  return (
    <>
      <PageTitle title="Environment detail" />
      <section className="mt-4">
        { isNewEnvironment ?
            <h3 className="text-lg font-medium mb-2">Environment creation</h3>
          : <h3 className="text-lg font-medium mb-2">Environment details</h3>
        }
        <FormSection title="Parent">
          <SelectInput label="Project" name="project"
            value={project?.shortName}
            placeholder="Select a project"
            onChange={(e) => setProject({uuid: null, shortName: e.target.value})}
            options={projects.map(p => ({label: p.name!, value: p.shortName!}))}
            disabled={!!projectUuid} />
        </FormSection>
        <FormSection title="Identifiers">
          <TextInput label="Name" name="name" value={name} placeholder="Name of the environment" onChange={() => {}} readonly />
          <TextInput label="Description" name="description" value={description} placeholder="Description of the environment" onChange={() => {}} readonly />
        </FormSection>
        <FormSection title="Attributes">
          <SelectInput label="Type" name="type" value={type} placeholder="Select a type" onChange={(e) => setType(e.target.value)} options={environmentTypes} />
          <NumberInput label="revision" name="revision" value={revision} placeholder="Version of the business service" onChange={(e) => {setRevision(e.target.value)}} />
          <TextInput label="JiraTicket" name="jiraTicket" value={name} placeholder="Number of the JiraTicket this environment setup" onChange={() => {}} />
          <SelectInput label="Location" name="location" value={location} placeholder="Select a location" onChange={(e) => setLocation(e.target.value)} options={locations} />
          <SelectInput label="Status" name="status" value={status} placeholder="Select a status" onChange={(e) => setStatus(e.target.value)} options={states} />
        </FormSection>
        { isNewEnvironment && <ButtonInput name="create-environment" label="Create Environment" onClick={validForm} /> }
        { !isNewEnvironment && <ButtonInput name="update-environment" label="Update Environment" onClick={validForm} /> }
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Components</h3>
        <ButtonInput label="Assign host" name="assign-host" onClick={() => navigate("/inventory-service/link-form")} />
        <SimpleTable
          columns={[
            { name: 'uuid', label: 'uuid' },
            { name: 'name', label: 'name' },
            { name: 'description', label: 'description' },
            { name: 'linkType', label: 'linkType' },
            { name: 'lastModifiedDate', label: 'lastModifiedDate' },
            { name: 'actions', label: 'Actions' },
          ]}
          rows={components?.map(componentLinked => ({
            uuid: {content: componentLinked.targetItemId},
            name: {content: componentLinked.targetItemName},
            description: {content: componentLinked.description},
            linkType: {content: componentLinked.linkType.label},
            lastModifiedDate: {content: componentLinked.lastModifiedDate},
            actions: {content: <ButtonInput key={`deleteBtn-${componentLinked.targetItemId}`} name={`delete-environment-${componentLinked.targetItemId}`} label="Delete" onClick={() => disconnectComponent(componentLinked)} /> }
          })) || []}
          isCollapsed={false}
        />
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Linked items</h3>
        <ComingSoonComponent />
      </section>
    </>
  )
}

export default EnvironmentDetaillsPage