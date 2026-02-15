import { useEffect, useState } from "react"
import ComingSoonComponent from "../../components/ComingSoonComponent"
import ButtonInput from "../../components/form/ButtonInput"
import SelectInput from "../../components/form/SelectInput"
import TextInput from "../../components/form/TextInput"
import VersionInput from "../../components/form/VersionInput"
import PageTitle from "../../components/PageTitle"
import FormSection from "../../components/form/FormSection"
import { searchItemTypes } from "../../service/inventory/InventorySync"
import type { AppDispatch } from "../../store"
import { useDispatch } from "react-redux"
import { addAlert } from "../../store/alert.slice"
import type { Project } from "../../service/backend/types"
import { createEnvironment, createProject, searchProject } from "../../service/backend/BackendSync"
import type { ItemTypeDto, UUID } from "../../service/inventory/types"
import { useSearchParams } from "react-router"

const EnvironmentDetaillsPage = () => {
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

  useEffect(() => {
    searchItemTypes("Environment")
    .then((_itemTypes) => {
      console.debug("Environment Item Type fetched:", _itemTypes)
      setEnvironmentItemType(_itemTypes.content[0])

      const environmentTypes = environmentItemType?.attributes?.find(attr => attr.label === "Type")?.possibleValues.map((value) => ({label: value, value})) || [
        {label: "Development", value: "DEV"},
        {label: "Acceptance", value: "ACC"},
        {label: "Pre-production", value: "PrePRD"},
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
    })
    .catch((error) => {
      console.error("Error fetching Environment Item Type:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Environment Item Type.", details: error }))
    })

    searchProject()
    .then((_projects) => {
      console.debug("Projects fetched:", _projects);
      setProjects(_projects.content.filter(item => item?.uuid !== null).map((item) => ({
        uuid: item.uuid!,
        name: item.name,
        shortName: item.attributes?.find(attr => attr.label === "ShortName")?.value as string
      })))
      if (projectUuid) {
        const foundProject = _projects.content.find(item => item.uuid === projectUuid)
        console.debug("Looking for project with UUID:", projectUuid, foundProject);
        if (foundProject) {
          setProject({
            uuid: foundProject.uuid!,
            name: foundProject.name,
            shortName: foundProject.attributes?.find(attr => attr.label === "ShortName")?.value as string || ""
          })
        }
      }
    })
    .then(() => {
      console.debug("Projects set in state:", projects);
    })
    .catch((error) => {
      console.error("Error fetching Projects:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Projects.", details: error }))
    });
  }, [])

  useEffect(() => {
    if (project && location && type) {
      setName(`${project!.shortName}-${location}-${type}`)
      setDescription(`${name} is the ${type} environnement at ${location} for the project ${project.name}`)
    }
  }, [project, location, type])

  const validForm = () => {
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
      projectUuid: project.uuid,
      name: name,
      description: description,
      type: type,
      location: location,
      status: status,
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

  return (
    <>
      <PageTitle title="Environments" />
      <section className="mt-4">
        <h3 className="text-lg font-medium mb-2">Environment creation</h3>
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
          <VersionInput label="Version" name="version" value="" placeholder="Version of the business service" onChange={() => {}} />
          <TextInput label="JiraTicket" name="jiraTicket" value={name} placeholder="Number of the JiraTicket this environment setup" onChange={() => {}} />
          <SelectInput label="Location" name="location" value={location} placeholder="Select a location" onChange={(e) => setLocation(e.target.value)} options={locations} />
          <SelectInput label="Status" name="status" value={status} placeholder="Select a status" onChange={(e) => setStatus(e.target.value)} options={states} />
        </FormSection>
        { isNewEnvironment && <ButtonInput name="create-environment" label="Create Environment" onClick={validForm} /> }
        { !isNewEnvironment && <ButtonInput name="update-environment" label="Update Environment" onClick={validForm} /> }
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Childs</h3>
        <ComingSoonComponent />
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Linked items</h3>
        <ComingSoonComponent />
      </section>
    </>
  )
}

export default EnvironmentDetaillsPage